import React from 'react';
import AccountStatus from 'AccountStatus';

export default function AccountDetail ({
  generateID,
  onVerificationActive,
  onEditAccountField,
  isAccountVerified,
  editProfile
}) {
  return(
    <div className="content-detail">
      <table className="table table-bordered table-striped">
        <caption className="sub-header clearfix">
          <div className="col-md-4 text-left">Account Number: 8550000001</div>
          <div className="col-md-4 text-center">Portfolio Number: 1555</div>
          <div className="col-md-4 text-right">Seller: Citibank</div>
        </caption>
        <thead>
          <tr className="detail-row">
            <th>Account and Document field values:</th>
            <th>Account Data Values</th>
            <th>Document MetaData Values <span className="pull-right edit">(<a href="#">View data</a>)</span></th>
            <th className="text-center">Document Type</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Original Account #:</td>
            <td>672900390178715</td>
            <td>672900390178715 <span className="pull-right edit">(<a href="#">Edit</a>)</span></td>
            <td className="text-center">GoodBye Letter
              <a
                href="#"
                style={{'marginLeft': '5px'}}
                className="glyphicon glyphicon-list-alt"
                data-toggle="modal"
                data-target=".bs-example-modal-lg"></a>
            </td>
          </tr>
          <tr className="error">
            <td>Consumer Name:</td>
            <td>Chris Gorman</td>
            <td>
              <span className="error-box">
                <span>Christopher Gorman</span>
                <input type="text"/>
              </span>
              <span
                className="pull-right edit"
                onClick={ e => onEditAccountField(e.target) }>(<i>{editProfile ? 'Update' : 'Edit'}</i>)</span>
            </td>
            <td></td>
          </tr>
          <tr>
            <td>Charge Off Balance:</td>
            <td>$ 169.95</td>
            <td>$ 169.95 <span className="pull-right edit">(<a href="#">Edit</a>)</span></td>
            <td></td>
          </tr>
        </tbody>
        <tfoot>
          <tr>
            <td colSpan="4">
              <AccountStatus
                generateID={generateID}
                onVerificationActive={onVerificationActive}
                isAccountVerified={isAccountVerified} />
            </td>
          </tr>
        </tfoot>
      </table>
    </div>
  );
}
