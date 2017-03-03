import React, { Component } from 'react';
import { connect } from 'react-redux';

// components
import CssLoad from 'components/Common/cssLoad';
import Pagination from 'components/Common/Pagination';
import GridHead from 'GridHead';
import GridRow from 'GridRow';

// actions
import {
  cleanProfileGrid,
  updateItemsPerPage,
  setCurrentPageNumber,
  setTotalPages,
  setTotalItems,
  setColumnsHeader,
  setCurrentRows,
  toggleFilter,
  sortColumn,
  cleanFilter
} from 'actions/viewProfiles';

// styles
import styles from './index.scss';

class ViewProfiles extends Component {
  componentDidMount() {
    const { dispatch } = this.props;
    const { filters, sorts } = this.props.viewProfiles;
    const columns = this.props.getFilterHeading();
    if (!this.props.common.preloadedProfileData) {
      this.getCurrentRowsPage({ filters, sorts }).then(response => dispatch(setCurrentRows(response.items)));
      this.onEditProfileButton = this.props.onEditProfileButton || (()=>{});
      dispatch(setColumnsHeader(columns));
    }
  }

  componentWillUnmount() {
    this.props.dispatch(cleanFilter());
  }

  queryFactory(query) {
    // Query format:
    // Sort:ASC:  /endpoint?sort="documentType.code"
    // Sort:DESC: /endpoint?sort="-documentType.code"
    // Filter:    /endpoint?filter=name="@INTERNET@"|documentType.code="@th@"
    // console.log('queryFactory:: ', query); // log of query param
    const sortQuery = query.sorts ? `sort=${query.sorts}` : '';
    const filterQuery = this.convertFilterToQueryString(query.filters);
    // query=val&query=val
    const amp = (sortQuery.length && filterQuery.length) ? '&' : '';
    const queryString = filterQuery+amp+sortQuery;
    return queryString.length > 0 ? `${queryString}&` : '';
  }

  convertFilterToQueryString(query) {
    const { columns } = this.props.viewProfiles;
    const keysArr = Object.keys(query);
    const filterKey = 'filter=';
    let queryString = filterKey;

    // Query format:
    // query used for strings: key="@value@"|key="@value@"
    // query used for numbers: key=number
    keysArr.forEach((key, i) => {
      const currFilter = columns.filter(filter => filter.key === key)[0];
      const filterParam = currFilter.query || key;
      const keyFormat = (currFilter.type !== 'number') ? `"@${query[key]}@"` : query[key];
      const pipe = (keysArr.length !== i+1) ? '|' : '';
      if (query[key]) {
        queryString += `${filterParam}=${keyFormat}${pipe}`;
      }
    });
    return queryString !== filterKey ? queryString : '';
    // console.log('queryString:: ', queryString); // queryString example
  }

  getPagination(pageNumber = 0, itemsPerPage = this.props.viewProfiles.itemsPerPage) {
    this.props.dispatch(setCurrentPageNumber(pageNumber));
    return `page=${pageNumber + 1}&size=${itemsPerPage}`;
  }

  getCurrentRowsPage = query => {
    const queryString = this.queryFactory(query) +
                        this.getPagination(query.currentPageNumber, query.itemsPerPage);
    // queryString format: &page=1&size=50
    // console.log('getCurrentRowsPage::querystraingFormat: ', queryString); // queryString example

    return this.props.getFilterRows(queryString).then(response => {
      const { dispatch } = this.props;
      const totalPages = this.getTotalPages(response.totalItems, response.itemsPerPage);
      dispatch(setTotalItems(response.totalItems));
      dispatch(setTotalPages(totalPages));
      return response;
    });
  }

  getTotalPages = (totalRows, itemsPerPage) => {
    return Math.ceil(totalRows / itemsPerPage);
  }

  onPageChanged = currentPageNumber => {
    const { filters, sorts } = this.props.viewProfiles;
    this.getCurrentRowsPage({ filters, sorts, currentPageNumber }).then(response =>
      this.props.dispatch(setCurrentRows(response.items)
    ));
  }

  onItemsPerPageChange = e => {
    const val = e.target.value;
    const { dispatch } = this.props;
    const { sorts, filters } = this.props.viewProfiles;

    dispatch(updateItemsPerPage(Number(val)));

    this.getCurrentRowsPage({ filters, sorts, itemsPerPage: val }).then(response => {
      const totalPages = this.getTotalPages(response.totalItems, val);
      dispatch(setTotalPages(totalPages));
      dispatch(setCurrentRows(response.items));
    });
  }

  onSortColumn = e => {
    const { dispatch } = this.props;
    const column = e.target.dataset.sortcol || e.target.dataset.filter || e.target.id;
    const currSort = this.props.viewProfiles.sorts;
    let sorts = '';

    if (currSort === column) {
      sorts = `-${column}`; // Sort ASC
    }
    if (currSort.indexOf(column) < 0 || currSort === '') {
      sorts = column; // sort DESC
    }

    const { itemsPerPage, currentPageNumber, filters } = this.props.viewProfiles;
    this.getCurrentRowsPage({ itemsPerPage, currentPageNumber, filters, sorts }).then(response => {
      dispatch(sortColumn(sorts));
      dispatch(setCurrentRows(response.items));
    });
    e.preventDefault();
  }

  renderFilterButton = () => {
    const { dispatch } = this.props;
    return <a href="#" title="Filter" className="glyphicon glyphicon-search cta-filter" onClick={e => {
      dispatch(toggleFilter(!this.props.viewProfiles.isFilterActive));
      $(".filter-field").val('');
      e.preventDefault();
    }}></a>
  }

  renderFilterLoading() {
    return (
      <tbody>
        <tr className="grid-row">
          <td colSpan={this.props.viewProfiles.columns.length} style={{padding: 0}}>
            <CssLoad style={{marginTop: 0, paddingTop: 1}} />
          </td>
        </tr>
      </tbody>
    )
  }

  render() {
    const {
      sorts,
      columns,
      totalPages,
      totalItems,
      currentRows,
      itemsPerPage,
      dropdownItems,
      isFilterActive,
      currentPageNumber
    } = this.props.viewProfiles;

    return(
      <div className="row">
        <div className={styles.viewProfiles + " row"}>
          <div className="col-xs-12 table-responsive">
            <table className={styles.filterGrid + " table table-bordered table-striped table-hover"}>
              <GridHead
                columns={columns}
                sortOrientation={sorts}
                isFilterActive={isFilterActive}
                onSortColumn={this.onSortColumn}
                renderFilterButton={this.renderFilterButton}
                getCurrentRowsPage={this.getCurrentRowsPage} />
              { this.props.isFetchingRows &&
                !this.props.common.preloadedProfileData &&
                this.renderFilterLoading()
              }
              <GridRow
                thead={columns}
                rows={currentRows}
                isFetching={this.props.isFetchingRows} />
              { this.props.isFetchingRows &&
                !this.props.common.preloadedProfileData &&
                this.props.viewProfiles.currentRows.length > 15 &&
                this.renderFilterLoading()
              }
            </table>
          </div>
          <Pagination
            totalItems={totalItems}
            totalPages={totalPages}
            itemsPerPage={itemsPerPage}
            dropdownItems={dropdownItems}
            currentPage={currentPageNumber}
            onPageChanged={this.onPageChanged}
            onItemsPerPageChange={this.onItemsPerPageChange} />
        </div>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    viewProfiles: state.viewProfiles,
    common: state.common
  };
}

export default connect(mapStateToProps)(ViewProfiles);
