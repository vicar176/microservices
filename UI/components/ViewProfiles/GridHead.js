import React, { Component } from 'react';
import GridFilter from 'GridFilter';
import Styles from './index.scss';

export default function GridHeader ({
  columns,
  isFilterActive,
  onSortColumn,
  getCurrentRowsPage,
  sortOrientation,
  renderFilterButton
}) {
  return (
    <thead className={Styles.gridHeader}>
      <tr className="detail-row">
        {columns.map((col, i) => {
          const key = col.sortcol || col.query || col.key;
          return (
            <th key={i} className={(col.className ? col.className : null)} style={col.style}>
              {(() => {
                if (col.btnHead === 'filter') {
                  return renderFilterButton()
                }
                if (col.sortable) {
                  // (sortOrientation === key || sortOrientation === `-${key}`)
                  return (
                    <a id={col.key} data-filter={col.query} data-sortcol={col.sortcol} href="#" className="sortable" onClick={onSortColumn}>
                      {col.name}
                      <span className={Styles.sortIndicators}>
                        <i data-sortcol={col.sortcol} data-filter={col.query} id={col.key} className={'glyphicon ' + (sortOrientation === `-${key}` ? 'active' : 'inactive') + ' glyphicon-chevron-up'}></i>
                        <i data-sortcol={col.sortcol} data-filter={col.query} id={col.key} className={'glyphicon ' + (sortOrientation === key ? 'active' : 'inactive') + ' glyphicon-chevron-down'}></i>
                      </span>
                    </a>
                  )
                }
                return <span>{col.name}</span>;
              })()}
            </th>
          );
        })}
      </tr>
      <GridFilter columns={columns} getCurrentRowsPage={getCurrentRowsPage} isShow={isFilterActive} />
    </thead>
  );
}
