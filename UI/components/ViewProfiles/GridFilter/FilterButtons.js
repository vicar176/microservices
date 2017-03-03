import React from 'react';
import styles from '../index.scss';

export default function ({ onFilter, onCleanAllFilters, colSpan }) {
  return (
    <th colSpan={colSpan}>
      <button
        className={`${styles.filterBtn} btn btn-primary`}
        onClick={onFilter}>
        Filter
      </button>
      <button
        className={`${styles.filterBtn} btn btn-default`}
        onClick={onCleanAllFilters}>
        Clean
      </button>
    </th>
  )
}
