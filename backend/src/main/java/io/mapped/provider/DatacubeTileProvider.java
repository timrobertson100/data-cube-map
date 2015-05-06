package io.mapped.provider;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import io.mapped.proto.DatacubeTileProto;

@Produces("application/x-protobuf")
public class DatacubeTileProvider implements MessageBodyWriter<DatacubeTileProto.DatacubeTile> {

  @Override
  public boolean isWriteable(
    Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType
  ) {
    return type == DatacubeTileProto.DatacubeTile.class;
  }

  @Override
  public long getSize(
    DatacubeTileProto.DatacubeTile tile, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType
  ) {
    // TODO: highly inefficient to serialize 2 times
    return tile == null ? 0 : tile.toByteArray().length;
  }

  @Override
  public void writeTo(
    DatacubeTileProto.DatacubeTile tile, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
    MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream
  ) throws IOException, WebApplicationException {
    entityStream.write(tile.toByteArray());
  }
}
