import React, { Component } from 'react';
import { findDOMNode } from 'react-dom';
import { connect } from 'react-redux';
import { bindAll, find, where } from 'lodash';
import baseToastrConfig from 'lib/baseToastrConfig';
import toastr from 'toastr';
// components
import ZoomLevel from './ZoomLevel';
import SelectPage from './SelectPage';
import TemplatePage from './TemplatePage';
import TemplateAreas from '../TemplateAreas';
// styles
import styles from './index.scss';
// actions
import {
  setTemplateWidth,
  selectZoomLevel,
  selectPage,
  showMapping,
} from 'actions/template';

import {
  setAreas,
  selectZone,
  fetchAreaText,
  selectArea,
  resetArea,
} from 'actions/areas';

import {
  selectAreaRef,
  setAreasRef,
} from 'actions/areasRef';

class TemplateMapping extends Component {

  constructor(props) {
    super(props);
    this.state = {
      documentHeight: 'auto',
      isNavLarge: false
    };
  }

  affixContainer(isNavLarge) {
    this.$affixContainer = $(isNavLarge ? '#TemplateMapping' : '#MainNav');
    this.$secondaryContainer = $(isNavLarge ? '#MainNav' : '#TemplateMapping');

    const navWidth = this.$affixContainer.width();
    const maxMenuScroll = $(document).height() - this.$secondaryContainer.offset().top - this.$secondaryContainer.outerHeight(true) + 100;

    // Adding bootstrap affix to field extraction menu
    this.$affixContainer.affix({
      offset: {
        top: this.$affixContainer.offset().top,
        bottom: maxMenuScroll
      }
    }).width(navWidth);
  }

  componentWillUnmount() {
    $(window).off('.affix');
    this.$affixContainer
      .removeClass("affix affix-top affix-bottom")
      .removeData("bs.affix");
    this.$affixContainer = null;
  }

  sortAreas = (a, b) => {
    const x = a.fieldDefinition.fieldName;
    const y = b.fieldDefinition.fieldName;
    return x < y ? -1 : x > y ? 1 : 0;
  }

  setWrapheight() {
    const navElem = findDOMNode(this.refs.templateNav);
    const templateHeight = findDOMNode(this.refs.templateWrap).offsetHeight;
    const navHeight = navElem.offsetHeight;
    const isNavLarge = navHeight > templateHeight;
    let documentHeight = 0;

    if (isNavLarge) {
      navElem.style.position = 'relative';
      documentHeight = navHeight;
    } else {
      documentHeight = templateHeight;
    }

    this.affixContainer(isNavLarge);
    this.setState({ documentHeight, isNavLarge });
  }

  setAreas() {
    const { dispatch } = this.props;
    const { selectedDocType, selectedTemplateName } = this.props.template;
    const { templates } = this.props.profile;
    let selectedTemplate = null;
    if (templates) {
      selectedTemplate = find(templates, {name: selectedTemplateName});
    }
    // todo: avoid loop if not selectedTemplate
    const areas = selectedDocType.fieldDefinitions.map( (field) => {
      if (selectedTemplate) {
        const mappings = selectedTemplate.zoneMappings;
        const fieldDefinition = find(mappings, (map) => {
          return map.fieldDefinition.fieldName === field.fieldDefinition.fieldName;
        });
        if (fieldDefinition) {
          const [x, y, w, h] = fieldDefinition.zoneArea;
          return Object.assign({}, field, {
            coords: {
              x: x,
              y: y,
              w: w,
              h: h,
            },
            mapped: !!(x || y || w || h),
            pageNumber: fieldDefinition.pageNumber
          });
        }
        return field;
      }
      return field;
    });

    const requiredAreas = areas.filter(area => area.required && area.fieldDefinition.active).sort(this.sortAreas);
    const optionalAreas = areas.filter(area => !area.required && area.fieldDefinition.active).sort(this.sortAreas);

    dispatch(setAreas([...requiredAreas, ...optionalAreas]));
    if (selectedTemplate && selectedTemplate.referenceAreas.length) {
      dispatch(setAreasRef(selectedTemplate.referenceAreas));
    }
  }

  setToastrOnWarning(message) {
    toastr.options = baseToastrConfig();
    toastr.warning(message);
  }

  getRealCoords(coords) {
    const { zoomLevel } = this.props.template;
    const coordsCopy = Object.assign({}, coords);
    Object.keys(coordsCopy).forEach( coord => {
      coordsCopy[coord] = Math.round((coordsCopy[coord] / zoomLevel) * 100);
    });
    return coordsCopy;
  }

  onTemplateLoaded = (imgWidth, wrapperWidth) => {
    const { dispatch } = this.props;
    // set only when page #1 is loaded
    if (!this.props.template.isTemplateLoaded) {
      const zoomLevel = Math.floor((wrapperWidth / imgWidth) * 100);
      dispatch(setTemplateWidth(imgWidth, wrapperWidth));
      dispatch(selectZoomLevel(zoomLevel))
      this.setAreas();
    }
    dispatch(showMapping(true));

    setTimeout(()=> {
      this.setWrapheight();
    }, 100);
  }

  onZoomChange = (newZoomLevel, oldZoomLevel) => {
    if (!isNaN(newZoomLevel)) {
      this.props.dispatch(selectZoomLevel(newZoomLevel));
    } else {
      this.props.dispatch(selectZoomLevel(oldZoomLevel));
      this.setToastrOnWarning('Enter a Zoom with only numbers');
    }
  }

  onFitWidth = () => {
    const { templateWidth, wrapperWidth } = this.props.template;
    const zoomLevel = Math.floor((wrapperWidth / templateWidth) * 100);
    this.props.dispatch(selectZoomLevel(zoomLevel));
  }

  onZoneCreate = (coords) => {
    const { dispatch } = this.props;
    const realCoords = this.getRealCoords(coords);
    dispatch(selectZone(realCoords, this.props.template.selectedPage));
    dispatch(fetchAreaText());
  }

  onSelectArea = (id, type) => {
    const { dispatch } = this.props;
    if (type === 'selection') {
      dispatch(selectArea(id));
    } else {
      dispatch(selectAreaRef(id));
    }
  }

  onResetArea = () => {
    this.props.dispatch(resetArea());
  }

  onSelectPage = (page) => {
    const { dispatch } = this.props;
    dispatch(showMapping(false));
    dispatch(selectPage(page));
  }

  render() {
    const {
      zoomLevel,
      isTemplateLoaded,
      templateWidth,
      selectedTemplateName,
      selectedTemplateFile,
      selectedPage,
      isMappingVisible,
    } = this.props.template;

    const { templates } = this.props.profile;

    let totalPages = this.props.template.totalPages;
    if (templates) {
      const template = find(templates, {sampleFileName: selectedTemplateFile});
      if (template) {
        totalPages = template.totalPages;
      }
    }

    const areas = where(this.props.areas, {pageNumber: +selectedPage});
    const areasRef = where(this.props.areasRef, {pageNumber: +selectedPage});

    return (
      <div style={{marginTop: '3em'}}>
        <h1 className="page-header">Template: {selectedTemplateName}</h1>
        <div id="template-tab" className={`${styles.TemplateTab} clearfix`} style={{ minHeight: this.state.documentHeight }}>
          <div ref="templateNav" className={`${styles.TemplateNavigation}`}>
            <TemplateAreas
              isDisabled={this.props.isDisabled}
              isNavLarge={this.state.isNavLarge}
            />
          </div>
          <div ref="templateWrap" id="TemplateMapping" className={`${styles.TemplateMapping} templateMapping clearfix`}>
            <div className={styles.TemplateControls}>
              <ZoomLevel
                onZoomChange={this.onZoomChange}
                zoomLevel={zoomLevel}
                onFitWidth={this.onFitWidth}
              />
              <SelectPage
                selectedPage={selectedPage}
                onSelectPage={this.onSelectPage}
                totalPages={totalPages}
              />
            </div>
            <TemplatePage
              onTemplateLoaded={this.onTemplateLoaded}
              onZoneCreate={this.onZoneCreate}
              onSelectArea={this.onSelectArea}
              onResetArea={this.onResetArea}
              isTemplateLoaded={isTemplateLoaded}
              templateWidth={templateWidth}
              zoomLevel={zoomLevel}
              selectedPage={selectedPage}
              areas={areas}
              areasRef={areasRef}
              isMappingVisible={isMappingVisible}
              selectedTemplateFile={selectedTemplateFile}
            />
          </div>
        </div>
      </div>
    );
  }

}

function mapStateToProps(state) {
  return {
    template: state.template,
    areas: state.templateAreas,
    areasRef: state.templateAreasRef,
    profile: state.templateProfile
  }
}

export default connect(mapStateToProps)(TemplateMapping);
