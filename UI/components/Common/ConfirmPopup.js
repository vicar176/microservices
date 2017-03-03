import React from 'react';

export default function ConfirmPopup (props) {
  const modalClass = props.customClass ? props.customClass + ' modal-dialog' : 'modal-dialog';
  const onConfirmClick = props.onConfirm || (() => {});
  const onCancelClick = props.onCancel || (() => {});

  return (
    <div className="modal fade" id={props.confirmId}>
      <div className={modalClass} role="document">
        <div className="modal-content">
          { props.title &&
            <div className="modal-header">
              <div type="button" className="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </div>
              <h1 className="modal-title">{props.title}</h1>
            </div>
          }
          <div className="modal-body">
            <div className="text-xs-center">{props.content}</div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-default" data-dismiss="modal" onClick={onCancelClick}>{props.cancelButtonText || 'No'}</button>
            <button type="button" className="btn btn-success" onClick={onConfirmClick}>{props.confirmButtonText || 'Yes'}</button>
          </div>
        </div>
      </div>
    </div>
  );
}
