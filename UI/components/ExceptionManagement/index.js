import React from 'react';
import { connect } from 'react-redux';

// components
import MainContent from 'components/Common/MainContent';
import IngestionSwitch from './IngestionSwitch';
import ExtractionExceptions from './ExtractionExceptions';
// import IngestionReprocessing from './IngestionReprocessing';
import OALDExceptionManagement from './OALDExceptionManagement/OALDExceptionManagement';
import AccountExceptionManagement from './AccountExceptionManagement/AccountExceptionManagement';
import SourceLocation from './SourceLocation/SourceLocation';

function ExceptionManagement (props) {
  const { selectedTab } = props.common;
  const tabs = [
    { id: 'Extraction', name: 'Extraction Exceptions' },
    { id: 'OALD', name: 'OALD Validation' }
  ];
    // { id: 'ingestionReprocessing', name: 'Ingestion Reprocessing' }];
    // { id: 'sourceLocation', name: 'Source Location' }];

  return (
    <MainContent routes={props.routes} mainTitle='Exception Management' tabs={tabs}>
      <IngestionSwitch
        label="Turn off/on ingestion workflow:"
        style={ { position: 'absolute', right: '1em', top: '-39px' } }
      />

      <div role="tabpanel" className="tab-pane active" id="Extraction">
        <h2 className="page-header">Extraction Exceptions</h2>
        { (selectedTab === 'Extraction') && <ExtractionExceptions /> }
      </div>
      <div role="tabpanel" className="tab-pane" id="OALD">
        <h2 className="page-header">OALD Validation</h2>
        { (selectedTab === 'OALD') && <OALDExceptionManagement /> }
      </div>
      {/* <div role="tabpanel" className="tab-pane" id="ingestionReprocessing">
        <h2 className="page-header">Ingestion Reprocessing</h2>
        { (selectedTab === 'ingestionReprocessing') && <IngestionReprocessing /> }
      </div> */}
      <div role="tabpanel" className="tab-pane" id="Account">
        <h2 className="page-header">Account Verification</h2>
        { (selectedTab === 'Account') && <AccountExceptionManagement /> }
      </div>
      <div role="tabpanel" className="tab-pane" id="SourceLocation">
        <h2 className="page-header">Account Verification</h2>
        { (selectedTab === 'SourceLocation') && <SourceLocation /> }
      </div>
    </MainContent>
  );
}

function mapStateToProps(state) {
  return {
    common: state.common
  };
}

export default connect(mapStateToProps)(ExceptionManagement);
