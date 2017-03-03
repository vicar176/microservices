import React, { Component } from 'react';
import classNames from 'classnames'
// styles
import styles from 'index.scss';

class AreaFieldRef extends Component {

  render() {
    const {
      coords,
      value,
      isFetching,
      selected,
      id,
    } = this.props.area;

    const { onSelect, onDelete, onCoordChange, isDisabled } = this.props;

    const { x=0, y=0, w=0, h=0 } = (coords || {});

    const wrapperClass = classNames({
      [styles.wrapper]: true,
      [styles.selected]: selected,
    });

    const contentClass = classNames({
      [styles.content]: true,
      [styles.visible]: selected,
    });

    return (
      <li className={wrapperClass + ' list-group-item'} onClick={ () => onSelect(id)}>
        <a>
          <span className={styles.areaId}>{id+1}</span>
          &nbsp;
          <div className={styles.title}>Reference Area {id+1}</div>
        </a>

        <div className={contentClass + ' row'}>
          <div className="col-xs-12">
            <div className="row form-inline">
              <span className={styles.zone + ' col-xs-6'}>
                <span className="input-group">
                  <span className={styles.coord + ' input-group-addon'}>X: </span>
                  <input
                    className={styles.value + ' form-control'}
                    onChange={(e)=> onCoordChange('x', e.target.value)}
                    type="number"
                    value={Math.round(x)}
                    disabled={isDisabled}
                  />
                </span>
              </span>
              <span className={styles.zone + ' col-xs-6'}>
                <span className="input-group">
                  <span className={styles.coord + ' input-group-addon'}>Y: </span>
                  <input
                    className={styles.value + ' form-control'}
                    onChange={(e)=> onCoordChange('y', e.target.value)}
                    type="number"
                    value={Math.round(y)}
                    disabled={isDisabled}
                  />
                </span>
              </span>
            </div>
            <div className="row form-inline">
              <span className={styles.zone + ' col-xs-6'}>
                <span className="input-group">
                  <span className={styles.coord + ' input-group-addon'}>W: </span>
                  <input
                    className={styles.value + ' form-control'}
                    onChange={(e)=> onCoordChange('w', e.target.value)}
                    type="number"
                    value={Math.round(w)}
                    disabled={isDisabled}
                  />
                </span>
              </span>
              <span className={styles.zone + ' col-xs-6'}>
                <span className="input-group">
                  <span className={styles.coord + ' input-group-addon'}>H: </span>
                  <input
                    className={styles.value + ' form-control'}
                    onChange={(e)=> onCoordChange('h', e.target.value)}
                    type="number"
                    value={Math.round(h)}
                    disabled={isDisabled}
                  />
                </span>
              </span>
            </div>

            <textarea
              className="text-center"
              onChange={ e => e.preventDefault() }
              cols="20"
              placeholder="Extract Text"
              value={isFetching ? 'loading...' : value}>
            </textarea>

            <div className={styles.row}>
              <button className="btn btn-default center-block" onClick={ () => onDelete(id) }>Delete</button>
            </div>
          </div>
        </div>
      </li>
    );
  }

}

export default AreaFieldRef;
