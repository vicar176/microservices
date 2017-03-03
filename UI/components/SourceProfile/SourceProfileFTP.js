import React from 'react';
import { Tooltip, OverlayTrigger} from 'react-bootstrap';

export default function sourceProfileFTP(props) {
  const {
    onEnterLocation,
    onEnterPort,
    onEnterUsername,
    onEnterPassword,
    onEnterFileLocation,
    onEnterZipPassword,
    onCheckFileType,
    isFileZipped,
    sourceProfile,
    isDisabled
  } = props;
  const passwordTip = <Tooltip id="passwordTip">Leave blank if password is not required</Tooltip>;
  const locationTip = <Tooltip id="locationTip">Leave blank if login directs to default file location</Tooltip>;

  return (
    <div className="form-group">
      <hr />
      <div className="row">
        <div className="col-xs-10">
          <label className="control-label">Enter host location:</label>
          <input
            id="hostLocation"
            name="hostLocation"
            type="text"
            placeholder="Location"
            className="form-control"
            disabled={isDisabled}
            defaultValue={sourceProfile.hostLocation}
            onBlur={onEnterLocation}/>
        </div>
        <div className="col-xs-2">
          <label className="control-label">Enter Port:</label>
          <input
            id="port"
            name="port"
            type="text"
            placeholder="Port"
            className="form-control"
            disabled={isDisabled}
            defaultValue={sourceProfile.hostPort}
            onBlur={onEnterPort} />
        </div>
      </div>
      <hr />
      <div className="row">
        <div className="col-xs-4">
          <label className="control-label">Username:</label>
          <input
            id="username"
            name="username"
            type="text"
            placeholder="Username"
            className="form-control"
            disabled={isDisabled}
            defaultValue={sourceProfile.username}
            onBlur={onEnterUsername} />
        </div>
        <div className="col-xs-4">
          <label className="control-label">Password:</label>
          <input
            id="password"
            name="password"
            type="password"
            placeholder="Password"
            className="form-control"
            disabled={isDisabled}
            defaultValue={sourceProfile.password}
            onBlur={onEnterPassword} />
        </div>
        <div className="col-xs-4">
          <label className="control-label">
            <span>Remote File location: </span>
            <OverlayTrigger placement="top" overlay={locationTip}>
              <i className="glyphicon glyphicon-info-sign" />
            </OverlayTrigger>
          </label>
          <input
            id="fileLocation"
            name="fileLocation"
            type="text"
            placeholder="File Location"
            className="form-control"
            disabled={isDisabled}
            defaultValue={sourceProfile.fileLocation}
            onBlur={onEnterFileLocation} />
        </div>
      </div>
      <hr />
      <div className="row">
        <div className="col-xs-6">
          <label className="control-label">Zipped File:</label>
          <select
            id="zippedFile"
            name="zippedFile"
            className="form-control"
            disabled={isDisabled}
            defaultValue={isFileZipped}
            onChange={e => onCheckFileType(e.target.value)}>

            <option value={0}>No</option>
            <option value={1}>Yes</option>
          </select>
        </div>
        <div className="col-xs-6">
          <label className="control-label">
            <span>Zipped File Password: </span>
            <OverlayTrigger placement="top" overlay={passwordTip}>
              <i className="glyphicon glyphicon-info-sign" />
            </OverlayTrigger>
          </label>
          <input
            id="zipPassword"
            name="zipPassword"
            className="form-control"
            type="password"
            placeholder="Password"
            disabled={(isFileZipped === '0' || isDisabled) ? true : false}
            defaultValue={sourceProfile.zipPassword}
            onBlur={onEnterZipPassword} />
        </div>
      </div>
      <hr />
    </div>
  );
}
