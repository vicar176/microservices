import _objectWithoutProperties from 'babel-runtime/helpers/objectWithoutProperties';
import _extends from 'babel-runtime/helpers/extends';
import _Object$getPrototypeOf from 'babel-runtime/core-js/object/get-prototype-of';
import _classCallCheck from 'babel-runtime/helpers/classCallCheck';
import _createClass from 'babel-runtime/helpers/createClass';
import _possibleConstructorReturn from 'babel-runtime/helpers/possibleConstructorReturn';
import _inherits from 'babel-runtime/helpers/inherits';
import Grid from '../Grid';
import React, { Component, PropTypes } from 'react';
import cn from 'classnames';
import shallowCompare from 'react-addons-shallow-compare';

/**
 * It is inefficient to create and manage a large list of DOM elements within a scrolling container
 * if only a few of those elements are visible. The primary purpose of this component is to improve
 * performance by only rendering the DOM nodes that a user is able to see based on their current
 * scroll position.
 *
 * This component renders a virtualized list of elements with either fixed or dynamic heights.
 */

var List = function (_Component) {
  _inherits(List, _Component);

  function List(props, context) {
    _classCallCheck(this, List);

    var _this = _possibleConstructorReturn(this, (List.__proto__ || _Object$getPrototypeOf(List)).call(this, props, context));

    _this._cellRenderer = _this._cellRenderer.bind(_this);
    _this._onScroll = _this._onScroll.bind(_this);
    _this._onSectionRendered = _this._onSectionRendered.bind(_this);
    return _this;
  }

  _createClass(List, [{
    key: 'forceUpdateGrid',
    value: function forceUpdateGrid() {
      this.Grid.forceUpdate();
    }

    /** See Grid#measureAllCells */

  }, {
    key: 'measureAllRows',
    value: function measureAllRows() {
      this.Grid.measureAllCells();
    }

    /** See Grid#recomputeGridSize */

  }, {
    key: 'recomputeRowHeights',
    value: function recomputeRowHeights() {
      var index = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 0;

      this.Grid.recomputeGridSize({
        rowIndex: index
      });
      this.forceUpdateGrid();
    }
  }, {
    key: 'render',
    value: function render() {
      var _this2 = this;

      var _props = this.props;
      var className = _props.className;
      var noRowsRenderer = _props.noRowsRenderer;
      var scrollToIndex = _props.scrollToIndex;
      var width = _props.width;


      var classNames = cn('ReactVirtualized__List', className);

      return React.createElement(Grid, _extends({}, this.props, {
        autoContainerWidth: true,
        cellRenderer: this._cellRenderer,
        className: classNames,
        columnWidth: width,
        columnCount: 1,
        noContentRenderer: noRowsRenderer,
        onScroll: this._onScroll,
        onSectionRendered: this._onSectionRendered,
        ref: function ref(_ref) {
          _this2.Grid = _ref;
        },
        scrollToRow: scrollToIndex
      }));
    }
  }, {
    key: 'shouldComponentUpdate',
    value: function shouldComponentUpdate(nextProps, nextState) {
      return shallowCompare(this, nextProps, nextState);
    }
  }, {
    key: '_cellRenderer',
    value: function _cellRenderer(_ref2) {
      var rowIndex = _ref2.rowIndex;
      var style = _ref2.style;

      var rest = _objectWithoutProperties(_ref2, ['rowIndex', 'style']);

      var rowRenderer = this.props.rowRenderer;

      // By default, List cells should be 100% width.
      // This prevents them from flowing under a scrollbar (if present).

      style.width = '100%';

      return rowRenderer(_extends({
        index: rowIndex,
        style: style
      }, rest));
    }
  }, {
    key: '_onScroll',
    value: function _onScroll(_ref3) {
      var clientHeight = _ref3.clientHeight;
      var scrollHeight = _ref3.scrollHeight;
      var scrollTop = _ref3.scrollTop;
      var onScroll = this.props.onScroll;


      onScroll({ clientHeight: clientHeight, scrollHeight: scrollHeight, scrollTop: scrollTop });
    }
  }, {
    key: '_onSectionRendered',
    value: function _onSectionRendered(_ref4) {
      var rowOverscanStartIndex = _ref4.rowOverscanStartIndex;
      var rowOverscanStopIndex = _ref4.rowOverscanStopIndex;
      var rowStartIndex = _ref4.rowStartIndex;
      var rowStopIndex = _ref4.rowStopIndex;
      var onRowsRendered = this.props.onRowsRendered;


      onRowsRendered({
        overscanStartIndex: rowOverscanStartIndex,
        overscanStopIndex: rowOverscanStopIndex,
        startIndex: rowStartIndex,
        stopIndex: rowStopIndex
      });
    }
  }]);

  return List;
}(Component);

List.defaultProps = {
  estimatedRowSize: 30,
  noRowsRenderer: function noRowsRenderer() {
    return null;
  },
  onRowsRendered: function onRowsRendered() {
    return null;
  },
  onScroll: function onScroll() {
    return null;
  },
  overscanRowCount: 10,
  scrollToAlignment: 'auto',
  style: {}
};
export default List;
process.env.NODE_ENV !== "production" ? List.propTypes = {
  'aria-label': PropTypes.string,

  /**
   * Removes fixed height from the scrollingContainer so that the total height
   * of rows can stretch the window. Intended for use with WindowScroller
   */
  autoHeight: PropTypes.bool,

  /** Optional CSS class name */
  className: PropTypes.string,

  /**
   * Used to estimate the total height of a List before all of its rows have actually been measured.
   * The estimated total height is adjusted as rows are rendered.
   */
  estimatedRowSize: PropTypes.number.isRequired,

  /** Height constraint for list (determines how many actual rows are rendered) */
  height: PropTypes.number.isRequired,

  /** Optional renderer to be used in place of rows when rowCount is 0 */
  noRowsRenderer: PropTypes.func.isRequired,

  /**
   * Callback invoked with information about the slice of rows that were just rendered.
   * ({ startIndex, stopIndex }): void
   */
  onRowsRendered: PropTypes.func.isRequired,

  /**
   * Number of rows to render above/below the visible bounds of the list.
   * These rows can help for smoother scrolling on touch devices.
   */
  overscanRowCount: PropTypes.number.isRequired,

  /**
   * Callback invoked whenever the scroll offset changes within the inner scrollable region.
   * This callback can be used to sync scrolling between lists, tables, or grids.
   * ({ clientHeight, scrollHeight, scrollTop }): void
   */
  onScroll: PropTypes.func.isRequired,

  /**
   * Either a fixed row height (number) or a function that returns the height of a row given its index.
   * ({ index: number }): number
   */
  rowHeight: PropTypes.oneOfType([PropTypes.number, PropTypes.func]).isRequired,

  /** Responsible for rendering a row given an index; ({ index: number }): node */
  rowRenderer: PropTypes.func.isRequired,

  /** Number of rows in list. */
  rowCount: PropTypes.number.isRequired,

  /** See Grid#scrollToAlignment */
  scrollToAlignment: PropTypes.oneOf(['auto', 'end', 'start', 'center']).isRequired,

  /** Row index to ensure visible (by forcefully scrolling if necessary) */
  scrollToIndex: PropTypes.number,

  /** Vertical offset. */
  scrollTop: PropTypes.number,

  /** Optional inline style */
  style: PropTypes.object,

  /** Tab index for focus */
  tabIndex: PropTypes.number,

  /** Width of list */
  width: PropTypes.number.isRequired
} : void 0;