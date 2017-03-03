import React, { Component } from 'react';
import { connect } from 'react-redux';
import baseToastrConfig from 'lib/baseToastrConfig';
import toastr from 'toastr';

// components
import MainContent from 'components/Common/MainContent';
import ConfirmPopup from 'components/Common/ConfirmPopup';
import ViewProfiles from 'components/ViewProfiles';
import PreviousVersion from 'components/Common/PreviousVersion/PreviousVersion';
import ViewMorePopover from 'components/Common/ViewMorePopover';
import SelectGroup from './SelectGroup';
import EnablePortfolio from './EnablePortfolio';
import SearchPortfolio from './SearchPortfolio';
import SelectLender from './SelectLender';
import SelectDocTypes from './SelectDocTypes';

// actions
import {
  fetchProductGroups,
  fetchDocTypes,
  selectProductGroup,
  fetchOALDProfile,
  receiveOALDProfile,
  enablePortfolio,
  fetchPortfolioLenders,
  isFetchingFilterGrid,
  receivePortfolioLenders,
  selectLender,
  fetchPortofolioProfile,
  removeDocTypes,
  addDocTypes,
  saveOALDProfile,
  cancelOALDProfile,
  selectPortfolio,
  fetchFilterGrid
} from 'actions/oald';

import { fetchVersion, resetPreviousVersion } from 'actions/previousVersion';
import { saveProfileData, cleanProfileData } from 'actions/common';
import { cleanProfileGrid } from 'actions/viewProfiles';

class OALDProfile extends Component {
  constructor(props) {
    toastr.options = baseToastrConfig();
    super(props);
    this.query = 'oald-profiles';
    this.state = {
      confirmationMessage: '',
      isPortfolioActive: false,
      isEditModeActive: false
    };
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch(cancelOALDProfile());
    dispatch(fetchProductGroups());
    dispatch(fetchDocTypes());
  }

  preloadProfile = (id, version) => {
    const { dispatch } = this.props;
    dispatch(cancelOALDProfile());

    return this.props.dispatch(fetchVersion(`${this.query}/${id}`, version)).then(response => {
      const { productGroup, portfolio, originalLender, documentTypes } = response;
      dispatch(selectProductGroup(productGroup));
      if (portfolio) {
        dispatch(enablePortfolio(true));
        dispatch(selectPortfolio(portfolio.id));
        if (originalLender) {
          dispatch(receivePortfolioLenders([originalLender]));
          dispatch(selectLender(originalLender.name));
        }
      }
      if (documentTypes) {
        dispatch(addDocTypes(documentTypes));
      }
      dispatch(receiveOALDProfile(response));

      return response;
    });
  }

  setToastrOnError(message) {
    toastr.options = baseToastrConfig();
    toastr.error(message);
  }

  setToastrOnWarning(message) {
    toastr.options = baseToastrConfig();
    toastr.warning(message);
  }

  getFilterHeading = () => {
    return [
      {
        key: 'productGroupName',
        query: 'productGroup.name',
        name: 'Product Group',
        sortable: true,
        filterable: true
      },{
        key: 'portfolioId',
        query: 'portfolio.id',
        type: 'number',
        name: 'Portfolio #',
        sortable: true,
        filterable: true
      },{
        key: 'originalLenderName',
        query: 'originalLender.name',
        name: 'Lender',
        sortable: true,
        filterable: true
      },{
        key: 'documentTypes',
        query: 'documentTypes.codeMap',
        sortcol: 'documentTypes',
        name: 'Document Type(s)',
        className: 'text-nowrap',
        sortable: true,
        filterable: true,
        rowFormatter: this.renderDocTypes
      },{
        key: 'version',
        name: 'Version #'
      },{
        key: 'configId',
        btnHead: 'filter',
        className: 'text-center',
        style: { 'width': 50 },
        rowFormatter: this.renderEditRowButton
      }
    ];
  }

  getFilterRows = query => {
    return this.props.dispatch(fetchFilterGrid(query)).then(response => {
      return {
        totalItems: response.totalItems,
        itemsPerPage: response.itemsPerPage,
        filters: query.filters,
        sorts: query.sorts,
        items: response.items
      }
    });
  }

  onGroupSelect = (group) => {
    const { dispatch } = this.props;
    dispatch(cancelOALDProfile());
    // reset all previous versions only if product Group change
    // if (oald.selectedLender === null) {
    //   dispatch(cleanProfileData());
    // }

    if (group) {
      dispatch(selectProductGroup(group));
      dispatch(fetchOALDProfile(group.code)).then(response => {
        if (response) {
          dispatch(fetchVersion(`document-fields/field-definitions/${response.id}`));
          dispatch(saveProfileData(response));
        } else {
          dispatch(resetPreviousVersion());
        }
      });
    }
  };

  onPortfolioEnable = (isEnabled) => {
    const { selectedLender, selectedProductGroup } = this.props.oald;

    this.props.dispatch(enablePortfolio(isEnabled));
    if (!isEnabled && selectedLender) {
      this.onGroupSelect(selectedProductGroup);
    }
  };

  onPortfolioSearch = (id) => {
    const { dispatch } = this.props;

    if (!isNaN(id) && id !== '') {
      this.setState({ isPortfolioActive: true });
      dispatch(fetchPortfolioLenders(id));
    } else {
      this.setToastrOnWarning('Enter a Portfolio ID with only numbers');
    }
  };

  onPortfolioChange = (newValue, oldValue) => {
    if (this.state.isPortfolioActive) {
      this.setState({ isPortfolioActive: false });
      this.onGroupSelect(this.props.oald.selectedProductGroup);
      this.onPortfolioEnable(true);
    }

    if (!isNaN(newValue)) {
      this.props.dispatch(selectPortfolio(newValue));
    } else {
      this.props.dispatch(selectPortfolio(oldValue));
      this.setToastrOnWarning('Enter a Portfolio ID with only numbers');
    }
  };

  onLenderSelect = (name) => {
    const { dispatch } = this.props;
    dispatch(selectLender(name));
    if (name) {
      dispatch(fetchPortofolioProfile()).then( (response) => {
        if (response) {
          dispatch(fetchVersion(`document-fields/field-definitions/${response.id}`));
          dispatch(saveProfileData(response));
        }
      });
    } else {
      const { selectedProductGroup, selectedPortfolio } = this.props.oald;
      this.onGroupSelect(selectedProductGroup);
      this.onPortfolioEnable(true);
      this.onPortfolioSearch(selectedPortfolio);
    }
  };

  onRemoveDocTypes = (ids) => {
    this.props.dispatch(removeDocTypes(ids));
  };

  onAddDocTypes = (ids) => {
    this.props.dispatch(addDocTypes(ids));
  };

  onSaveConfirmation = () => {
    const { isPortfolioEnabled, selectedPortfolio, selectedLender } = this.props.oald;
    if (isPortfolioEnabled) {
      if (!selectedPortfolio || !selectedLender) {
        this.setToastrOnError('You must select a Portfolio and Original lender');
      } else {
        this.setState({confirmationMessage: 'You are going to change OALD profile for portfolio '+selectedPortfolio+' for the selected product group.  Would you like to continue?'});
        jQuery('#oaldSaveCancel').modal('show');
      }
    } else {
      this.setState({confirmationMessage: 'You are going to change OALD profile for the selected product group level.  Would you like to continue?'});
      jQuery('#oaldSaveCancel').modal('show');
    }
  };

  onSave = () => {
    const { dispatch } = this.props;
    dispatch(saveOALDProfile());
    dispatch(cleanProfileData());
    jQuery('#oaldSaveCancel').modal('hide');
    if (this.props.common.preloadedProfileData && !this.props.oald.errorOnSave) {
      $('a[href="#viewProfiles"]').tab('show');
      dispatch(cleanProfileData());
    }
  };

  onCancel = () => {
    const { dispatch } = this.props;
    dispatch(cancelOALDProfile());
    dispatch(cleanProfileData());
    dispatch(editProfile(null));
  };

  onApplyVersion = () => {
    this.props.dispatch(applyVersion());
  };

  onEditProfile = (e, profileToEdit) => {
    const { dispatch } = this.props;
    const { id, version } = profileToEdit;

    dispatch(saveProfileData(profileToEdit));
    this.setState({ isEditModeActive: true });
    this.preloadProfile(id, version);

    $('a[href="#configProfile"]').tab('show');
    e.preventDefault();
  }

  onTabChangeHandle = () => {
    const { dispatch } = this.props;
    if (this.props.common.selectedTab === 'viewProfiles') {
      dispatch(cancelOALDProfile());
      dispatch(isFetchingFilterGrid(false));
      this.setState({ isEditModeActive: false });
    } else {
      dispatch(cleanProfileGrid());
    }
  }

  renderDocTypes = props => {
    return (
      <ViewMorePopover
        listSize={3}
        id={props.id}
        scrollItems={12}
        title="Document Types"
        keysList={props.documentTypes.map(type => type.code)} />
    )
  }

  renderEditRowButton = props => {
    return <a
      href="#"
      id={props.id}
      title="Configure"
      className="glyphicon glyphicon-edit"
      onClick={e => this.onEditProfile(e, props)}></a>
  }

  render() {
    const {
      selectedProductGroup,
      selectedPortfolio,
      selectedLender,
      isFetchingProductGroups,
      isPortfolioEnabled,
      isFetchingPortfolio,
      isSavingProfile
    } = this.props.oald;

    const {
      productGroups,
      docTypes,
      originalLenders
    } = this.props.portfolio;

    const { selectedTab, preloadedProfileData } = this.props.common;

    let isSelectDoctypesEnabled = true;
    if (isPortfolioEnabled && (!selectedPortfolio || !selectedLender)) {
      isSelectDoctypesEnabled = false;
    }

    const { documentTypes } = this.props.profile;
    const { isFetchingPreviousVersion, isProfileEnable } = this.props.previousVersion;
    const isPreviewMode = !!isProfileEnable;


    return (
      <MainContent
        routes={this.props.routes}
        mainTitle='OALD Profile'
        tabs={[{id: 'configProfile', name: 'Configure Profile'},{id: 'viewProfiles', name: 'View Profiles'}]}
        onClickCallback={this.onTabChangeHandle} >

        <div role="tabpanel" className="tab-pane active" id="configProfile">
          <div className="tab-header">
            { !!preloadedProfileData &&
              <PreviousVersion
                query={this.query}
                template={preloadedProfileData}
                selectVersion={this.preloadProfile} />
            }
            <h2 className="page-header">Product Group</h2>
          </div>
          <div className="wrap-forms">
            { (selectedTab === 'configProfile') &&
              <form onSubmit={ e => e.preventDefault() }>
                <SelectGroup
                  selectedProductGroup={selectedProductGroup}
                  productGroups={productGroups}
                  onGroupSelect={this.onGroupSelect}
                  isFetching={isFetchingProductGroups || isFetchingPreviousVersion}
                  isDisabled={isPreviewMode || this.state.isEditModeActive}
                />
                { selectedProductGroup &&
                  <div className="row" style={{marginTop: '3em'}}>
                    <div className="col-xs-12">
                        <div>
                          <h2 className="page-header">Specify OALD for Portfolio</h2>
                          <div className="row">
                            <EnablePortfolio
                              onPortfolioEnable={this.onPortfolioEnable}
                              isPortfolioEnabled={isPortfolioEnabled}
                              isDisabled={isPreviewMode || this.state.isEditModeActive}
                            />
                            {
                              isPortfolioEnabled &&
                              <SearchPortfolio
                                onPortfolioSearch={this.onPortfolioSearch}
                                isFetching={isFetchingPortfolio}
                                selectedPortfolio={selectedPortfolio}
                                onPortfolioChange={this.onPortfolioChange}
                                isDisabled={isPreviewMode || this.state.isEditModeActive}
                              />
                            }
                            {
                              isPortfolioEnabled &&
                              selectedPortfolio &&
                              !!originalLenders.length &&
                              this.state.isPortfolioActive &&
                              <SelectLender
                                originalLenders={originalLenders}
                                onLenderSelect={this.onLenderSelect}
                                selectedLender={selectedLender}
                                isDisabled={isPreviewMode || this.state.isEditModeActive}
                              />
                            }
                          </div>
                        </div>
                    </div>
                  </div>
                }
                {
                  selectedProductGroup &&
                  <SelectDocTypes
                    docTypes={docTypes}
                    associatedDocTypes={documentTypes}
                    onRemoveDocTypes={this.onRemoveDocTypes}
                    onAddDocTypes={this.onAddDocTypes}
                    isDisabled={!isSelectDoctypesEnabled || isPreviewMode}
                    groupName={selectedProductGroup.name}
                  />
                }
                {
                  selectedProductGroup &&
                  <div className="row" style={{margin: '3em 0 2em'}}>
                    <div className="col-md-offset-5 col-xs-4">
                      <button name="save" className="btn btn-primary" style={{marginRight: "1em"}} disabled={isSavingProfile || isPreviewMode} onClick={this.onSaveConfirmation}>{isSavingProfile ? 'Saving' : 'Save'}</button>
                      <button name="cancel" disabled={isPreviewMode} className="btn btn-default" onClick={this.onCancel}>Cancel</button>
                    </div>
                  </div>
                }
              </form>
            }
            <ConfirmPopup confirmId='oaldSaveCancel' title='Save' onConfirm={this.onSave} content={this.state.confirmationMessage}/>
          </div>
        </div>
        <div role="tabpanel" className="tab-pane" id="viewProfiles">
          <h2 className="page-header">View Profiles</h2>
          <div className="wrap-forms">
            { (selectedTab === 'viewProfiles') &&
              <ViewProfiles
                isFetchingRows={this.props.oald.isFetchingFilterGrid}
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
    oald: state.oald,
    portfolio: state.oaldPortfolio,
    profile: state.oaldProfile,
    previousVersion: state.previousVersion
  }
}

export default connect(mapStateToProps)(OALDProfile);
