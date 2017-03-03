import React from 'react';

export default function Input (props) {
  let wrapperClass = 'form-group';
  if (props.error && props.error.length > 0) {
    wrapperClass += ' has-error';
  }
  return (
    <div>
      <div className="col-lg-offset-1 col-lg-2">
        <label htmlFor={props.name}>{props.label}</label>
      </div>
      <div className={wrapperClass + ' col-lg-4'}>
        <div className="input-group">
          <input type="text"
            name={props.name}
            className="form-control"
            placeholder={props.placeholder}
            ref={props.name}
            value={props.value}
            onChange={props.onChange} />
            {
              props.btnName &&
                <span className="input-group-btn">
                  <button className="btn btn-secondary" type="button" onClick={props.onChange}>{props.btnName}</button>
                </span>
            }
          <div className="input">{props.error}</div>
        </div>
      </div>
    </div>
  )
}
