import React from 'react';

export default function ControlsListPager () {
  return(
    <div className="form-group">
      <div className="row">
        <div className="col-xs-12">
          <nav>
            <ul className="pager">
              <li>
                <button className="btn btn-primary"><i className="glyphicon glyphicon-menu-left"></i> Previous Account</button>
              </li>
              <li>
                <button className="btn btn-primary">Next Account <i className="glyphicon glyphicon-menu-right"></i></button>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>
  );
}
