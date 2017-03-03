import React from 'react';

export default function ControlsCTASection () {
  return(
    <div className="row">
      <div className="col-md-12">
        <nav>
          <ul className="pager pager-bottom">
            <li>
              <button className="btn btn-default">Cancel</button>
            </li>
            <li>
              <button className="btn btn-primary">Save</button>
            </li>
          </ul>
        </nav>
      </div>
    </div>
  );
}
