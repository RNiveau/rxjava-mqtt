'use strict';

var http = require('http');

var server = http.createServer(function (request, response) {
  console.log('Received request:' + request.url);
  if (request.url === '/login') {
    setTimeout(function () {
      response.end("{'status':'logged'}");
    }, 1000);
  } else if (request.url === '/profile') {
    setTimeout(function () {
      response.end("{'user':'toto'}");
    }, 1000);
  } else {
    response.statusCode = 404;
    response.end();
  }
});
server.listen(3000);
