import React from 'react';
import styles from './PreviousVersion.scss';

export default function PreviousMode ({ isShow, userName, date, selectedPreviousVersion, onApplyVersion, onCancelPreview }) {
  return (
    <div className={styles.previousMode} style={ isShow ? {'display': 'block'} : {'display': 'none'} }>
      <div className="row">
        <h3 className="col-xs-3">Preview Mode - Version {selectedPreviousVersion}</h3>
        <h3 className="col-xs-5 text-center">Updated by: {userName} - {date}</h3>
        <div className="col-xs-4 text-right">
          <button name="applyVersion" className="btn btn-default" onClick={onApplyVersion}>Apply Version</button>
          <button name="cancelPreview" className="btn btn-default" onClick={onCancelPreview}>Cancel Preview</button>
        </div>
      </div>
    </div>
  );
}
