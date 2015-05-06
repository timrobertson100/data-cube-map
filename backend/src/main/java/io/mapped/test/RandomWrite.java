package io.mapped.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.zip.GZIPOutputStream;

/**
 * Daft test to basically see how big binary would be.
 * This writes 256 tiles, meaning 256x256 bytes for coords, and then a channel of data (one INT per count).
 * It produces both uncompressed and compressed tiles to compare.
 *
 * Summary:
 * - relative referencing (i.e. moveTo) looks like it improves compression significantly (can come down to 2%)
 * - 64KB can compress to 1.3KB
 *
 */
public class RandomWrite {

  public static void main(String[] args) throws IOException {

    FileOutputStream os = new FileOutputStream("/tmp/tile.bin");
    FileOutputStream os2 = new FileOutputStream("/tmp/tile.bin.gz");
    BufferedOutputStream out = new BufferedOutputStream(os);
    BufferedOutputStream out2 = new BufferedOutputStream(new GZIPOutputStream(os2));
    int size=256 * 70 / 100; // 70% coverage
    SecureRandom r = new SecureRandom();
    for (int x=0; x<size; x++) {
      for (int y=0; y<size; y++) {
        // simulate relative coords, not absolute
        if (y==0) {
          // inflated
          out.write(Integer.valueOf(-256).byteValue());
          out.write(Integer.valueOf(1).byteValue());

          // Gzipped
          out2.write(Integer.valueOf(-256).byteValue());
          out2.write(Integer.valueOf(1).byteValue());

        } else {
          // inflated
          out.write(Integer.valueOf(1).byteValue());
          out.write(Integer.valueOf(0).byteValue());

          // Gzipped
          out2.write(Integer.valueOf(1).byteValue());
          out2.write(Integer.valueOf(0).byteValue());

        }
      }
    }

    // write a channel of INT (32 bits) per pixel (e.g. 4,294,967,296)
    for (int i=0; i<size; i++) {
      out.write(Integer.valueOf(r.nextInt()).byteValue());
      out.write(Integer.valueOf(r.nextInt()).byteValue());
      out.write(Integer.valueOf(r.nextInt()).byteValue());
      out.write(Integer.valueOf(r.nextInt()).byteValue());

      out2.write(Integer.valueOf(r.nextInt()).byteValue());
      out2.write(Integer.valueOf(r.nextInt()).byteValue());
      out2.write(Integer.valueOf(r.nextInt()).byteValue());
      out2.write(Integer.valueOf(r.nextInt()).byteValue());
    }

    out.close();
    out2.close();
    System.out.println(new File("/tmp/tile.bin").length() + "b");
    System.out.println(new File("/tmp/tile.bin.gz").length() + "b");
    System.out.println(Math.ceil((float)new File("/tmp/tile.bin.gz").length() / new File("/tmp/tile.bin").length() * 100) + "%");
  }
}
