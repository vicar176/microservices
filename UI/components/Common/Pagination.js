import React from 'react';
import Pager from 'components/Common/Pager';
import toastr from 'toastr';

// styles
import styles from 'styles/components/partials.scss';

export default function Pagination({
  totalItems,
  totalPages,
  currentPage,
  itemsPerPage,
  dropdownItems,
  onPageChanged,
  onItemsPerPageChange
}) {
  const sizes = dropdownItems.split(',');
  let prevVal = 0;
  return (
    <div className={styles.paginationWrap + " row"}>
      <div className="col-sm-8 col-sm-offset-2">
        <Pager
          isShow={totalPages > 1}
          totalPages={totalPages}
          currentPage={currentPage}
          handlePageChanged={e => {
            onPageChanged(e);
            toastr.clear();
          }} />
      </div>
      <div className="col-sm-2">
        <select
          name="pager-count"
          className={styles.pagerCounter + " form-control"}
          value={itemsPerPage || sizes[0]}
          onChange={onItemsPerPageChange}
          style={{display: totalItems <= sizes[0] ? 'none' : 'block'}}>
          { sizes.map((val, i) => {
            const isDisabled = totalItems<prevVal;
            prevVal = val;
            return <option value={val} key={i} disabled={isDisabled}>{val}</option>
          })}
        </select>
      </div>
    </div>
  );
}
