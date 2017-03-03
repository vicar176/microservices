import React from 'react';

export default function UploadSample ({ onFileChange, createNewTemplate, isTemplateNameValid, sampleTemplateName, isDisabled}) {
  function getClass(val) {
    return val ? 'glyphicon-saved' : 'glyphicon-open';
  }

  function onChange (e) {
    const val = e.target.value;
    const filename = val.split('\\');

    $('.btn-upload-filename').html(filename[filename.length-1]);
    $('.fileicon').addClass(getClass(val)).removeClass(getClass(!val));
    onFileChange(e.target.files[0],filename[filename.length-1]);
  }

  return (
    <div key={createNewTemplate} className="required" style={{marginTop: '2em'}}>
      <label>Upload Sample Template:</label>
      <div className="inline-group">
        <label className="btn-upload">
          <span className="btn-upload-label btn btn-primary"><i className={"fileicon glyphicon " + (!sampleTemplateName ? 'glyphicon-open' : 'glyphicon-saved')}/> Choose a file...</span>
          <span className="btn-upload-filename">{sampleTemplateName}</span>
          <input disabled={!isTemplateNameValid}
            type="file"
            name="file"
            disabled={isDisabled}
            onChange={onChange}/>
          </label>
      </div>
    </div>
  );
}
