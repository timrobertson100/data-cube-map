window.$ = window.jQuery = require("jquery");
require('bootstrap/dist/css/bootstrap.css');
window.Link = require('link/Source/Web/link.js');
require('nouislider-browser');
require('nouislider-browser/jquery.nouislider.css');



var datacube = module.exports = window.datacube = {};
datacube.DatacubeTile = require("./datacubeTile.js");


/**
 * A utility to do ajax calls suitable for passing to a protobuf decoder.
 * TODO: move this into a utility module
 */
datacube.getArrayBuffer = function(url, callback) {
  var xhr = new XMLHttpRequest();
  xhr.open('GET', url, true);
  xhr.responseType = 'arraybuffer';
  xhr.onerror = function(e) {
    callback(e);
  };
  xhr.onload = function() {
    if (xhr.status >= 200 && xhr.status < 300 && xhr.response) {
      callback(null, xhr.response);
    } else {
      callback(new Error(xhr.statusText));
    }
  };
  xhr.send();
  return xhr;
};

