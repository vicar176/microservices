import React from 'react';
import { Button } from 'react-bootstrap';
import FormBoolean from 'components/Common/FormBoolean';
import styles from 'styles/components/partials.scss';

export default function HeadingForm (props) {
  const {
    isProfileActive,
    isDocumentEnable,
    hasFieldDefinitionsActive,
    isFieldsActive,
    // isProfileVerified,
    onAddFieldDefinition,
    onActiveStatusChange
    // onAccountVerificationChange
  } = props;

  // Account verified not for next release
  // <FormBoolean
  //   isActive={isProfileVerified}
  //   onChange={onAccountVerificationChange}
  //   title="Account Verification for Document"
  //   style={{marginTop: 6}} />

  return (
    <div className={'panel-heading ' + styles.rowHeading}>
      <div className="row">
        <div className="col-xs-4">
          <FormBoolean
            isActive={isProfileActive}
            isDisabled={isFieldsActive || !isDocumentEnable}
            onChange={onActiveStatusChange}
            title="Document Active Status"
            style={{marginTop: 6}} />
        </div>
        <div className="col-xs-6 text-center">
        </div>
        <div className="col-xs-2 text-right">
          <Button bsStyle="success" disabled={isFieldsActive} onClick={onAddFieldDefinition}>Add Field</Button>
        </div>
      </div>
    </div>
  );
}
