import React, { Component } from 'react';
import { connect } from 'react-redux';
import toastr from 'toastr';
import baseToastrConfig from 'lib/baseToastrConfig';
import { Tooltip, OverlayTrigger} from 'react-bootstrap';
import { isFormValid, saveProfileData, cleanProfileData } from 'actions/common';

// components
import MainContent from 'components/Common/MainContent';
import SelectDocumentFields from 'SelectDocumentFields';
import PreviousVersion from 'components/Common/PreviousVersion/PreviousVersion';
import HeadingForm from 'HeadingForm';
import FieldDefinitionsGrid from 'FieldDefinitionsGrid';
import ConfirmPopup from 'components/Common/ConfirmPopup';

// styles
import styles from './index.scss';

// actions
import {
  // component ui actions:
  fetchDocuments,
  fetchFieldDefinitions,
  selectDocument,
  // profile data actions:
  setActiveState,
  setAccountVerification,
  setDocumentFieldProfile,
  addFieldDefinition,
  removeFieldDefinition,
  updateFieldDefinition,
  updateRequirementLevel,
  saveDocumentFieldProfile,
  // cancelDocumentFieldProfile,
  cleanDocumentFieldProfile,
  cleanDocumentField
} from 'actions/documentFieldProfile';

import {
  // fetchVersionList,
  fetchVersion,
  resetPreviousVersion
} from 'actions/previousVersion';

class DocumentFieldProfile extends Component {
  constructor(props) {
    super(props);
    toastr.options = baseToastrConfig();

    this.query = 'document-fields/document-field-definitions';
    this.state = {
      documentProfileDropdown: [],
      fieldDefinitions: [],
      currentProfile: {},
      isDocumentEnable: false,
      isDocumentActive: false,
      fieldNameRequired: ""
    }
  }

  componentDidMount() {
    const { dispatch } = this.props;
    // reset state
    dispatch(cleanDocumentField());
    this.fetchDocumentProfileData();
  }

  // Converts server response into dropdown format {id, value}
  convertDataToList(documents) {
    return documents.map(profile => {
      // console.log('convertDataToList:: ', profile);
      return {
        id: profile.fieldName || profile.code,
        value: profile.fieldName || profile.id,
        active: profile.active || true
      }
    });
  }

  fetchDocumentProfileData = () => {
    const { dispatch } = this.props;
    // fetch and transform documents for dropdown
    dispatch(fetchDocuments()).then(({ data }) =>
      this.setState({
        documentProfileDropdown: this.convertDataToList(data)
      })
    );

    // fetch and transform documents definitions for dropdown
    dispatch(fetchFieldDefinitions()).then(response =>
      this.setState({
        fieldDefinitions: this.convertDataToList(response)
      })
    );
  }

  cleanDocumentProfile = () => {
    const { dispatch } = this.props;
    // toastr.clear();
    dispatch(cleanProfileData());
    dispatch(resetPreviousVersion());
    dispatch(cleanDocumentFieldProfile());
    dispatch(cleanDocumentField());
    this.setState({ currentProfile: {} });
  }

  onSelectDocument = selectedOption => {
    const { dispatch } = this.props;
    toastr.clear();
    this.cleanDocumentProfile();
    if (selectedOption) {
      dispatch(fetchDocuments(selectedOption.id)).then(({ data }) => {
        dispatch(selectDocument(selectedOption));
        if (data) {
          dispatch(setDocumentFieldProfile(data));
          dispatch(saveProfileData(data));
          this.setDocumentActiveState();
          // Helps setting profile to previous state when user cancel it
          this.setState({ currentProfile: data });
        }
      });
    } else {
      dispatch(setDocumentFieldProfile({}));
      this.setState({ currentProfile: {} });
    }
  };

  preloadDocumentFields = (id, version) => {
    const { dispatch} = this.props;
    return dispatch(fetchVersion(`${this.query}/${id}`, version)).then(response => {
      dispatch(setDocumentFieldProfile(response));
      dispatch(saveProfileData(response));
      this.setState({ currentProfile: response });
      // console.log('preloadDocumentFields:: ', response);
      return response;
    });
  }

  setDocumentActiveState () {
    const { fieldDefinitions } = this.props.documentFieldProfile;
    const isDocumentActive = !!fieldDefinitions.filter(field => field.fieldDefinition.active).length;
    this.setState({ isDocumentActive: isDocumentActive });
    this.setState({ isDocumentEnable: isDocumentActive });
    if (!isDocumentActive) {
      this.props.dispatch(setActiveState(false));
    }
  }

  onFieldDefinitionDropdownChange = (oldFieldName, newField) => {
    const { dispatch } = this.props;
    const { fieldDefinitions } = this.props.documentFieldProfile;
    const lowFieldName = newField.toLocaleLowerCase();
    const isFieldRecerved = lowFieldName === 'statementbalance' || lowFieldName === 'statementdate';

    const repeatField = fieldDefinitions.filter(field =>
      field.fieldDefinition &&
      field.fieldDefinition.fieldName === newField
    );

    if (repeatField && repeatField.length) {
      toastr.error("Field name is repeated. You cannot save a repeated field name.");
    } else {
      const { receivedFieldDefinitions } = this.props.documentField;
      const newFieldObj = receivedFieldDefinitions.filter(field => field.fieldName === newField)[0];

      dispatch(updateFieldDefinition(oldFieldName, newFieldObj));

      if (newFieldObj.active) {
        this.setState({ isDocumentEnable: true });
      }

      if (isFieldRecerved) {
        dispatch(updateRequirementLevel(newField, 'true'));
      }
    }

  }

  onAccountVerificationChange = val => {
    if (this.props.documentFieldProfile.accountVerification !== val) {
      $('#documentFieldVerification').modal('show');
    }
  }

  onCloseActiveStatusModal = () => {
    $('#documentFieldDeactivate').modal('hide');
    this.props.dispatch(setActiveState(false));
  };

  onActiveStatusChange = val => {
    if (this.props.documentFieldProfile.active !== val) {
      if (!val) {
        $('#documentFieldDeactivate').modal('show');
      } else {
        this.props.dispatch(setActiveState(val));
      }
    }
  }

  onCancelDocumentFieldProfile = () => {
    this.cleanDocumentProfile();
    // this.props.dispatch(cleanDocumentField());
    toastr.clear();
  }

  saveDocumentField = () => {
    const { dispatch } = this.props;
    const { fieldDefinitions } = this.props.documentFieldProfile;
    const emptyFields = fieldDefinitions.filter(field => !field.fieldDefinition.fieldName);
    const isValidForm = emptyFields.length < 1;

    this.getValidationFieldName();

    dispatch(isFormValid(isValidForm));
    if (emptyFields.length) {
      toastr.error('Please be sure all required fields are filled correctly.');
    } else {
      dispatch(saveDocumentFieldProfile(this.state.currentProfile.id)).then(response => {
        if (response.code === 200) {
          toastr.clear();
          // Reset and clean form
          this.cleanDocumentProfile();
          // dispatch(cleanDocumentField());
        }
      });
    }
  }

  getValidationFieldName = () => {
    const { fieldDefinitions } = this.props.documentFieldProfile;
    const emptyFields = fieldDefinitions.filter(field => field.fieldDefinition && !field.fieldDefinition.fieldName);
    this.state.fieldNameRequired = '';
    if (emptyFields.length) {
      this.state.fieldNameRequired = 'Field name is required';
    }
  }

  onAddNewField = (val) => {
    const { dispatch } = this.props;
    this.state.fieldNameRequired = '';
    dispatch(addFieldDefinition(val));
  }

  onRemoveFieldDefinition = val => {
    this.props.dispatch(removeFieldDefinition(val));
    // this.setDocumentActiveState();
  }

  // Set title document indicatior
  renderCurrentDocumentIndicatior(selectedDocumentRef) {
    return(
      <span>:
        <span className={styles.currentDocumentIndicatior}>
          <i className="glyphicon glyphicon-list-alt" />
          {selectedDocumentRef ? selectedDocumentRef : ''}
        </span>
      </span>
    )
  }

  render() {
    // references
    // const isPreviewMode = !!this.props.previousVersionData;
    const { dispatch } = this.props;
    const { fieldDefinitions, accountVerification, active } = this.props.documentFieldProfile;
    const { preloadedProfileData } = this.props.common;
    const { isProfileEnable } = this.props.previousVersion;
    const {
      isFetchingDocuments,
      isFetchingFieldDefinitions,
      selectedDocument
    } = this.props.documentField;
    return (
     <MainContent routes={this.props.routes} mainTitle='Document Field Profile'>
        <div role="tabpanel" className="tab-pane active" id="fieldProfile">
          <div className="tab-header">
            { !!preloadedProfileData &&
              <PreviousVersion
                template={preloadedProfileData}
                query={this.query}
                selectVersion={this.preloadDocumentFields} />
            }
            <h2 className="page-header">Define Document Zoning Fields{selectedDocument ? this.renderCurrentDocumentIndicatior(selectedDocument.id) : ''}</h2>
          </div>
          <div className="wrap-forms">
            <SelectDocumentFields
              documentProfileDropdown={this.state.documentProfileDropdown}
              selectedDocument={selectedDocument}
              onSubmitDocument={this.onSubmitDocument}
              onSelectDocument={this.onSelectDocument}
              isFetchingDocuments={isFetchingDocuments} />
            { !!selectedDocument &&
              <div className="profileContainer">
                <hr />
                <HeadingForm
                  isProfileActive={active}
                  hasFieldDefinitionsActive={this.state.isDocumentActive}
                  isDocumentEnable={this.state.isDocumentEnable}
                  isFieldsActive={isProfileEnable}
                  isProfileVerified={accountVerification}
                  onAddFieldDefinition={this.onAddNewField}
                  onActiveStatusChange={this.onActiveStatusChange}
                  onAccountVerificationChange={this.onAccountVerificationChange} />

                <FieldDefinitionsGrid
                  isActive={fieldDefinitions.length > 0}
                  isFieldsActive={isProfileEnable}
                  getValidationFieldName={this.getValidationFieldName}
                  fieldNameRequired={this.state.fieldNameRequired}
                  documentFieldDefinitions={fieldDefinitions}
                  fieldDefinitionsDropdown={this.state.fieldDefinitions}
                  isFetchingFieldDefinitions={isFetchingFieldDefinitions}
                  onFieldDefinitionDropdownChange={this.onFieldDefinitionDropdownChange}
                  onRemoveFieldDefinition={this.onRemoveFieldDefinition}
                  onSelectRequirementLevel={(val, isRequired) => dispatch(updateRequirementLevel(val, isRequired))} />

                <div className={styles.ctaSection}>
                  <button
                    name="save"
                    className="btn btn-primary"
                    disabled={isProfileEnable}
                    onClick={this.saveDocumentField}
                    style={{marginRight: "1em"}}>
                    Save
                  </button>
                  <button
                    name="cancel"
                    className="btn btn-default"
                    disabled={isProfileEnable}
                    onClick={this.onCancelDocumentFieldProfile}>
                    Cancel
                  </button>
                </div>
              </div>
            }
          </div>
        </div>
        <ConfirmPopup
          title='Account Verification'
          confirmId='documentFieldVerification'
          content='Change account verfication.  Are you sure?'
          onConfirm={() => {
            $('#documentFieldVerification').modal('hide');
            this.props.dispatch(setAccountVerification(!this.props.documentFieldProfile.accountVerification));
          }} />

        <ConfirmPopup
          title='Document Active Status'
          confirmId='documentFieldDeactivate'
          content='Deactivate this document zone Profile. Are you sure?'
          onConfirm={this.onCloseActiveStatusModal} />
      </MainContent>
    );
  }
}

function mapStateToProps(state) {
  return {
    common: state.common,
    documentField: state.documentField,
    documentFieldProfile: state.documentFieldProfile,
    previousVersion: state.previousVersion
  };
}

export default connect(mapStateToProps)(DocumentFieldProfile);
