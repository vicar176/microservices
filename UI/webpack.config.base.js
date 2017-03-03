var webpack = require('webpack');
var HtmlWebpackPlugin = require('html-webpack-plugin');
var path = require('path');
var ExtractTextPlugin = require('extract-text-webpack-plugin');

// multiple extract instances
var extractCSS = new ExtractTextPlugin('libs.css', { allChunks: true });
var extractSASS = new ExtractTextPlugin('app.css', { allChunks: true });

var AUTOPREFIXER_BROWSERS = [
  'Chrome >= 45',
  'Firefox >= 42',
  'Explorer >= 9',
];

var baseConfig = {

  entry: ['./index.js', './styles/app.scss'],

  output: {
    filename: 'app.js',
    publicPath: '',
    hash: true
  },

  module: {

    loaders: [

      // scss loader
      {
        test: /\.scss$/,
        exclude: /node_modules/,
        loader: extractSASS.extract(['css-loader?modules&importLoaders=1&localIdentName=[name]__[local]___[hash:base64:5]', 'postcss-loader', 'sass-loader'])
      },

      // css loader
      {
        test: /\.css$/,
        loader: extractCSS.extract('style-loader', 'css-loader')
      },

      // file loader
      {
        test: /\.(png|jpg)$/,
        loader: 'file-loader?name=images/[name].[ext]'
      },

      // file loader
      // {
      //   test: /\.pdf$/,
      //   loader: 'file-loader?name=Demo/[name].[ext]'
      // },

      // jQuery loaders
      { test: /jquery\.js$/, loader: 'expose?$' },
      { test: /jquery\.js$/, loader: 'expose?jQuery' },

      // Fonts
      {test: /\.(woff|woff2)(\?v=\d+\.\d+\.\d+)?$/, loader: 'url?limit=10000&mimetype=application/font-woff'},
      {test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/, loader: 'url?limit=10000&mimetype=application/octet-stream'},
      {test: /\.eot(\?v=\d+\.\d+\.\d+)?$/, loader: 'file'},
      {test: /\.svg(\?v=\d+\.\d+\.\d+)?$/, loader: 'url?limit=10000&mimetype=image/svg+xml'}
    ]

  },

  postcss: function (bundler) {
    return [
      require('autoprefixer')({ browsers: AUTOPREFIXER_BROWSERS }),
    ];
  },

  resolve: {
    modulesDirectories: ['node_modules', './']
  },

  plugins: [
    extractCSS,
    extractSASS,
    new webpack.optimize.OccurenceOrderPlugin(),
    new webpack.NoErrorsPlugin(),
    new HtmlWebpackPlugin({
      title: "Everest",
      template: 'template.html',
      hash: true,
      favicon: 'favicon.png'
    }),
    new webpack.ProvidePlugin({
      '$': 'jquery',
      'jQuery': 'jquery',
      'window.jQuery': 'jquery',
      'jquery': 'imports?this=>global!exports?global.jquery!jquery',
      'Tether': 'tether',
      'window.Tether': 'tether'
    })
  ]

};

module.exports = baseConfig;
