package io.mapped.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import io.mapped.proto.DatacubeTileProto;

/**
 * A utility to rewrite a CSV of lat,lng,count into a collection of tiles.
 */
public class CsvToProto {
  private static final Pattern SPLITTER = Pattern.compile("\t");
  /**
   * Expects inpath outpath numZooms
   */
  public static void main(String[] args) throws IOException {
    int zooms = Integer.parseInt(args[2]);
    Map<Key, TileBuilder> tiles = Maps.newHashMap();
    try (
      BufferedReader in = new BufferedReader(
        new InputStreamReader(new GZIPInputStream(new FileInputStream(new File(args[0])))))
      ) {
      String line = in.readLine();
      int rowCount=0;
      while (line!=null) {

        String[] fields = SPLITTER.split(line);
        // 0 is kingdom
        double lat = Double.parseDouble(fields[1]);
        double lng = Double.parseDouble(fields[2]);
        int count = Integer.parseInt(fields[3]);

        for (int zoom=0; zoom<zooms; zoom++) {

          if (MercatorProjectionUtil.isPlottable(lat,lng)) {
            int x = MercatorProjectionUtil.toTileX(lng, zoom);
            int y = MercatorProjectionUtil.toTileY(lat, zoom);

            Key key = new Key(x,y,zoom);
            TileBuilder b = tiles.get(key);
            if (b == null) {
              b = new TileBuilder();
              tiles.put(key, b);
            }
            b.collect(zoom, lat, lng, count);
          }
        }

        if (++rowCount %10000 == 0) {
          System.out.println(rowCount);
        }

        line = in.readLine();
      }
    }

    for (Map.Entry<Key, TileBuilder> e : tiles.entrySet()) {
      OutputStream os = new GZIPOutputStream(new FileOutputStream("/tmp/" + e.getKey().getZ()
                                             + "-" + e.getKey().getX() + "-" + e.getKey().getY() + ".pbf.gz"));
      os.write(e.getValue().build().toByteArray());
      os.close();
    }
  }

  private static final class Key {
    private final int x;
    private final int y;
    private final int z;


    private Key(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Key that = (Key) o;

      return Objects.equal(this.x, that.x) &&
             Objects.equal(this.y, that.y) &&
             Objects.equal(this.z, that.z);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(x, y, z);
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
    }

    public int getZ() {
      return z;
    }
  }
}
