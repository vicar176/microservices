import React from 'react';
import { range } from 'lodash';
// styles
import styles from 'index.scss';

export default function SelectPage ({ onSelectPage, selectedPage, totalPages }) {
  return(
    <div className={styles.SelectPage + ' col-xs-6 text-right'}>
      <p>Page #
        <select
          className="nice-select"
          style={ {marginLeft: 10} }
          name="page"
          value={selectedPage}
          onChange={e=> onSelectPage(e.target.value)}>
          {range(1, totalPages+1).map((page, i) => {
            return <option key={i} value={page}>{page}</option>;
          })}
        </select>
      </p>
    </div>
  )
}
