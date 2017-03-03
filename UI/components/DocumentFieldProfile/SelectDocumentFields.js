import React from 'react';
import Select from 'react-select';
import CssLoad from 'components/Common/cssLoad';

export default function SelectDocumentFields ({ documentProfileDropdown, isFetchingDocuments, selectedDocument, onSelectDocument }) {
  return(
    <form id="SearchDocField" method="post">
      <div className="form-group">
        <div className="row">
          <div className="col-xs-12">
            <label className="control-label">Select Document Type</label>
            { isFetchingDocuments ? <CssLoad /> :
              <Select
                name="SelectDocuments"
                valueKey="id"
                labelKey="id"
                placeholder="Select Document Type"
                multi={false}
                options={documentProfileDropdown}
                value={selectedDocument}
                onChange={onSelectDocument}
                /> }
          </div>
        </div>
      </div>
    </form>
  );
}
