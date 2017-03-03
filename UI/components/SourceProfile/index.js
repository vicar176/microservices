import React, { Component } from 'react';
import { connect } from 'react-redux';
// Components
import MainContent from 'components/Common/MainContent';
import ViewProfiles from 'components/ViewProfiles';
import PreviousVersion from 'components/Common/PreviousVersion/PreviousVersion';
import ProfileName from 'ProfileName';
import SelectProfile from 'SelectProfile';
import SourceProfileFTP from 'SourceProfileFTP';
// Actions
import { editProfile } from 'actions/viewProfiles';
import {
  fetchPortfolioName,
  setAccessType,
  setHostLocation,
  setHostPort,
  setUsername,
  setPassword,
  setFileLocation,
  checkFileIsZippped,
  setZipPassword,
  preloadProfile,
  cancelProfile
} from 'actions/sourceProfile';
// styles
import Styles from 'index.scss';

class SourceProfile extends Component {
  componentWillUnmount() {
    this.props.dispatch(cancelProfile());
  }

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
        hostLocation: ['ftp.barclaybankmedia.com', 'ftp.capitalone.com/mcm/media', 'ftp.capitalone.com/mcm/stmt', 'ftp.citibank.mcm.com', 'ftp.synchrony.mcm.com'][Math.floor((Math.random() * 5))],
        version: ['Version 1', 'Version 2', 'Version 3', 'Version 4'][Math.floor((Math.random() * 4))],
        configId: i
      });
    }
    // console.log('SourceProfile::getFilterRows::response::', response);
    return response;
  }

  getFilterHeading = () => {
    return [
      { key: 'id', name: 'ID', className: 'text-center' },
      { key: 'profileName', name: 'Profile Name', sortable: true, filterable: true },
      { key: 'accessType', name: 'Access Type', className: 'text-center', sortable: true, filterable: true },
      { key: 'hostLocation', name: 'Host Location', sortable: true, filterable: true },
      { key: 'version', name: 'Version #', btnFilter: 'filter', className: 'text-center' }
    ];
  }

  onEditProfileButton = profileToEdit => {
    $('a[href="#configProfile"]').tab('show');
    this.props.dispatch(preloadProfile(profileToEdit[0]));
  }

  onCancelProfile = () => {
    const { dispatch } = this.props;
    dispatch(cancelProfile());
    dispatch(editProfile(null));
  }

  renderButtons() {
    return (
      <div className="row">
        <ul className="col-xs-12 text-center list-inline">
          <li><button className="btn btn-default" onClick={this.onCancelProfile}>Cancel</button></li>
          <li><button className="btn btn-primary">Save</button></li>
          <li><button className="btn btn-success">Execute Now</button></li>
        </ul>
      </div>
    );
  }

  render() {
    const { dispatch } = this.props;
    const { selectedTab } = this.props.common;
    const {
      profileName,
      accessType,
      isNameAvailable,
      isFileZipped
    } = this.props.sourceProfile;

    const isEditModeActive = this.props.viewProfiles.profileToEdit ? true : false;
    const isPreviewMode = !!this.props.previousVersionData;

    return (
      <MainContent routes={this.props.routes} mainTitle='Source Location Profile' tabs={[{id: 'configProfile', name: 'Configure Profile'},{id: 'viewProfiles', name: 'View Profiles'}]}>
        <div role="tabpanel" className={`${Styles.sourceProfile} tab-pane active`} id="configProfile">
          <div className="tab-header">
            {/* <PreviousVersion
              isShow={isPreviewMode}
              userName={'userTet'}
              date={'1/1/2016'}
              onApplyVersion={() => console.log('applyVersion')} /> */}
            <h2 className="page-header">Source Location Profile</h2>
          </div>
          <form onSubmit={ e => e.preventDefault() }>
            <div className="form-group">
              <div className="row">
                <ProfileName
                  profileName={profileName}
                  isDisabled={isPreviewMode}
                  isNameAvailable={isNameAvailable}
                  isEditModeActive={isEditModeActive}
                  onTypeProfileName={e => dispatch(fetchPortfolioName(e.currentTarget.value))} />
                <SelectProfile
                  isDisabled={isPreviewMode}
                  profileName={profileName}
                  accessType={accessType}
                  isNameAvailable={isNameAvailable}
                  onProfileSelect={e => dispatch(setAccessType(e.currentTarget.value))}/>
              </div>
            </div>
            { isNameAvailable && profileName && accessType === 'ftp' &&
              <SourceProfileFTP
                isDisabled={isPreviewMode}
                sourceProfile={this.props.sourceProfile}
                onEnterLocation={e => dispatch(setHostLocation(e.currentTarget.value))}
                onEnterPort={e => dispatch(setHostPort(e.currentTarget.value))}
                onEnterUsername={e => dispatch(setUsername(e.currentTarget.value))}
                onEnterPassword={e => dispatch(setPassword(e.currentTarget.value))}
                onEnterFileLocation={e => dispatch(setFileLocation(e.currentTarget.value))}
                onEnterZipPassword={e => dispatch(setZipPassword(e.currentTarget.value))}
                onCheckFileType={val => dispatch(checkFileIsZippped(val))}
                isFileZipped={isFileZipped} /> }
            { isNameAvailable && profileName && accessType && this.renderButtons() }
          </form>
        </div>
        <div role="tabpanel" className="tab-pane " id="viewProfiles">
          <h2 className="page-header">View Profiles</h2>
          <div className="wrap-forms">
            { (selectedTab === 'viewProfiles') &&
              <ViewProfiles
                onEditProfileButton={this.onEditProfileButton}
                getFilterHeading={this.getFilterHeading}
                getFilterRows={this.getFilterRows} />
            }
          </div>
        </div>
      </MainContent>
    );
  }

}

function mapStateToProps(state) {
  return {
    common: state.common,
    sourceProfile: state.sourceProfile,
    viewProfiles: state.viewProfiles,
    previousVersionData: state.previousVersion.previousVersionData,
  }
}

export default connect(mapStateToProps)(SourceProfile);
