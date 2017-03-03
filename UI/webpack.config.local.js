var webpack = require('webpack');
var config = require('./webpack.config.base');
var path = require('path');

config.entry.push('webpack-hot-middleware/client');

config.output.path = path.resolve('builds');

config.devtool = 'cheap-module-eval-source-map';

config.module.loaders.push(

  // babel loader
  {
    test: /\.js$/,
    exclude: /node_modules/,
    loader: 'babel-loader',
    query: {
      presets: ['es2015','stage-0', 'react'],
      plugins: [
        ['react-transform', {
          transforms: [{
            transform: 'react-transform-hmr',
            imports: ['react'],
            locals: ['module']
          },
          {
            transform: 'react-transform-catch-errors',
            imports: ['react', 'redbox-react']
          }]
        }]
      ]
    }
  }
);

config.plugins.push(
  new webpack.HotModuleReplacementPlugin()
);

config.plugins.push(
  new webpack.DefinePlugin({
    'process.env': {
      'NODE_ENV': JSON.stringify('local')
    }
  })
);

module.exports = config;
