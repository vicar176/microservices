import React from 'react';
import { OverlayTrigger, Popover } from 'react-bootstrap';
import CssLoad from 'components/Common/cssLoad';
import DatePicker from 'react-datepicker';
import moment from 'moment';

import 'react-datepicker/dist/react-datepicker.css';

export default function OALDDocument ({
  documents,
  onUpdateExceptionField,
  onViewDataExtracted,
  onUpdateDataExtracted,
  showDocumentModal,
  isFetchingDocument
}) {
  return (
    <div>
      { !!documents.length &&
        documents.map((document, docKey) => {
          return (
            <div key={docKey} className="table-responsive">
              <table className="table table-bordered table-striped">
                <caption className="sub-header">
                  Data from Document: <strong>{document.extraction.template.name}</strong>
                  <button className="btn btn-default pull-right" onClick={() => onUpdateDataExtracted(document)}>Update</button>
                  <button className="btn btn-default pull-right" onClick={onViewDataExtracted}>View Data</button>
                </caption>

                <thead>
                  <tr>
                    <th className="col-sm-4"><strong>Document ID:</strong> <a href="#" onClick={(e) => showDocumentModal(e, document)}>{document.documentId}</a></th>
                    <th className="col-sm-4"><strong>Document Type:</strong> {document.originalDocumentType.code}</th>
                    <th className="col-sm-4"><strong>Original Account Number:</strong> {document.document.documentName.originalAccountNumber}</th>
                  </tr>
                </thead>
                <tbody>
                  { isFetchingDocument === document.documentId ?
                    <tr>
                      <td colSpan="5">
                        <CssLoad />
                      </td>
                    </tr> :
                  <tr>
                    <td colSpan="3" className="metadata-content">
                      <table className="table">
                        <caption className="text-center">Values from Metadata Extraction</caption>
                        <tbody>
                          <tr className="document-labels">
                            { !document.dataElements.length ? <td className="text-center">No Values</td> :
                              document.dataElements.map((field, fieldKey) => {
                                return(
                                  <td key={fieldKey}>
                                    <div>
                                      <label htmlFor={field.fieldDefinition.fieldName}>
                                        {field.fieldDefinition.fieldName}
                                        {field.fieldDefinition.fieldRequired && <sup style={{color: '#f00'}}>*</sup>}:
                                      </label>

                                      { field.fieldDefinition.fieldType === 'date' ?
                                        <DatePicker
                                          showYearDropdown
                                          placeholderText={field.value}
                                          // selected={moment(field.value).isValid() ? moment(field.value) : {}}
                                          className={"form-control "+ (!field.validated ? 'error' : 'valid')}
                                          onChange={e => onUpdateExceptionField(e, docKey, field.fieldDefinition.fieldName)} />
                                        :
                                        <input
                                          type="text"
                                          value={field.value}
                                          id={field.fieldDefinition.fieldName}
                                          className={"form-control "+ (!field.validated ? 'error' : 'valid')}
                                          onChange={e => onUpdateExceptionField(e, docKey, field.fieldDefinition.fieldName)} />
                                      }

                                      <OverlayTrigger id={field.fieldDefinition.fieldName + '-popoverTrigger'} trigger={['hover','focus']} placement={(document.dataElements.length === fieldKey+1 ? 'left' : 'top')}
                                        overlay={<Popover id={field.fieldDefinition.fieldName + '-popover'} title="Extraction Snippet:"><img src={field.snipet} style={{ "maxWidth": "100%" }}/></Popover>}>
                                        <span className="snippet-wrap">
                                          <img src={field.snipet} title={field.fieldDefinition.fieldDescription}/>
                                        </span>
                                      </OverlayTrigger>
                                    </div>
                                  </td>
                                );
                              })
                            }
                          </tr>
                        </tbody>
                      </table>
                    </td>
                  </tr>
                  }
                </tbody>
              </table>
              { documents.length !== docKey && <hr /> }
            </div>
          )
        })
      }
    </div>
  )
}
