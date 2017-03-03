import React from 'react';

export default function selectProfile (props) {
  const { profileName, accessType, isNameAvailable, onProfileSelect, isDisabled } = props;
  return (
    <div className="col-xs-4">
      <label className="control-label">Select Profile:</label>
      <select
        id="profileMethod"
        name="profileMethod"
        className="form-control"
        defaultValue={accessType}
        disabled={!(isNameAvailable && profileName) || isDisabled}
        onChange={onProfileSelect}>
        <option value="">Select Profile Location</option>
        <option value="ftp">FTP</option>
      </select>
    </div>
  );
}
