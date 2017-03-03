import React from 'react';

export default function ZoomLevel ({ zoomLevel, onFitWidth, onZoomChange }) {
  return (
    <div className="ZoomLevel col-xs-6">
      <label style={ {marginRight: 5} }>Zoom:</label>
      <input
        style= { {width: 50, position: 'relative', marginRight: 10, verticalAlign: 'middle'} }
        maxLength={3}
        value={zoomLevel}
        onChange={(e) => onZoomChange(+e.target.value.trim(), zoomLevel)}
      />%
      <button
        style={ {marginLeft: 10} }
        onClick={onFitWidth} >Fit Width
      </button>
    </div>
  );
}
