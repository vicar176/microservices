import React, { Component } from 'react';
import { connect } from 'react-redux';
import moment from 'moment';

// components
import MainContent from 'components/Common/MainContent';
import ViewProfiles from 'components/ViewProfiles';

// styles
import styles from './index.scss';

// actions
import {
  updateIngestionState,
  fetchIngestionState,
  isIngestionShoutDown
} from 'actions/common';

import {
  fetchIngestionReprocessingList,
  reprocessDocuments
} from 'actions/ingestionReprocessing';

class IngestionReprocessing extends Component {
  constructor() {
    super();
    this.state = {
      query: "page=1&size=25"
    };
  }

  renderEditRowButton = () => {
    return <span>-</span>;
  }

  getFilterHeading = () => {
    return [
      {
        key: 'date',
        name: 'Date',
        sortable: true,
        filterable: true,
        type: "date",
        rowFormatter: this.renderDate,
        style: { "minWidth": 125 }
      },{
        key: 'documentId',
        name: 'Document Id',
        sortable: true,
        filterable: true
      },{
        key: 'step',
        name: 'Step',
        sortable: true,
        filterable: true
      },{
        key: 'count',
        name: 'Count',
        sortable: false,
        filterable: false
      },{
        key: 'description',
        name: 'Description',
        sortable: false,
        filterable: false
      },{
        key: 'configId',
        btnHead: 'filter',
        className: 'text-center',
        style: { 'width': 50 },
        rowFormatter: this.renderEditRowButton
      }
    ];
  }

  getFilterRows = (query = this.state.query) => {
    return this.props.dispatch(fetchIngestionReprocessingList(query)).then(response => {
      return {
        totalItems: response.totalItems,
        itemsPerPage: response.itemsPerPage,
        filters: query.filters,
        sorts: query.sorts,
        items: response.items
      }
    });
  }

  reprocessDocuments = (type = "all") => {
    this.props.dispatch(reprocessDocuments(type));
    this.getFilterRows();
  }

  renderDate = val => {
    return <span>{val.date && moment(val.date).utc().format('MMMM Do YYYY, hh:mm:ss a')}</span>
  }

  render() {
    const { selectedTab } = this.props.common;

    const {
      isFetchingIngestionReprocessing,
      lenghtOfDocumentsForReprocessing
    } = this.props.ingestionReprocessing;

    const {
      totalItems
    } = this.props.viewProfiles;

    const tabs = [
      { id: 'ingestionReprocessing', name: 'Ingestion Reprocessing' }
    ];

    return (
      <MainContent routes={this.props.routes} mainTitle='Exception Management' tabs={tabs}>
        <div role="tabpanel" className="tab-pane active" id="ingestionReprocessing">
          <h2 className="page-header">Ingestion Reprocessing</h2>
          <div className={styles.ingestionReprocessing}>
            <div className={"row "+ styles.bottonsHeader}>
              <div className="col-xs-12">
                <button className="pull-right btn btn-primary" disabled={!lenghtOfDocumentsForReprocessing && !totalItems} onClick={e => this.reprocessDocuments()}>Reprocess All Documents</button>
                <button className="pull-right btn btn-primary" disabled={!totalItems} onClick={e => this.reprocessDocuments("filtered")}>Reprocess Filtered Documents</button>
              </div>
            </div>
            <ViewProfiles
              getFilterHeading={this.getFilterHeading}
              getFilterRows={this.getFilterRows}
              isFetchingRows={isFetchingIngestionReprocessing}/>
          </div>
        </div>
      </MainContent>
    );
  }
}

function mapStateToProps(state) {
  return {
    common: state.common,
    ingestionReprocessing: state.ingestionReprocessing,
    viewProfiles: state.viewProfiles
  };
}

export default connect(mapStateToProps)(IngestionReprocessing);
