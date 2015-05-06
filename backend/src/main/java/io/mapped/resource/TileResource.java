package io.mapped.resource;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;
import com.sun.jersey.spi.resource.Singleton;
import io.mapped.proto.DatacubeTileProto.DatacubeTile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple resource that returns a demo tile.
 */
@Path("/")
@Singleton
public final class TileResource {

  private static final Logger LOG = LoggerFactory.getLogger(TileResource.class);

  private static final int TILE_SIZE = 256;
  private static final Random RANDOM = new Random();
  private static final DatacubeTile DEMO_TILE;

  /**
   * Return a demo tile always (for now)
   */
  static {
    DatacubeTile.Builder builder = DatacubeTile.newBuilder();
    int count = 0;
    int numberChannels = 10;

    DatacubeTile.Channel.Builder[] channels =  new DatacubeTile.Channel.Builder[10];
    for (int i=0; i<numberChannels; i++) {
      channels[i] = DatacubeTile.Channel.newBuilder();
    }

    for (int x = 0; x < TILE_SIZE; x += 1) {
      for (int y = 0; y < TILE_SIZE; y += 1) {
        builder.addPixels(x);
        builder.addPixels(y);

        // 2 channels of data, randomly assigning 0 or 1
        for (int i=0; i<numberChannels; i++) {
          channels[i].addValue(RANDOM.nextInt(2));
        }
      }
    }
    for (int i=0; i<numberChannels; i++) {
      builder.addChannel(channels[i].build());
    }
    DEMO_TILE = builder.build();
  }

  @GET
  @Path("{z}/{x}/{y}/datacube.pbf")
  @Timed
  @Produces("application/x-protobuf")
  public DatacubeTile demo(@PathParam("z") int z, @PathParam("x") int x, @PathParam("y") int y) {
    return DEMO_TILE;
  }

  // TODO: research why mapbox are doing this manually... should we be?
  /*
  static int zigZagEncode(int n) {
    // https://developers.google.com/protocol-buffers/docs/encoding#types
    return (n << 1) ^ (n >> 31);
  }
  */
}
