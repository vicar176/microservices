import React from 'react';
import moment from 'moment';

// Components
import CssLoad from 'components/Common/cssLoad';
import styles from '../index.scss';

export default function TableTab ({ getValues, isFetchingValues, values }) {

  function getRowHead() {
    return Object.keys(values[0]).map((key) => {
      return <td key={key}>{key}</td>;
    });
  }

  function getColumnList () {
    const rowCells = values.map((row, i) => {
      return(
        <tr key={i} className={ (row["Document Status"] === "Failed" || row["Status"] === "Failed") && styles.rowError}>
          { Object.keys(values[0]).map((col, pos) => {
            const key = i+'-'+pos;
            return (
              <td key={key} className="grid-col">
                { col.toLowerCase().includes("date") || col.toLowerCase().includes("time") ? moment(row[col]).utc().format('MMMM Do YYYY, hh:mm:ss a') : row[col]}
              </td>
            );
          })}
        </tr>
      );
    })
    return rowCells;
  }

  return(
  <div className="row">
      <div className="col-xs-12 text-center" style={{ "marginTop": 20 }}>
        <button
          name="save"
          className="btn btn-primary"
          onClick={getValues}>
          Refresh
        </button>
      </div>
      <hr/>
      { isFetchingValues ? <CssLoad/> :
        values.length > 0 ?
        <div className="col-xs-12">
          <table className="table table-bordered table-striped table-hover">
            <thead>
              <tr>
              { getRowHead() }
              </tr>
            </thead>
            <tbody>
              { values.length > 0 && getColumnList()}
            </tbody>
          </table>
        </div>
        :
        <div className="col-xs-12">
          <p className="text-center">No Data.</p>
        </div>
      }
  </div>
  );
}
