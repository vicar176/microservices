import React from 'react';
// import { ButtonGroup, Button } from 'react-bootstrap';
// import styles from 'styles/commons/forms.scss';

export default function FormBoolean ({
  title,
  style,
  isActive,
  onChange,
  className = 'form-boolean',
  isDisabled = false
}) {
  return (
    <div className={className + (isDisabled ? ' is-disabled' : '')}>
      <div style={style}>
        <strong>{title}</strong>
        <div className="btn-group btn-group-xs">
          <label className={"btn btn-default " + (isActive ? 'active' : '')}>
            <input
              type="radio"
              value={true}
              checked={isActive}
              disabled={isDisabled}
              onChange={() => onChange(true)} /> Yes
          </label>
          <label className={"btn btn-default " + (isActive ? '' : 'active')}>
            <input
              type="radio"
              value={false}
              checked={!isActive}
              disabled={isDisabled}
              onChange={() => onChange(false)} /> No
          </label>
        </div>
      </div>
    </div>
  );
}
