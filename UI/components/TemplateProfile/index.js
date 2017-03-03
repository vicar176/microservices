import 'react-select/dist/react-select.css';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import toastr from 'toastr';
import baseToastrConfig from 'lib/baseToastrConfig';
import { flatten, reject, difference} from 'lodash';

// components
import MainContent from 'components/Common/MainContent';
import TemplateMapping from 'components/TemplateMapping';
import ConfirmPopup from 'components/Common/ConfirmPopup';
import PreviousVersion from 'components/Common/PreviousVersion/PreviousVersion';
import ViewProfiles from 'components/ViewProfiles';
import ViewMorePopover from 'components/Common/ViewMorePopover';
import SelectDocumentType from './SelectDocumentType';
import SelectSeller from './SelectSeller';
import SelectLender from './SelectLender';
import AddAffinities from './AddAffinities';
import AddEditTemplate from './AddEditTemplate';
import TemplateName from './TemplateName';
import SelectTemplate from './SelectTemplate';
import UploadSample from './UploadSample';
import SelectAffinities from './SelectAffinities';

// actions
import {
  fetchDocType,
  selectTemplateDocType,
  fetchSellers,
  selectTemplateSeller,
  fetchLenders,
  selectTemplateLender,
  fetchLenderAffinities,
  selectAddAffinities,
  receiveTemplateProfile,
  selectTemplateName,
  selectTemplateFile,
  cancelTemplate,
  sendSampleTemplate,
  saveTemplate,
  addLenderAffinities,
  removeLenderAffinities,
  createNewTemplate,
  resetLenderAffinities,
  setTotalPages,
  selectedDocTypeLenderSellerAffinities,
  fetchFilterGrid,
  fetchTemplateProfile,
  isFetchingFilterGrid,
  saveSampleTemplateName,
  cleanSampleTemplateName
} from 'actions/template';


import { saveProfileData, cleanProfileData, isEditMode } from 'actions/common';
import {
  fetchVersion,
  selectPreviousVersionNumber,
  selectPreviousVersion
 } from 'actions/previousVersion';
import { cleanProfileGrid } from 'actions/viewProfiles';


class TemplateProfile extends Component {
  constructor(props) {
    super(props);
    this.query = 'template-mapping-profiles';
    this.state = {
      isPreloadingTemplate: false,
      isTemplateNameValid: true
    };
  }

  componentDidMount() {
    const { dispatch } = this.props;
    const { preloadedProfileData } = this.props.common;
    dispatch(cancelTemplate());
    dispatch(fetchDocType()).then(() => {
      if (preloadedProfileData) {
        const { templateId, version, documentType, seller, originalLender, exception, isNotFoundTemplate } = preloadedProfileData;

        if (exception) {
          this.preloadTemplate(templateId, version);
        }

        if (isNotFoundTemplate) {
          this.onTypeSelect(documentType.id);
          this.onSelectSeller(seller);
          this.onSelectLender({ name: originalLender });
        }
      }
    });
  }

  preloadTemplate = (id, version) => {
    return this.props.dispatch(fetchVersion(`${this.query}/${id}`, version)).then(response => {
      if (response) {
        const { documentType, seller, originalLender, affinities} = response;
        this.setState({ isPreloadingTemplate: true });
        this.onTypeSelect(documentType.id);
        this.onSelectSeller(seller);
        this.onSelectLender(originalLender, !!affinities);

        if (affinities) {
          this.onAddAffinities('yes');
          this.onAddLenderAffinities(affinities);
          // this.onSelectTemplate(id);
        } else {
          this.onAddAffinities('no');
        }

        this.props.dispatch(receiveTemplateProfile({
          seller,
          documentType,
          originalLender,
          templates: [response]
        }));
      }

      this.setState({ isPreloadingTemplate: false });
      return response;
    });
  }

  getFilterHeading = () => {
    return [
      {
        key: 'name',
        name: 'Template Name',
        style: {'width': '40%'},
        sortable: true,
        filterable: true
      },{
        key: 'documentTypeCode',
        query: 'documentType.code',
        name: 'Document Type',
        className: 'text-upper',
        sortable: true,
        filterable: true
      },
      {
        key: 'sellerName',
        query: 'seller.name',
        name: 'Seller',
        sortable: true,
        filterable: true
      },
      {
        key: 'originalLenderName',
        query: 'originalLender.name',
        name: 'Lender',
        sortable: true,
        filterable: true
      },
      {
        key: 'affinities',
        query: 'affinitiesMap',
        sortcol: 'affinities',
        name: 'Affinity(s)',
        sortable: true,
        filterable: true,
        rowFormatter: this.renderAffinitiesList
      },{
        key: 'version',
        name: 'Version #'
      },
      {
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
        // startIndex: response.startIndex,
        filters: query.filters,
        sorts: query.sorts,
        items: response.items
      }
    });
  }

  onEditTemplate = (e, profileToEdit) => {
    $('a[href="#configProfile"]').tab('show');
    const { templateId, version } = profileToEdit;
    const { dispatch } = this.props;
    // const currRowData = this.props.viewProfiles.currentRows.filter(row => row.id === e.target.id)[0];
    dispatch(saveProfileData(profileToEdit));
    dispatch(isEditMode(true));
    this.preloadTemplate(templateId, version);
    e.preventDefault();
  }

  onTypeSelect = (docType) => {
    const { dispatch } = this.props;
    dispatch(selectTemplateDocType(docType));
    dispatch(fetchSellers());
    dispatch(selectedDocTypeLenderSellerAffinities());
  };

  onSelectSeller = (seller) => {
    const { dispatch } = this.props;
    dispatch(selectTemplateSeller(seller));
    if (seller) {
      dispatch(fetchLenders(seller.id));
    }
    dispatch(selectedDocTypeLenderSellerAffinities());
  };

  onSelectLender = (lender, preloadAffinities = true) => {
    const { dispatch } = this.props;
    dispatch(selectTemplateLender(lender));
    if (lender && preloadAffinities) {
      dispatch(fetchLenderAffinities(lender.name));
    }
    dispatch(selectedDocTypeLenderSellerAffinities());
  };

  onAddAffinities = (value) => {
    const { dispatch } = this.props;
    this.setState({ isTemplateNameValid: true});
    dispatch(selectAddAffinities(value));
    if (!this.state.isPreloadingTemplate) {
      dispatch(fetchTemplateProfile()).then((response) => {
        if (response) {
          const {id,version} = response[0];
          dispatch(fetchVersion(`${this.query}/${id}`, version)).then((result) => {
            dispatch(saveProfileData(result));
          });
        }
      });
    }
    if (value === 'yes') {
      dispatch(createNewTemplate(false));
      dispatch(selectTemplateName(''));
      return;
    }
    dispatch(selectedDocTypeLenderSellerAffinities());
  };

  onFileChange = (file,sampleName) => {
    const {dispatch} = this.props;
    const { preloadedProfileData } = this.props.common;
    if (file) {
      dispatch(sendSampleTemplate(file));
      dispatch(saveSampleTemplateName(sampleName));
      if (!preloadedProfileData) {
        dispatch(fetchTemplateProfile()).then((response) => {
          if (response) {
            const {id,version} = response[0];
            dispatch(fetchVersion(`${this.query}/${id}`, version)).then((result) => {
              dispatch(saveProfileData(result));
            });
          }
        });
      }
    }
  };

  onSelectTemplate = (id) => {
    const { dispatch } = this.props;
    if (id) {
      const { name, sampleFileName, affinities, totalPages } = this.props.profile.templates.find(template => template.id === id);
      dispatch(selectTemplateName(name));
      dispatch(selectTemplateFile(sampleFileName));
      dispatch(resetLenderAffinities());
      dispatch(addLenderAffinities(affinities));
      dispatch(setTotalPages(totalPages));
    } else {
      dispatch(selectTemplateName(''));
      dispatch(selectTemplateFile(''));
    }
  };

  onTemplateCancel = () => {
    const { preloadedProfileData } = this.props.common;
    const { previousVersion } = this.props;
    jQuery('#templateSaveCancel').modal('hide');
    if (previousVersion.selectedPreviousVersionNumber === '') {
      this.props.dispatch(cancelTemplate());
      this.props.dispatch(cleanProfileData());
    } else {
      this.props.dispatch(selectPreviousVersionNumber(''));
      this.props.dispatch(selectPreviousVersion({}));
      this.preloadTemplate(preloadedProfileData.templateId, preloadedProfileData.version);
    }
  };

  onTemplateSave = () => {
    const { areas, dispatch } = this.props;
    toastr.options = baseToastrConfig();

    for (let i = areas.length - 1; i >= 0; i--) {
      const area = areas[i];
      if (area.required && !area.mapped) {
        toastr.error('All required fields must be mapped');
        return;
      }
    }

    toastr.clear();
    dispatch(saveTemplate());
  };

  onAddLenderAffinities = (affinities) => {
    this.props.dispatch(addLenderAffinities(affinities));
  };

  onRemoveLenderAffinities = (affinities) => {
    this.props.dispatch(removeLenderAffinities(affinities));
  };

  onCreateNew = (value) => {
    const {dispatch} = this.props;
    this.setState({ isTemplateNameValid: true});
    dispatch(createNewTemplate(value));
    if (value) {
      dispatch(cleanSampleTemplateName());
    }
  };

  cancelTemplate = () => {
    const {dispatch} = this.props;
    const { selectedTab } = this.props.common;
    if (selectedTab === 'viewProfiles') {
      dispatch(cancelTemplate());
      dispatch(isFetchingFilterGrid(false));
      dispatch(isEditMode(false));
    } else {
      dispatch(cleanProfileGrid());
    }
  };

  renderEditRowButton = props => {
    return <a
      href="#"
      title="Configure"
      id={props.id}
      className="glyphicon glyphicon-edit"
      onClick={e => this.onEditTemplate(e, props)}></a>
  }

  renderAffinitiesList = props => {
    if (props.affinities) {
      return <ViewMorePopover
                listSize={3}
                scrollItems={6}
                trimLength={20}
                title="Affinities"
                id={props.templateId}
                keysList={props.affinities} />
    }
  }

  render() {
    const {
      selectedDocType,
      isFetchingSellers,
      selectedSeller,
      isFetchingLenders,
      selectedLender,
      addAffinities,
      selectedTemplateName,
      selectedTemplateFile,
      isProfileLoaded,
      associatedAffinities,
      isFetchingAffinities,
      createNewTemplate,
      isTemplateLoaded,
      isSavingProfile,
      isFetchingProfile,
      sampleTemplateName
    } = this.props.template;
    const {
      sellers,
      docTypes,
      originalLenders,
      lenderAffinities,
    } = this.props.portfolio;

    const { templates } = this.props.profile;
    const { selectedTab, preloadedProfileData, isEditMode } = this.props.common;
    const isPreviewMode = this.props.previousVersion.isProfileEnable;
    let hasAvailableAffinities = true;

    // use for affinities association
    let availableAffinities = [];
    if (addAffinities === 'yes') {
      let templatesWithAffinities = templates;
      // if template is selected, exclude it
      // so its affinities don't get discarded
      if (selectedTemplateName && !createNewTemplate) {
        templatesWithAffinities = reject(templates, {name: selectedTemplateName});
      }
      templatesWithAffinities = templatesWithAffinities || [];
      const templatesAffinities = flatten(templatesWithAffinities.map(template=> template.affinities));
      availableAffinities = difference(lenderAffinities, templatesAffinities, associatedAffinities);
      hasAvailableAffinities = !!availableAffinities.length || !!associatedAffinities.length;
    }

    return (
      <MainContent
        routes={this.props.routes}
        mainTitle='Metadata Template Profile'
        tabs={[{id: 'configProfile', name: 'Configure Profile'},{id: 'viewProfiles', name: 'View Profiles'}]}
        onClickCallback={() => this.cancelTemplate()} >
        <div role="tabpanel" className="tab-pane active" id="configProfile">
          { // selectedTab === 'configProfile' &&
            <form id="metadata-profile-form" className="panel-group" onSubmit={ e => e.preventDefault() }>
              <div className="tab-header">
                { !!preloadedProfileData &&
                  <PreviousVersion
                    query={this.query}
                    template={preloadedProfileData}
                    selectVersion={this.preloadTemplate} />
                }
                <h2 className="page-header">Configure Profile</h2>
              </div>
              <div id="configuration-tab">
                <div className="row">
                  <SelectDocumentType
                    docTypes={docTypes}
                    selectedDocType={selectedDocType}
                    isDisabled={isPreviewMode || !!preloadedProfileData}
                    onTypeSelect={this.onTypeSelect}
                  />
                  <SelectSeller
                    selectedSeller={selectedSeller}
                    sellers={sellers}
                    isFetching={isFetchingSellers}
                    selectedDocType={selectedDocType}
                    isDisabled={isPreviewMode || !!preloadedProfileData}
                    onSelectSeller={this.onSelectSeller}
                  />
                  <SelectLender
                    selectedLender={selectedLender}
                    lenders={originalLenders}
                    selectedSeller={selectedSeller}
                    isFetching={isFetchingLenders}
                    isDisabled={isPreviewMode || !!preloadedProfileData}
                    onSelectLender={this.onSelectLender}
                  />
                </div>
                { selectedLender &&
                  <div className="template-definition" style={{ marginTop: '3em' }}>
                    <h1 className="page-header">Template Definition</h1>
                    <div className="row">
                      <div className="col-xs-6 col-sm-3 col-md-2 required">
                        <AddAffinities
                          onAddAffinities={this.onAddAffinities}
                          hasAffinities={!!lenderAffinities.length}
                          addAffinities={addAffinities}
                          isFetching={isFetchingAffinities}
                          isDisabled={isEditMode && preloadedProfileData && !preloadedProfileData.isNotFoundTemplate} />
                      </div>
                      <div className="col-xs-6 col-sm-3 col-md-2">
                        <AddEditTemplate
                          addAffinities={addAffinities}
                          hasAvailableAffinities={hasAvailableAffinities}
                          // templates={templates}
                          createNewTemplate={createNewTemplate}
                          isDisabled = {isEditMode && preloadedProfileData && !preloadedProfileData.isNotFoundTemplate}
                          isFetchingProfile={isFetchingProfile}
                          onCreateNew={this.onCreateNew} />
                      </div>
                      <div className="col-xs-12 col-sm-6 col-md-8">
                        <TemplateName
                          selectedTemplateName={selectedTemplateName}
                          isShow={isProfileLoaded && createNewTemplate || addAffinities === 'no' && selectedTemplateName} />
                        <SelectTemplate
                          onSelectTemplate={this.onSelectTemplate}
                          selectedTemplateName={selectedTemplateName}
                          templates={templates ? templates : []}
                          isShow={addAffinities === 'yes' && isProfileLoaded && !createNewTemplate}
                          isDisabled={isEditMode && preloadedProfileData && !preloadedProfileData.isNotFoundTemplate} />
                      </div>
                    </div>
                    <div className="row">
                      <div className="col-xs-12">
                        <UploadSample
                          onFileChange={this.onFileChange}
                          createNewTemplate={createNewTemplate}
                          sampleTemplateName={sampleTemplateName}
                          isTemplateNameValid={this.state.isTemplateNameValid}
                          isDisabled={isPreviewMode}
                        />
                      </div>
                    </div>
                    {
                      isProfileLoaded &&
                      addAffinities === 'yes' &&
                      (createNewTemplate || selectedTemplateName.length > 0) &&
                      <div className="row" style={{ marginTop: '2em' }}>
                        <SelectAffinities
                          createNewTemplate={createNewTemplate}
                          selectedTemplateName={selectedTemplateName}
                          availableAffinities={availableAffinities}
                          associated={associatedAffinities}
                          onAddLenderAffinities={this.onAddLenderAffinities}
                          onRemoveLenderAffinities={this.onRemoveLenderAffinities}
                          isTemplateNameValid={this.state.isTemplateNameValid}
                          hasAvailableAffinities={hasAvailableAffinities}
                          isDisabled={isPreviewMode} />
                      </div>
                    }
                  </div>
                }
              </div>
              { isProfileLoaded &&
                selectedTemplateFile &&
                <TemplateMapping isDisabled={isPreviewMode}/>
              }
              { isTemplateLoaded &&
                <div className="row">
                  <div className="text-center form-inline-group col-xs-12 col-sm-9 col-sm-offset-3">
                    <p className="col-xs-6 col-sm-3 col-sm-offset-3">
                      <button name="save" className="btn btn-lg btn-block btn-primary" disabled={isPreviewMode || isSavingProfile} onClick={this.onTemplateSave}>
                        {isSavingProfile ? 'Saving' : 'Save'}
                      </button>
                    </p>
                    <p className="col-xs-6 col-sm-3">
                      <button name="cancel" className="btn btn-lg btn-block btn-default" disabled={isPreviewMode} onClick={()=>jQuery('#templateSaveCancel').modal('show')}>Cancel</button>
                    </p>
                  </div>
                </div>
              }
            </form>
          }
          <ConfirmPopup confirmId='templateSaveCancel' onConfirm={this.onTemplateCancel} title='Cancel' content='Do you really want to cancel? You will lose the changes'/>
        </div>
        <div role="tabpanel" className="tab-pane" id="viewProfiles">
          <h2 className="page-header">View Profiles</h2>
          <div className="wrap-forms">
            { selectedTab === 'viewProfiles' &&
              <ViewProfiles
                isFetchingRows={this.props.template.isFetchingFilterGrid}
                getFilterHeading={this.getFilterHeading}
                getFilterRows={this.getFilterRows} />
            }
          </div>
        </div>
      </MainContent>
    )
  }
}

function mapStateToProps(state) {
  return {
    common: state.common,
    template: state.template,
    portfolio: state.templatePortfolio,
    profile: state.templateProfile,
    areas: state.templateAreas,
    previousVersion: state.previousVersion
  }
}

export default connect(mapStateToProps)(TemplateProfile);
