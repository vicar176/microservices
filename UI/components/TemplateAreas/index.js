import React, { Component } from 'react';
import { connect } from 'react-redux';
import { find } from 'lodash';
// components
import AreaField from 'AreaField';
import AreaFieldRef from 'AreaFieldRef';
import CssLoad from 'components/Common/cssLoad';
// styles
import styles from 'index.scss';
// actions
import {
  selectArea,
  resetArea,
  fetchAreaText,
  setCoordValue,
} from 'actions/areas';

import {
  addAreaRef,
  selectAreaRef,
  deleteAreaRef,
} from 'actions/areasRef';

class TemplateAreas extends Component {

  constructor(props) {
    super(props);
    this.headerHeight = 0;
  }

  componentDidMount() {
    $('.main-wrapper').removeClass('sidebar-open').addClass('sidebar-close');
    // $('#configuration-tab').collapse('hide');
  }

  onSelect = (id) => {
    this.props.dispatch(selectArea(id));
    const selectedArea = find(this.props.areas, {id: id});
    if (selectedArea && selectedArea.mapped) {
      this.props.dispatch(fetchAreaText());
    }
  };

  onReset = (e) => {
    e.stopPropagation();
    this.props.dispatch(resetArea());
  };

  onDelete = (id) => {
    this.props.dispatch(deleteAreaRef(id));
  };

  onSelectRef = (id) => {
    this.props.dispatch(selectAreaRef(id));
    const selectedArea = find(this.props.areasRef, {id: id});
    if (selectedArea && selectedArea.mapped) {
      this.props.dispatch(fetchAreaText());
    }
  };

  onAddRefArea = () => {
    this.props.dispatch(addAreaRef());
  };

  onCoordChange = (coord, value) => {
    this.props.dispatch(setCoordValue(coord, value));
  };

  render() {
    const { areas, areasRef, totalPages, isDisabled } = this.props;
    const isAddEnabled = totalPages > areasRef.length;
    let areaIndex = 0;

    return (
      <nav id="MainNav" className={styles.mainNav} ref="sidebar">
        <div className="menu-container" ref="menuContainer">
          <div className="panel panel-default">
            <h3 className="panel-heading">Reference Areas</h3>
            <ul className="list-group">
              {areasRef.sort(this.sortTemplateAreas).map( (area, i) => {
                return (
                  <AreaFieldRef
                    key={i}
                    area={area}
                    onSelect={this.onSelectRef}
                    onDelete={this.onDelete}
                    onCoordChange={this.onCoordChange}
                    isDisabled={isDisabled}
                  />
                )
              })}
              {isAddEnabled && <button className={styles.addNew + " btn btn-primary center-block"} onClick={this.onAddRefArea}>Add New</button>}
            </ul>
          </div>
          <div className="panel panel-default">
            <h3 className="panel-heading">Selection Areas</h3>
            <ul className="list-group">
              {!areas.length &&
                <div className="panel-body">
                  <CssLoad />
                </div>
              }
              {areas.map((area, i) => {
                areaIndex++;
                return (
                  <AreaField
                    key={i}
                    areaIndex={areaIndex}
                    area={area}
                    onSelect={this.onSelect}
                    onReset={this.onReset}
                    onCoordChange={this.onCoordChange}
                    isDisabled={isDisabled}
                  />
                );
              })}
            </ul>
          </div>
        </div>
      </nav>
    );
  }

}

function mapStateToProps(state) {
  return {
    areas: state.templateAreas,
    areasRef: state.templateAreasRef,
    totalPages: state.template.totalPages
  }
}

export default connect(mapStateToProps)(TemplateAreas);
