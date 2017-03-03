var webpack = require('webpack');
var config = require('./webpack.config.local.js');
var webpackDevMiddleware = require('webpack-dev-middleware');
var webpackHotMiddleware = require('webpack-hot-middleware');

var app = require('express')();
var port = 3000;

var compiler = webpack(config);

app.use(webpackDevMiddleware(compiler, { stats: { colors: true }, noInfo: true, publicPath: config.output.publicPath }));
app.use(webpackHotMiddleware(compiler))

app.get('*', function(req, res) {
  res.sendFile(__dirname + '/index.html');
});

app.listen(port, function(error) {
  if (error) {
    console.error(error);
  } else {
    console.info("Listening on port %s. Open up http://localhost:%s/ in your browser.", port, port);
  }
});
