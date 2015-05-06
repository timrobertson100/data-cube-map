package io.mapped;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.mapped.provider.DatacubeTileProvider;
import io.mapped.resource.TileResource;

/**
 * The main entry point for running the member node.
 */
public class TileServerApplication extends Application<TileServerConfiguration> {

  private static final String APPLICATION_NAME = "Datacube Tile Server";

  public static void main(String[] args) throws Exception {
    new TileServerApplication().run(args);
  }

  @Override
  public String getName() {
    return APPLICATION_NAME;
  }

  @Override
  public final void initialize(Bootstrap<TileServerConfiguration> bootstrap) {
  }

  @Override
  public final void run(TileServerConfiguration configuration, Environment environment) {
    environment.jersey().register(new TileResource());
    environment.jersey().register(new DatacubeTileProvider());
  }
}
