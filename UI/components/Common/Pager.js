import React from 'react';
import ReactPager from 'react-pager';

export default function Pager ({handlePageChanged, currentPage, totalPages, isShow}) {
  return(
    <div className="row" style={ isShow? {'display': 'block'} : {'display': 'none'} }>
      <div className="col-xs-12 text-center">
        <ReactPager total={totalPages} onPageChanged={handlePageChanged} current={currentPage} visiblePages={3} titles= {{
          first: 'First',
          last: 'Last'
        }} />
      </div>
    </div>
  );
}
