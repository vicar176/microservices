import React from 'react';
import CssLoad from 'components/Common/cssLoad';

export default function AddAffinities ({
  onAddAffinities,
  hasAffinities,
  isFetching,
  isDisabled,
  addAffinities
}) {
  return (
    <div className={"form-boolean" + (isDisabled ? ' is-disabled' : '')}>
      <strong>Associate Affinities:</strong>
      { isFetching ? <CssLoad /> :
        hasAffinities ?
        <span className="btn-group btn-group-xs" style={{ margin: '11px 0 0 0', display: 'block' }}>
          <label className={"btn btn-default "+(addAffinities === 'yes' ? 'active' : '')} htmlFor="addAffinitiesYes">
            <input
              value="yes"
              type="checkbox"
              name="addAffinites"
              id="addAffinitiesYes"
              disabled={isDisabled}
              checked={addAffinities === 'yes'}
              onChange={ e => onAddAffinities(e.target.value) } />
            Yes
          </label>
          <label className={"btn btn-default "+(addAffinities === 'no' ? 'active' : '')} htmlFor="addAffinitiesNo">
            <input
              value="no"
              type="checkbox"
              name="addAffinites"
              id="addAffinitiesNo"
              disabled={isDisabled}
              checked={addAffinities === 'no'}
              onChange={ e => onAddAffinities(e.target.value) } />
            No
          </label>
        </span>
        :
        <p className="info" style={{ marginTop: 15 }}>No affinities available.</p>
      }
    </div>
  );
}
