import React, { Component } from 'react';
import { connect } from 'react-redux';
import { setFilter, setCurrentRows, cleanFilterColumn } from 'actions/viewProfiles';

// components
import FilterField from 'FilterField';
import FilterButtons from 'FilterButtons';
// actions
import { cleanFilter } from 'actions/viewProfiles';
import { lastUrlHistory } from 'actions/common';
// styles
import styles from '../index.scss';

class GridFilter extends Component {
  constructor(props) {
    super(props);
  }

  componentDidMount() {
    // TODO
    // Need to refresh filters when page change. This will be fixed using internal state
    // this.props.dispatch(lastUrlHistory(location.hash));
    // browserHistory.listen( location => {
    //   if (this.props.common.lastUrlHistory !== location.hash ) {
    //     this.onCleanAllFilters();
    //   }
    // });
  }

  getFilteredRows = () => {
    const { dispatch, getCurrentRowsPage } = this.props;
    const { filters, sorts } = this.props.viewProfiles;
    getCurrentRowsPage({ filters, sorts }).then(response =>
      dispatch(setCurrentRows(response.items)
    ));
  }

  onFilter = () => {
    const { filters } = this.props.viewProfiles;
    if (Object.keys(filters).length) {
      this.getFilteredRows();
    }
  }

  onSetFilterValue = (e, field) => {
    const val = e.target.value;
    this.props.dispatch(setFilter({ [e.target.dataset.filter]: val }));
    // Filter when user press enter
    if (e.keyCode === 13) {
      setTimeout(() => this.getFilteredRows(), 100);
    }
    // Clean when field is empty, backspace key
    if (val.trim() === "" && e.keyCode === 8 || val.trim() === "" && e.keyCode === 32) {
      this.onCleanFilterField(field);
    }
  }

  onSetFilterDateValue = (val, field) => {
    this.props.dispatch(setFilter({ [field]: val.format("YYYY-MM-DD") }));
  }

  onFieldBlur = e => {
    const val = e.target.value;
    const key = e.target.dataset.filter;
    const { dispatch } = this.props;
    if (val && (val !== this.props.viewProfiles.filters[key])) {
      dispatch(setFilter({ [key]: val }));
    }
  }

  onCleanAllFilters = () => {
    $(".filter-field").val('');
    this.props.dispatch(cleanFilter());
    setTimeout(() => this.getFilteredRows() , 100);
  }

  onCleanFilterField = (field, fieldType) => {
    if (fieldType === "date") {
      document.getElementById(`${field}-field`).placeholder = '';
    } else {
      document.getElementById(`${field}-field`).value = '';
    }
    this.props.dispatch(cleanFilterColumn(field));
    setTimeout(() => this.getFilteredRows(), 100);
  }

  render() {
    const { filters } = this.props.viewProfiles;
    const { columns, isShow } = this.props;
    const columnFields = columns.filter(col => col.filterable === true);

    return (
      <tr className={styles.filterControls + (!isShow ? ' hide' : '')}>
        { columnFields.map((col, i) =>
          <FilterField
            key={i}
            col={col}
            currFilter={filters[col.key]}
            onFieldBlur={this.onFieldBlur}
            onSetFilterValue={this.onSetFilterValue}
            onSetFilterDateValue={this.onSetFilterDateValue}
            onCleanFilterField={this.onCleanFilterField}
            onCleanFilterDate={this.onCleanFilterDate} />
        )}
        <FilterButtons
          onFilter={this.onFilter}
          onCleanAllFilters={this.onCleanAllFilters}
          colSpan={columns.length - columnFields.length} />
      </tr>
    )
  }
}

function mapStateToProps(state) {
  return {
    viewProfiles: state.viewProfiles,
    common: state.common
  };
}

export default connect(mapStateToProps)(GridFilter);
