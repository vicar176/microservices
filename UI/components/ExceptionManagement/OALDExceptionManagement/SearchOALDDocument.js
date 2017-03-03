import React from 'react';
import Select from 'react-select';
import CssLoad from 'components/Common/cssLoad';
import VirtualizedSelect from 'react-virtualized-select'
import 'react-select/dist/react-select.css'
import 'react-virtualized/styles.css'
import 'react-virtualized-select/styles.css'

export default function SearchOALDDocument (props) {
  const { documentList, onSelectDocumentList, onSearchDocuments, selectedDocumentList, isFetchingDocumentList, selectedReviewMethod } = props;
  const isPortfolioMethod = selectedReviewMethod === 'byPortfolio';
  const label = isPortfolioMethod ? 'Portfolio Number' : 'Document ID';
  // const value = (selectedReviewMethod === 'byPortfolio') ? selectedDocumentList.value : selectedDocumentList;
  return(
    <div className="form-group">
      <div className="row">
        <div className="col-xs-10">
          <label className="control-label">{label}:</label>
          { isFetchingDocumentList ? <CssLoad /> :
            // <Select
            //   name="documentList"
            //   style={ { width: '100%', zIndex: '1000' } }
            //   valueKey="value"
            //   labelKey="value"
            //   multi={true}
            //   placeholder={`Select ${label}`}
            //   value={selectedDocumentList}
            //   onChange={onSelectDocumentList}
            //   // onBlur={onBlur}
            //   options={documentList}
            // />
            <VirtualizedSelect
              name="documentList"
              style={ { width: '100%', zIndex: '1000' } }
              options={documentList}
              multi={true}
              onChange={onSelectDocumentList}
              placeholder={`Select ${label}`}
              value={selectedDocumentList}
              valueKey="value"
              labelKey="value"
            />
          }
        </div>
        <div className="col-xs-2" style={{top: "1.7em", position: 'relative'}}>
          <button className="btn btn-block btn-primary" onClick={onSearchDocuments} disabled={documentList.length === 0}>Search</button>
        </div>
      </div>
      <hr />
    </div>
  );
}
