import React from 'react';
// styles
import styles from './index.scss';

export default function GridRow ({ isFetching, rows, thead }) {
  function getEmptyList () {
    return(
      <tr className="grid-row text-center">
        <td colSpan={thead.length}>
          {isFetching ? 'Requesting data, please wait...' : 'No data available.'}
        </td>
      </tr>
    );
  }

  function getColumnList () {
    const rowCells = rows.map((row, i) => {
      return(
        <tr key={i} className="grid-row">
          { thead.map((col, pos) => {
            const key = i+'-'+pos;
            Object.assign(row, { text: row[col.key], rowId: key});
            return (
              <td key={key} className={"grid-col" + (col.className ? ` ${col.className}` : '')}>
                {(typeof col.rowFormatter === 'function') ? col.rowFormatter(row) : row[col.key]}
              </td>
            );
          })}
        </tr>
      );
    })
    return rowCells;
  }

  return (
    <tbody className={styles.gridContent}>
      { rows.length > 0 ? getColumnList() : getEmptyList() }
    </tbody>
  );
}
