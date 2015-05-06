'use strict';

var Protobuf = require("Pbf");

module.exports = DatacubeTile;

/**
 * A builder of DatacubeTile from a protobuffer.
 * This is tied to the protobuf schema which is assumed to be stable.
 */
function DatacubeTile(buffer) {
  var self = this;
  self.channel = [];
  var protobuf = new Protobuf(new Uint8Array(buffer));
  protobuf.readFields(readTile, self);
}

/**
 * Builds the tile from the protobuf buffer.
 */
function readTile(tag, tile, pbf) {
  if (tag === 1) {
    tile.pixels = readPixels(pbf);
  } else if (tag === 2) {
    tile.channel.push(readChannelMessage(pbf));
  }
}

/**
 * Reads the pixels and returns them as an array of Points.
 */
function readPixels(pbf) {
  var bytes = pbf.readVarint(), // first bytes holds length
      posEnd = pbf.pos + bytes,
      pixels = [];

  while (pbf.pos < posEnd) {
    var x = pbf.readVarint();
    var y = pbf.readVarint();
    pixels.push(new Point(x,y));
  }
  return pixels;
}

function readChannelMessage(pbf) {
  var channel = null,
    bytes = pbf.readVarint(),
    end = pbf.pos + bytes;

  while (pbf.pos < end) {
    var tag = pbf.readVarint() >> 3;
    channel = tag === 1 ? readChannel(pbf) : null;
  }
  return channel;
}

/**
 * Reads the channel data and returns them as an array of ints.
 */
function readChannel(pbf) {
  var bytes = pbf.readVarint(),  // first byte holds length
      posEnd = pbf.pos + bytes,
      channel = [];
  while (pbf.pos < posEnd) {
    var data = pbf.readVarint();
    channel.push(data);
  }
  return channel;
}

/**
 * Utility container
 */
function Point(x, y) {
  this.x = x;
  this.y = y;
}
