import React from 'react';
import { Tooltip, OverlayTrigger} from 'react-bootstrap';

// Components
import Switch from 'components/Common/Switch';
import IngestionSwitch from 'components/ExceptionManagement/IngestionSwitch';
// style
import styles from 'index.scss';

export default function Utils (props) {
  const {
    activitiesStatus,
    documentProcesorStatus,
    onSwitchChange,
    onLevelChange
  } = props;

  return (
    <div className={`${styles.utilsList} list-group`}>
      <div className="list-group-item">
        <label htmlFor="ingestion-switch">Turn off/on ingestion workflow:</label>
        <div className="pull-right" style={{ marginRight: '-7px' }}>
            <IngestionSwitch />
        </div>
      </div>
      <div className="list-group-item">
        <label htmlFor="documents">Turn off/on document processor:</label>
        <div className="pull-right">
          <Switch
            id="documents"
            isShoutDown={!documentProcesorStatus}
            onSwitchChange={onSwitchChange}
          />
        </div>
      </div>
      <div className="list-group-item">
        <label htmlFor="activities">Clear workflow activities:</label>
        <div className="pull-right">
          <Switch
            id="activities"
            isShoutDown={activitiesStatus}
            onSwitchChange={onSwitchChange}
          />
        </div>
      </div>
      <div className="list-group-item">
        <label htmlFor="logType">Log Level:</label>
        <div className="pull-right">
          <select className="form-control" onChange={onLevelChange}>
            <option value="" default>Select</option>
            <option value="error">Error</option>
            <option value="warn">Warn</option>
            <option value="info">Info</option>
            <option value="debug">Debug</option>
          </select>
        </div>
      </div>
    </div>
  );
}
