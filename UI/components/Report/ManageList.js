import React from 'react';

export default function ManageList ({manageListId, title, currentNewName, currentNewNameChange, currentList, addItem, editItem, removeItem}) {
  return (
    <div className="modal fade" id={manageListId}>
      <div className="modal-dialog" role="document">
        <div className="modal-content">
          <div className="modal-header">
            <div type="button" className="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">&times;</span>
            </div>
            <h1 className="modal-title" style={{textAlign: 'center'}}>{title}</h1>
          </div>
          <div className="modal-body">
            <div className="row sec-divider">
              <div className="col-md-10">
                <input className="form-control" value={currentNewName} onChange={ e => currentNewNameChange(e.target.value.trim())} />
              </div>
              <div className="col-md-2">
                <button
                  type="button"
                  className="btn btn-default"
                  onClick={ e => {
                    e.preventDefault();
                    addItem(currentNewName)
                  }}>Add</button>
              </div>
            </div>
            {
              currentList.map((item, i) => {
                return (
                <div className="row" key={i}>
                  <div className="col-md-8">
                    <input className="form-control" value={item.name} onChange={ (e) => editItem(item.id ,e.target.value.trim())} />
                  </div>
                  <div className="col-md-2">
                    <button type="button" className="btn btn-default" onChange={ () => console.log('save to be')}>Save</button>
                  </div>
                  <div className="col-md-2">
                    <button type="button" className="btn btn-default" onChange={ () => removeItem(item.id)}>X</button>
                  </div>
                </div>
                )
              })
            }
          </div>
          <div className="modal-footer" style={{textAlign: 'center'}}>
            <button type="button" className="btn btn-default" data-dismiss="modal">Done</button>
          </div>
        </div>
      </div>
    </div>
  );
}
