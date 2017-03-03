import React from 'react';
import Styles from 'index.scss';
import { Tooltip, OverlayTrigger} from 'react-bootstrap';

export default function locationProfile (props) {
  const { onTypeProfileName, isNameAvailable, profileName, isEditModeActive, isDisabled } = props;
  const availableTip = (<Tooltip id="availableTip">{isNameAvailable ? "Available Name" : "Unavailable Name"}</Tooltip>);
  return (
    <div className="col-xs-8">
      <label className="control-label" style={{'display': 'block'}}>
        Enter Profile Name<span className="text-error">*</span>:
      </label>
      <div className={Styles.validationIcon}>
        <input
          id="portfolioName"
          name="portfolioName"
          type="text"
          className="form-control"
          value={profileName}
          disabled={isDisabled || isEditModeActive ? true : false}
          placeholder="Example of taken names: Citibank, Synchorny, Capital One"
          onChange={onTypeProfileName}/>
        { profileName &&
          <OverlayTrigger placement="left" overlay={availableTip}>
            <i className={(isNameAvailable ? "glyphicon-ok-circle text-success" : "glyphicon-ban-circle text-error") + " glyphicon"} />
          </OverlayTrigger> }
      </div>
    </div>
  );
}
