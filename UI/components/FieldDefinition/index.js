import React, { Component } from 'react';
import { connect } from 'react-redux';
import toastr from 'toastr';
import validator from 'validator';
import { Tooltip, OverlayTrigger} from 'react-bootstrap';
import baseToastrConfig from 'lib/baseToastrConfig';

// components
import MainContent from 'components/Common/MainContent';
import ConfirmPopup from 'components/Common/ConfirmPopup';
import FieldProfile from './FieldProfile';
import PreviousVersion from 'components/Common/PreviousVersion/PreviousVersion';
import ViewProfiles from 'components/ViewProfiles';

// actions
import {
  fetchFieldDefinitionList,
  fetchFieldMappingOptions,
  setFieldDefinitionId,
  fieldNameChange,
  fieldDescriptionChange,
  fieldTypeChange,
  accountMappingChange,
  activeChange,
  encryptChange,
  fieldMappingChange,
  removeAllFieldMapping,
  addMap,
  removeMap,
  saveField,
  cancelNewField,
} from 'actions/fieldDefinition';
import { fetchVersion } from 'actions/previousVersion';
import { saveProfileData, cleanProfileData, isFormValid } from 'actions/common';
import { cleanProfileGrid } from 'actions/viewProfiles';

class FieldDefinition extends Component {
  constructor(props) {
    super(props);
    toastr.options = baseToastrConfig();
    this.query = 'document-fields/field-definitions';
    this.state = {
      activeModal: false,
      isFieldRecerved: false,
      validationNameLegend: '',
      validationDescriptionLegend: '',
      validationFieldTypeLegend: ''
    };
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch(cancelNewField());
    // dispatch(fetchFieldMappingOptions());
  }

  preloadProfile = (id, version) => {
    const { dispatch } = this.props;
    return this.props.dispatch(fetchVersion(`${this.query}/${id}`, version)).then(response => {
      const { fieldName, fieldType, fieldDescription, active } = response;
      dispatch(fieldNameChange(fieldName));
      dispatch(fieldTypeChange(fieldType));
      dispatch(fieldDescriptionChange(fieldDescription));
      dispatch(activeChange(active));

      this.setState({
        validationNameLegend: '',
        validationDescriptionLegend: '',
        validationFieldTypeLegend: ''
      });

      return response;
    });
  }

  isFieldRecerved(field) {
    if (field) {
      const lowFieldName = field.toLocaleLowerCase();
      return lowFieldName === 'statementbalance' || lowFieldName === 'statementdate';
    }
  }

  getFilterHeading = () => {
    return [
      { key: 'fieldName',
        name: 'Field Name',
        sortable: true,
        filterable: true
      },{
        key: 'fieldType',
        name: 'Field Type',
        sortable: true,
        filterable: true
      },
      {
        key: 'fieldDescription',
        name: 'Description',
        sortable: true,
        filterable: true
      },
      {
        key: 'active',
        name: 'Active',
        sortable: true,
        rowFormatter: this.renderActiveCol
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
    return this.props.dispatch(fetchFieldDefinitionList(query)).then(response => {
      return {
        totalItems: response.totalItems,
        itemsPerPage: response.itemsPerPage,
        filters: query.filters,
        sorts: query.sorts,
        items: response.items
      }
    });
  }

  getValidationNameLegend = () => {
    const { fieldName } = this.props.fieldDefinitionProfile;
    this.state.validationNameLegend = "";

    if (!fieldName.length) {
      this.state.validationNameLegend = "Field name is required";
    }
    if (validator.contains(fieldName, ' ')) {
      this.state.validationNameLegend = "Field name cannot contain spaces";
    }
    if (!validator.isAlpha(fieldName)) {
      this.state.validationNameLegend = "Field name can only contain alpha characters";
    }
    if (fieldName.length && !validator.isLowercase(fieldName[0])) {
      this.setState({ validationNameLegend: 'Field name must be camel case format'});
    }
  }

  getValidationDescriptionLegend = () => {
    this.state.validationDescriptionLegend = '';
    if (!this.props.fieldDefinitionProfile.fieldDescription.length) {
      this.state.validationDescriptionLegend = 'Field description is required';
    }
  }

  getValidationFieldTypeLegend = () => {
    this.state.validationFieldTypeLegend = '';
    if (!this.props.fieldDefinitionProfile.fieldType.length) {
      this.state.validationFieldTypeLegend = 'Field type is required';
    }
  }

  onEditFieldButton = (e, profileToEdit) => {
    const { dispatch } = this.props;
    const { id, version, isFieldRecerved } = profileToEdit;

    dispatch(saveProfileData(profileToEdit));
    dispatch(setFieldDefinitionId(id));
    this.preloadProfile(id, version);
    this.setState({ isFieldRecerved: isFieldRecerved });

    $('a[href="#configProfile"]').tab('show');
    e.preventDefault();
  }

  onSaveFieldDefinition = () => {
    const { dispatch } = this.props;

    this.getValidationNameLegend();
    this.getValidationDescriptionLegend();
    this.getValidationFieldTypeLegend();

    const isFormValidated = !this.state.validationNameLegend.length &&
                            !this.state.validationDescriptionLegend.length &&
                            !this.state.validationFieldTypeLegend.length;

    dispatch(isFormValid(isFormValidated));

    if (isFormValidated) {
      dispatch(saveField());
      if (this.props.common.preloadedProfileData) {
        $('a[href="#viewProfiles"]').tab('show');
        this.onCancelFieldDefinition();
      }
    } else {
      return toastr.error('Please be sure all fields are filled correctly.');
    }
  }

  onCancelFieldDefinition = () => {
    const { dispatch } = this.props;
    if (this.props.common.selectedTab === 'viewProfiles') {
      this.setState({ isFieldRecerved: false });
      toastr.clear();
      dispatch(cancelNewField());
      dispatch(cleanProfileData());
      dispatch(isFormValid(true));
    } else {
      dispatch(cleanProfileGrid())
    }
  }

  onAccountMappingChange = value => {
    this.props.dispatch(accountMappingChange(value));
    if (value) {
      this.props.dispatch(addMap(''));
    } else {
      this.props.dispatch(removeAllFieldMapping());
    }
  }

  onActiveChangeConfirm = e => {
    const val = e.target.value === 'true' ? true : false;
    if (!val) {
      $('#fieldDefinitionDeactivate').modal('show');
    } else {
      this.props.dispatch(activeChange(val));
    }
  }

  onCloseConfirmModal = () => {
    $('#fieldDefinitionDeactivate').modal('hide');
    this.props.dispatch(activeChange(false));
  }

  onEncryptedChange = e => {
    const val = e.target.value === 'true' ? true : false;
    if (val) {
      $('#fieldDefinitionEncrypted').modal('show');
    } else {
      this.props.dispatch(encryptChange(val));
    }
  }

  onCloseEncryptedModal = () => {
    $('#fieldDefinitionEncrypted').modal('hide');
    this.props.dispatch(encryptChange(true));
  }

  onModalClose = () => {
    this.setState({ activeModal: false });
  }

  onModalOpen = e => {
    e.preventDefault();
    this.setState({
      activeModal: e.target.getAttribute('data-target')
    });
  }

  renderActiveCol = props => {
    return <span>{props.active ? 'Yes' : 'No'}</span>
  }

  renderEditRowButton = props => {
    const fieldName = props.name || props.fieldName;
    props.isFieldRecerved = this.isFieldRecerved(fieldName);
    return <a
      href="#"
      id={props.id}
      title={props.isFieldRecerved ? 'Reserved Field' : 'Configure'}
      className={'glyphicon ' + (props.isFieldRecerved ? 'glyphicon-exclamation-sign' : 'glyphicon-edit')}
      onClick={e => this.onEditFieldButton(e, props)}></a>
  }

  render() {
    const { selectedTab, preloadedProfileData } = this.props.common;
    const { isSavingFieldDefinitions, fieldMappingOptions } = this.props.fieldDefinition;
    const isPreviewMode = this.props.previousVersion.isProfileEnable;
    const isActiveDescription = this.props.previousVersion.selectedPreviousVersion.active || false;

    return (
      <MainContent
        routes={this.props.routes}
        mainTitle='Field Definition Profile'
        tabs={[{id: 'configProfile', name: 'Configure Profile'},{id: 'viewProfiles', name: 'View Profiles'}]}
        onClickCallback={this.onCancelFieldDefinition} >

        <div role="tabpanel" className="tab-pane active" id="configProfile">
          <div className="tab-header">
            { !!preloadedProfileData &&
              <PreviousVersion
                query={this.query}
                template={preloadedProfileData}
                selectVersion={this.preloadProfile} />
            }
            <h2 className="page-header">Define Document Field</h2>
          </div>
          <div className="wrap-forms">
            { (selectedTab === 'configProfile') &&
              <form onSubmit={ e => e.preventDefault() } style={{position: 'relative'}}>
                <FieldProfile
                  fieldDefinitionProfile={this.props.fieldDefinitionProfile}
                  fieldMappingOptions={fieldMappingOptions}
                  isDisabled={isPreviewMode}
                  isActiveDescription={isActiveDescription}
                  isFieldRecerved={this.state.isFieldRecerved}
                  validationNameLegend={this.state.validationNameLegend}
                  validationDescriptionLegend={this.state.validationDescriptionLegend}
                  validationFieldTypeLegend={this.state.validationFieldTypeLegend}
                  onFieldNameChange={(value) => this.props.dispatch(fieldNameChange(value))}
                  onFieldDescriptionChange={(value) => this.props.dispatch(fieldDescriptionChange(value))}
                  onFieldTypeChange={(value) => this.props.dispatch(fieldTypeChange(value))}
                  onAccountMappingChange={this.onAccountMappingChange}
                  onEncryptedChange={this.onEncryptedChange}
                  onActiveChange={this.onActiveChangeConfirm}
                  onModalClose={this.onModalClose}
                  onModalOpen={this.onModalOpen}
                  activeModal={this.state.activeModal}
                  onFieldMappingChange={(fieldMappingId, mappingTo) => this.props.dispatch(fieldMappingChange(fieldMappingId, mappingTo))}
                  onAddMap={joinType => this.props.dispatch(addMap(joinType))}
                  onRemoveMap={fieldMappingId => this.props.dispatch(removeMap(fieldMappingId))} />
                <div className="row">
                  <div className="col-xs-12 text-center">
                    <button
                      name="save"
                      className="btn btn-primary"
                      style={{marginRight: "1em"}}
                      onClick={() => this.onSaveFieldDefinition(this.props.fieldDefinitionProfile)}>
                      {isSavingFieldDefinitions ? 'Saving' : 'Save'}
                    </button>
                    <button
                      name="cancel"
                      className="btn btn-default"
                      onClick={this.onCancelFieldDefinition}>
                      Cancel
                    </button>
                  </div>
                </div>
              </form>
            }
          </div>
        </div>
        <div role="tabpanel" className="tab-pane" id="viewProfiles">
          <h2 className="page-header">View Profiles</h2>
          <div className="wrap-forms">
            { (selectedTab === 'viewProfiles') &&
              <ViewProfiles
                isFetchingRows={this.props.fieldDefinition.isFetchingFieldDefinitions}
                getFilterHeading={this.getFilterHeading}
                getFilterRows={this.getFilterRows} />
            }
          </div>
        </div>
        <ConfirmPopup confirmId='fieldDefinitionDeactivate' onConfirm={this.onCloseConfirmModal} title='Deactivate Field' content='This action will remove the metadata extraction zone from all associated metadata template profiles.  Continue? Yes/No'/>
        <ConfirmPopup confirmId='fieldDefinitionEncrypted' onConfirm={this.onCloseEncryptedModal} title='Encrypt Field Modal' content='NOT DEFINED YET...'/>
      </MainContent>
    );
  }
}

function mapStateToProps(state) {
  return {
    common: state.common,
    fieldDefinition: state.fieldDefinition,
    fieldDefinitionProfile: state.fieldDefinitionProfile,
    previousVersion: state.previousVersion
  };
}

export default connect(mapStateToProps)(FieldDefinition);
