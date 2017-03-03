import React from 'react';

export default function AccountStatus ({onVerificationActive,isAccountVerified,generateID}) {

  const accountId = generateID(100, 300);

  return(
    <div className="additional-info clearfix">
      <div className="col-md-6 headline-info">
        <div className="switch">
          <input
            id={"account-verified-" + accountId}
            name={"account-verified-" + accountId}
            className="switch-toggle"
            type="checkbox"
            onChange={ e => onVerificationActive(e.target.checked) }/>
          <label htmlFor={"account-verified-" + accountId}>
            <span className="status">{isAccountVerified ? 'Verified' : 'Unverified'}</span>
          </label>
        </div>
      </div>
      <div className="col-md-6 headline-info text-right">
        <span
          className="collapsable-comment"
          data-toggle="collapse"
          data-target={"#account-comment-" + accountId}
          aria-controls="collapseExample">Add comment <i style={{'marginLeft': '5px', 'marginRight': '5px'}} className="glyphicon glyphicon-collapse-down"></i></span>
      </div>
      <div className="col-md-12 collapse" id={"account-comment-" + accountId}>
        <textarea className="form-control" name="verificationComment" rows="6" />
      </div>
    </div>
  );
}
