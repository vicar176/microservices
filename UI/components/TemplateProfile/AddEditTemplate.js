import React from 'react';
import CssLoad from 'components/Common/cssLoad';

export default function AddEditTemplate ({
  // templates,
  onCreateNew,
  addAffinities,
  isFetchingProfile,
  createNewTemplate,
  isDisabled,
  hasAvailableAffinities
}) {
  const isShow = addAffinities === 'yes' && hasAvailableAffinities;
  return (
    <div
      className={"form-boolean " + (isDisabled ? 'is-disabled' : '')}
      style={{display: isShow ? 'block' : 'none'}} >
      <strong className="status">Create New Template:</strong>
      { isFetchingProfile ? <CssLoad style={{marginTop: 20, width: '90%'}} /> :
        <span className="btn-group btn-group-xs" style={{ margin: '11px 0 0 0', display: 'block' }}>
          <label className={"btn btn-default "+(createNewTemplate ? 'active' : '')} htmlFor="newTemplate">
            <input
              value="yes"
              type="checkbox"
              name="newTemplate"
              id="newTemplate"
              checked={createNewTemplate}
              disabled={isDisabled}
              onChange={() => onCreateNew(true)} />
            Yes
          </label>
          <label className={"btn btn-default "+(!createNewTemplate ? 'active' : '')} htmlFor="existingTemplate">
            <input
              value="no"
              type="checkbox"
              name="existingTemplate"
              id="existingTemplate"
              checked={!createNewTemplate}
              disabled={isDisabled}
              onChange={() => onCreateNew(false)} />
            No
          </label>
        </span>
      }
      {/* <label>
        <input
          name="test"
          className="switch-toggle"
          type="checkbox"
          disabled={isDisabled}
          checked={createNewTemplate}
          onChange={ e => onCreateNew(e.target.checked)}/>
      </label> */}
    </div>
  );
}
