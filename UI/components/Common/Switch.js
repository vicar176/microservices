import React from 'react';

// styles
import styles from './Switch.scss';

export default function Switch ({ id, isShoutDown, onSwitchChange }) {
  const switchId = id ? id : `switch-${Date.now()}`;
  return (
    <div className={styles.globalSwitch}>
      <div className="switch">
        <input
          id={switchId}
          className="switch-toggle"
          type="checkbox"
          checked={isShoutDown}
          onChange={onSwitchChange} />

        <label htmlFor={switchId}>
          {/* <span className="status"></span> */}
        </label>
      </div>
    </div>
  );
}
