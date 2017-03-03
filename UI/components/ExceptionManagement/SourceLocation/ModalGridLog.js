import React from 'react';

export default function ModalGridLog (props) {
  return (
    <table className="table table-striped table-log">
      <thead>
        <tr>
          <th>Error Run Date</th>
          <th>Error Code</th>
          <th>Error Description</th>
          <th>Execution Type</th>
        </tr>
      </thead>
      <tbody>
        { props.profileLogs.map((log, i) =>
          <tr key={i}>
            <td>{log.errorDate}</td>
            <td>{log.errorCode}</td>
            <td>{log.errorDetail}</td>
            <td>{log.executionType}</td>
          </tr>)
        }
      </tbody>
    </table>
  );
}
