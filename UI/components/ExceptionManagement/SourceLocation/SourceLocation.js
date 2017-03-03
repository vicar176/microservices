import React, { Component } from 'react';
import { connect } from 'react-redux';
import { hashHistory } from 'react-router';

// actions
import { preloadProfile } from 'actions/sourceProfile';
import { fetchProfileLog } from 'actions/sourceLocation';
import { editProfile } from 'actions/viewProfiles';

// Components
import CssLoad from 'components/Common/cssLoad';
import ViewProfiles from 'components/ViewProfiles';
import ConfirmPopup from 'components/Common/ConfirmPopup';
import ModalGridLog from 'ModalGridLog';

class SourceLocation extends Component {
  getFilterRows = query => {
    const response = {
      totalItems: 100,
      itemsPerPage: query.itemsPerPage,
      startIndex: query.currentPageNumber,
      filters: query.filters,
      sorts: query.sorts,
      items: []
    };

    for (let i = 0; i < response.itemsPerPage; i++) {
      response.items.push({
        id: response.startIndex * response.itemsPerPage + i + 1,
        profileName: ['Barclays Bank FTP', 'Capital One Media', 'Capital One STMT', 'Citibank FTP', 'Synchorny FTP'][Math.floor((Math.random() * 5))],
        accessType: 'FTP',
        successRun: ['05/01/201614:30:55', '05/02/2016 06:30:55', '08/08/2016 06:56:55'][Math.floor((Math.random() * 3))],
        fileRetrieved: Math.floor(Math.random() * (30000 - 100)),
        hostLocation: ['ftp.barclaybankmedia.com', 'ftp.capitalone.com/mcm/media', 'ftp.capitalone.com/mcm/stmt', 'ftp.citibank.mcm.com', 'ftp.synchrony.mcm.com'][Math.floor((Math.random() * 5))],
        version: ['Version 1', 'Version 2', 'Version 3', 'Version 4'][Math.floor((Math.random() * 4))],
        configId: i
      });
    }
    return response;
  }

  getFilterHeading = () => {
    return [
      { key: 'id', name: 'ID', className: 'text-center' },
      { key: 'profileName', name: 'Profile Name', sortable: true, filterable: true },
      { key: 'successRun', name: 'Last Success Run', sortable: true, filterable: true },
      { key: 'fileRetrieved', name: 'Files Retrieved', sortable: true, filterable: true },
      { key: 'accessType', name: 'Access Type', sortable: true, filterable: true },
      { key: 'hostLocation', name: 'Host Location', sortable: true, filterable: true },
      { key: 'version', name: 'Version #', btnFilter: 'filter', className: 'text-center' }
    ];
  }

  onEditProfile = () => {
    hashHistory.push('source-profile');
    $('a[href="#configProfile"]').tab('show');
    $('#configProfileLog').modal('hide');
    this.props.dispatch(preloadProfile(this.props.viewProfiles.profileToEdit[0]));
  }

  onShowLogModal = (profileLog) => {
    this.props.dispatch(fetchProfileLog(profileLog[0].id));
    setTimeout(() => {
      $('#configProfileLog')
        .modal('show')
        .on('hide.bs.modal', () => this.props.dispatch(editProfile(null)));
    }, 300);
  }

  render() {
    const { selectedTab } = this.props.common;
    const { profileToEdit } = this.props.viewProfiles;
    const { isFetching, profileLogs } = this.props.sourceLocation;

    return (
      <div className="source-location-wrapper">
        { selectedTab === 'SourceLocation' &&
          <ViewProfiles
            editBtnIcon="glyphicon-list-alt"
            editBtnText="View Details"
            onEditProfileButton={this.onShowLogModal}
            getFilterHeading={this.getFilterHeading}
            getFilterRows={this.getFilterRows} /> }

        { profileToEdit && ViewProfiles &&
          <ConfirmPopup
            customClass="modal-lg modal-table"
            confirmId='configProfileLog'
            title={'Profile Name: ' + profileToEdit[0].profileName}
            content={isFetching ? <div className="cssload-wrapper"><CssLoad /></div> : <ModalGridLog profileLogs={profileLogs} />}
            cancelButtonText='Cancel'
            confirmButtonText='Configure'
            onConfirm={this.onEditProfile} /> }
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    common: state.common,
    viewProfiles: state.viewProfiles,
    sourceLocation: state.sourceLocation
  };
}

export default connect(mapStateToProps)(SourceLocation);
