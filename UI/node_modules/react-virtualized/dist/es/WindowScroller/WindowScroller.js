import _Object$getPrototypeOf from 'babel-runtime/core-js/object/get-prototype-of';
import _classCallCheck from 'babel-runtime/helpers/classCallCheck';
import _createClass from 'babel-runtime/helpers/createClass';
import _possibleConstructorReturn from 'babel-runtime/helpers/possibleConstructorReturn';
import _inherits from 'babel-runtime/helpers/inherits';
import { Component, PropTypes } from 'react';
import ReactDOM from 'react-dom';
import shallowCompare from 'react-addons-shallow-compare';
import { registerScrollListener, unregisterScrollListener } from './utils/onScroll';

var WindowScroller = function (_Component) {
  _inherits(WindowScroller, _Component);

  function WindowScroller(props) {
    _classCallCheck(this, WindowScroller);

    var _this = _possibleConstructorReturn(this, (WindowScroller.__proto__ || _Object$getPrototypeOf(WindowScroller)).call(this, props));

    var height = typeof window !== 'undefined' ? window.innerHeight : 0;

    _this.state = {
      isScrolling: false,
      height: height,
      scrollTop: 0
    };

    _this._onScrollWindow = _this._onScrollWindow.bind(_this);
    _this._onResizeWindow = _this._onResizeWindow.bind(_this);
    _this._enablePointerEventsAfterDelayCallback = _this._enablePointerEventsAfterDelayCallback.bind(_this);
    return _this;
  }

  _createClass(WindowScroller, [{
    key: 'updatePosition',
    value: function updatePosition() {
      // Subtract documentElement top to handle edge-case where a user is navigating back (history) from an already-scrolled bage.
      // In this case the body's top position will be a negative number and this element's top will be increased (by that amount).
      this._positionFromTop = ReactDOM.findDOMNode(this).getBoundingClientRect().top - document.documentElement.getBoundingClientRect().top;
    }
  }, {
    key: 'componentDidMount',
    value: function componentDidMount() {
      var height = this.state.height;


      this.updatePosition();

      if (height !== window.innerHeight) {
        this.setState({
          height: window.innerHeight
        });
      }

      registerScrollListener(this);

      window.addEventListener('resize', this._onResizeWindow, false);
    }
  }, {
    key: 'componentWillUnmount',
    value: function componentWillUnmount() {
      unregisterScrollListener(this);

      window.removeEventListener('resize', this._onResizeWindow, false);
    }
  }, {
    key: 'render',
    value: function render() {
      var children = this.props.children;
      var _state = this.state;
      var isScrolling = _state.isScrolling;
      var scrollTop = _state.scrollTop;
      var height = _state.height;


      return children({
        height: height,
        isScrolling: isScrolling,
        scrollTop: scrollTop
      });
    }
  }, {
    key: 'shouldComponentUpdate',
    value: function shouldComponentUpdate(nextProps, nextState) {
      return shallowCompare(this, nextProps, nextState);
    }
  }, {
    key: '_enablePointerEventsAfterDelayCallback',
    value: function _enablePointerEventsAfterDelayCallback() {
      this.setState({
        isScrolling: false
      });
    }
  }, {
    key: '_onResizeWindow',
    value: function _onResizeWindow(event) {
      var onResize = this.props.onResize;


      this.updatePosition();

      var height = window.innerHeight || 0;

      this.setState({ height: height });

      onResize({ height: height });
    }
  }, {
    key: '_onScrollWindow',
    value: function _onScrollWindow(event) {
      var onScroll = this.props.onScroll;

      // In IE10+ scrollY is undefined, so we replace that with the latter

      var scrollY = 'scrollY' in window ? window.scrollY : document.documentElement.scrollTop;

      var scrollTop = Math.max(0, scrollY - this._positionFromTop);

      this.setState({
        isScrolling: true,
        scrollTop: scrollTop
      });

      onScroll({ scrollTop: scrollTop });
    }
  }]);

  return WindowScroller;
}(Component);

WindowScroller.defaultProps = {
  onResize: function onResize() {},
  onScroll: function onScroll() {}
};
export default WindowScroller;
process.env.NODE_ENV !== "production" ? WindowScroller.propTypes = {
  /**
   * Function responsible for rendering children.
   * This function should implement the following signature:
   * ({ height, scrollTop }) => PropTypes.element
   */
  children: PropTypes.func.isRequired,

  /** Callback to be invoked on-resize: ({ height }) */
  onResize: PropTypes.func.isRequired,

  /** Callback to be invoked on-scroll: ({ scrollTop }) */
  onScroll: PropTypes.func.isRequired
} : void 0;