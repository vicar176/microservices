import styles from './index.scss';
import React, { Component } from 'react';
import classNames from 'classnames';

class AreaField extends Component {

  render() {
    const {
      selected,
      id,
      coords,
      text,
      isFetching,
      required,
      mapped,
      fieldDefinition
    } = this.props.area;

    const { fieldName } = fieldDefinition;

    const { onSelect, onReset, onCoordChange, isDisabled } = this.props;

    let contentClass = {};
    contentClass[styles.fieldContent] = true;
    contentClass[styles.fieldVisible] = selected;
    contentClass = classNames(contentClass);

    let wrapperClass = {};
    wrapperClass[styles.fieldWrapper] = true;
    wrapperClass[styles.fieldSelected] = selected;
    wrapperClass = classNames(wrapperClass);

    const { x=0, y=0, w=0, h=0 } = (coords || {});

    return (
      <li className={wrapperClass+' list-group-item'} onClick={ () => onSelect(id)}>
        <a>
          <span className={styles.areaId} style={ required? {'backgroundColor': '#f56363'} : {}}>{this.props.areaIndex}</span>
          &nbsp;
          <div
            className={styles.areaFieldTitle}>
            {fieldName}
            {required && <sup className={styles.required}>*</sup>}
            {mapped && <span className={styles.mapped}><i className="glyphicon glyphicon-ok-circle"></i></span>}
          </div>
        </a>

        <div className={contentClass + ' row'}>
          <div className="col-xs-12">
            <div className="row form-inline">
              <span className={styles.fieldZone + ' col-xs-6'}>
                <span className="input-group">
                  <span className={styles.fieldCoord + ' input-group-addon'}>X: </span>
                  <input
                    className={styles.fieldValue + ' form-control'}
                    onChange={(e)=> onCoordChange('x', e.target.value)}
                    type="number"
                    value={Math.round(x)}
                    disabled={isDisabled}
                  />
                </span>
              </span>
              <span className={styles.fieldZone + ' col-xs-6'}>
                <span className="input-group">
                  <span className={styles.fieldCoord + ' input-group-addon'}>Y: </span>
                  <input
                    className={styles.fieldValue + ' form-control'}
                    onChange={(e)=> onCoordChange('y', e.target.value)}
                    type="number"
                    value={Math.round(y)}
                    disabled={isDisabled}
                  />
                </span>
              </span>
            </div>
            <div className="row form-inline">
              <span className={styles.fieldZone + ' col-xs-6'}>
                <span className="input-group">
                  <span className={styles.fieldCoord + ' input-group-addon'}>W: </span>
                  <input
                    className={styles.fieldValue + ' form-control'}
                    onChange={(e)=> onCoordChange('w', e.target.value)}
                    type="number"
                    value={Math.round(w)}
                    disabled={isDisabled}
                  />
                </span>
              </span>
              <span className={styles.fieldZone + ' col-xs-6'}>
                <span className="input-group">
                  <span className={styles.fieldCoord + ' input-group-addon'}>H: </span>
                  <input
                    className={styles.fieldValue + ' form-control'}
                    onChange={(e)=> onCoordChange('h', e.target.value)}
                    type="number"
                    value={Math.round(h)}
                    disabled={isDisabled}
                  />
                </span>
              </span>
            </div>
            <textarea
              className="form-control"
              onChange={ e => e.preventDefault() }
              placeholder="Extract Text"
              value={isFetching ? 'loading...' : text}>
            </textarea>

            <div className={styles.row + ' text-center'}>
              <button className="btn btn-default" onClick={ (e)=> onReset(e) }>Reset</button>
            </div>
            {
              required &&
              <div className={styles.row}>
                <p className="text-center"><small className={styles.fieldRequired}>*Required</small></p>
              </div>
            }
          </div>
        </div>
      </li>
    );
  }

}

export default AreaField;
