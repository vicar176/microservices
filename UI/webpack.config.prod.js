var webpack = require('webpack');
var config = require('./webpack.config.base');
var WebpackStrip = require('strip-loader');
var path = require('path');

config.output.path = path.resolve('builds');

config.module.loaders.push(

  // babel loader
  {
    test: /\.js$/,
    exclude: /node_modules/,
    loader: 'babel-loader',
    query: {
      presets: ['es2015','stage-0',  'react']
    }
  },

  // strip-loader
  {
    test: [/\.js?$/],
    exclude: '/node_modules/',
    loader: WebpackStrip.loader('console')
  }

);

config.plugins.push(
    new webpack.optimize.UglifyJsPlugin({
      compress: {
        warnings: false
      }
  })
);

config.plugins.push(
  new webpack.DefinePlugin({
    'process.env': {
      'NODE_ENV': JSON.stringify('production')
    }
  })
);

module.exports = config;
