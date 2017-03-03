import React, { Component } from 'react';
import { where, bindAll, defer } from 'lodash';
import classNames from 'classnames';
import config from 'config';
import queryString from 'query-string';
import CssLoad from 'components/Common/cssLoad';
import toastr from 'toastr';
import baseToastrConfig from 'lib/baseToastrConfig';
// styles
import styles from 'index.scss';

const ESC_KEY = 27;

class TemplatePage extends Component {

  constructor() {
    super();
    bindAll(this,
      'onMouseDownZone',
      'onMouseDown',
      'onMouseMove',
      'onMouseUp',
      'onLoad',
      'onKeyDown'
    );
    // better to keep this out of component's state for performance
    // used for creating a zone
    this.isMouseDown = false;
    // used for resizing and dragging
    this.isMouseDownZone = false;
    this.mouseDownTarget = '';
    this.resetRect();
  }

  componentDidMount() {
    this.calculateRect();
    $(document).on('keydown', this.onKeyDown);
  }

  componentDidUpdate() {
    this.calculateRect();
    this.resetRect();
    this.drawActiveRect();
  }

  componentWillUnmount() {
    $(document).on('keydown', this.onKeyDown);
  }

  calculateRect() {
    const { top, left } = this.refs.template.getBoundingClientRect();
    if (window.scrollY) {
      this.wrapperRect = {
        top: top + window.scrollY,
        left: left + window.scrollX
      };
    } else {
      this.wrapperRect = {
        top: top + document.documentElement.scrollTop,
        left: left + document.documentElement.scrollLeft
      };
    }

  }

  drawActiveRect() {
    const { initX, initY, currX, currY } = this;
    const activeArea = this.refs.active;
    activeArea.style.top = initY + 'px';
    activeArea.style.left = initX + 'px';
    activeArea.style.width = (currX - initX) + 'px';
    activeArea.style.height = (currY - initY) + 'px';
  }

  hideActive() {
    this.refs.active.style.display = 'none';
  }

  showActive() {
    this.refs.active.style.display = 'block';
  }

  resetRect() {
    this.initX = 0;
    this.initY = 0;
    this.currX = 0;
    this.currY = 0;
    this.lastX = 0;
    this.lastY = 0;
  }

  imageNotLoaded() {
    toastr.options = baseToastrConfig();
    toastr.error("Template Image Not Found.");
  }

  getMousePosition(e) {
    const { top, left } = this.wrapperRect;
    const template = this.refs.template;
    const scrollLeft = template.scrollLeft;
    const scrollTop = template.scrollTop;
    const x = e.pageX - left + scrollLeft;
    const y = e.pageY - top + scrollTop;
    return { x, y };
  }

  getCoordsZoomed(coords) {
    const { zoomLevel } = this.props;
    const coordsCopy = Object.assign({}, coords);
    Object.keys(coordsCopy).forEach( coord => {
      coordsCopy[coord] = Math.round(coordsCopy[coord] * (zoomLevel / 100));
    });
    return coordsCopy;
  }

  onLoad(e) {
    const imgWidth = +$(e.target).width();
    const wrapperWidth = +$(this.refs.template).width();
    this.props.onTemplateLoaded(imgWidth, wrapperWidth);
  }

  onKeyDown(e) {
    if (e.which === ESC_KEY) {
      this.props.onResetArea();
    }
  }

  onMouseDown(e) {
    e.preventDefault();
    if (e.button === 0) {
      const { x, y } = this.getMousePosition(e);
      this.initX = x;
      this.initY = y;
      this.isMouseDown = true;
    }
    this.showActive();
  }

  onMouseMove(e) {

    if (this.isMouseDown || this.isMouseDownZone) {
      const { x, y } = this.getMousePosition(e);
      if (this.isMouseDown) {
        this.currX = x;
        this.currY = y;
      }
      // todo: clean :)
      const target = this.mouseDownTarget;
      if (this.isMouseDownZone) {
        if (target === 'top') {
          this.initY = y;
        } else if (target === 'topLeft') {
          this.initY = y;
          this.initX = x;
        } else if (target === 'topRight') {
          this.initY = y;
          this.currX = x;
        } else if (target === 'bottom') {
          this.currY = y;
        } else if (target === 'bottomLeft') {
          this.currY = y;
          this.initX = x;
        } else if (target === 'bottomRight') {
          this.currY = y;
          this.currX = x;
        } else if (target === 'left') {
          this.initX = x;
        } else if (target === 'right') {
          this.currX = x;
        } else if (target === 'zone') {
          // we have to move all 4 points for dragging
          const moveY = this.lastY - y;
          const moveX = this.lastX - x;

          this.initY = this.initY - moveY;
          this.currY = this.currY - moveY;
          this.lastY = y;

          this.initX = this.initX - moveX;
          this.currX = this.currX - moveX;
          this.lastX = x;
        }

      }

      this.drawActiveRect();
    }
  }

  onMouseUp() {
    if (this.isMouseDown || this.isMouseDownZone) {
      const { initX, initY, currX, currY } = this;

      const coords = {
        x: initX,
        y: initY,
        w: currX - initX,
        h: currY - initY
      }

      this.isMouseDownZone = this.isMouseDown = false;

      if (coords.w > config.minAreaSize &&
        coords.h > config.minAreaSize) {
        this.props.onZoneCreate(coords);
      } else {
        this.resetRect();
        this.drawActiveRect();
      }
      this.hideActive()
    }

  }

  onMouseDownZone(e, area, target) {
    e.persist();
    e.preventDefault();
    e.stopPropagation();
    if (e.button === 0) {
      defer(() => {
        const coordsZoomed = this.getCoordsZoomed(area.coords);
        const { x, y, w, h } = coordsZoomed;
        this.initY = y;
        this.initX = x;
        this.currY = y + h;
        this.currX = x + w;
        this.isMouseDownZone = true;
        this.mouseDownTarget = target;
        if (target === 'zone') {
          const { x, y } = this.getMousePosition(e);
          this.lastY = y;
          this.lastX = x;
        }
        this.drawActiveRect();
        this.showActive();
      });
      this.props.onSelectArea(area.id, area.areaType);
      this.props.onResetArea();
    }
  }

  renderArea(area, index) {
    const { id, fieldDescription, coords, selected, areaType, required } = area;
    const coordsZoomed = this.getCoordsZoomed(coords);

    const zoneClass = classNames({
      [styles.templatePageZone]: true,
      [styles.referenceZone]: areaType === 'reference',
      [styles.selected]: selected,
      [styles.required]: required,
    });

    const areaIdClass = classNames({
      [styles.areaIdRef]: areaType === 'reference',
      [styles.areaId]: true,
      [styles.areaIdRequired]: required,
    });

    const { x, y, w, h } = coordsZoomed;

    const zoneStyle = {
      left: x,
      top: y,
      width: w,
      height: h,
      zIndex: index,
    };

    return (
      <div
        key={index}
        className={zoneClass}
        style={zoneStyle}
        title={fieldDescription}
        onMouseDown={e => this.onMouseDownZone(e, area, 'zone') }
      >
        <span className={areaIdClass}>{id+1}</span>
        <div
          className={styles.border+' '+styles.top}
          onMouseDown={e => this.onMouseDownZone(e, area, 'top')}
        >
        </div>
        <div
          className={styles.border+' '+styles.topLeft+' '+styles.corner}
          onMouseDown={e => this.onMouseDownZone(e, area, 'topLeft')}
        >
        </div>
        <div
          className={styles.border+' '+styles.topRight+' '+styles.corner}
          onMouseDown={e => this.onMouseDownZone(e, area, 'topRight')}
        >
        </div>
        <div
          className={styles.border+' '+styles.bottom}
          onMouseDown={e => this.onMouseDownZone(e, area, 'bottom')}
        >
        </div>
        <div
          className={styles.border+' '+styles.bottomLeft+' '+styles.corner}
          onMouseDown={e => this.onMouseDownZone(e, area, 'bottomLeft')}
        >
        </div>
        <div
          className={styles.border+' '+styles.bottomRight+' '+styles.corner}
          onMouseDown={e => this.onMouseDownZone(e, area, 'bottomRight')}
        >
        </div>
        <div
          className={styles.border+' '+styles.left}
          onMouseDown={e => this.onMouseDownZone(e, area, 'left')}
        >
        </div>
        <div
          className={styles.border+' '+styles.right}
          onMouseDown={e => this.onMouseDownZone(e, area, 'right')}
        >
        </div>
      </div>
    );
  }

  render() {
    const {
      selectedTemplateFile,
      templateWidth,
      isTemplateLoaded,
      zoomLevel,
      selectedPage,
      areas,
      areasRef,
      isMappingVisible,
    } = this.props;

    let imgStyle = {};
    if (isTemplateLoaded) {
      imgStyle = {
        width: templateWidth * ( zoomLevel / 100 ),
        visibility: isMappingVisible ? 'visible': 'hidden',
      };
    }

    const zoneClass = classNames({
      [styles.templatePageZone]: true,
      [styles.active]: true,
    });

    const query = queryString.stringify({
      fileName: selectedTemplateFile,
      pageNumber: selectedPage,
    });

    const imageUrl = `${config.utilitiesService}sample-files?${query}`;

    return (
      <div className={styles.TemplatePage}>
        <div className={styles.templatePageTemplate} ref="template">
          <div
            ref="wrapper"
            className={styles.templatePageWrapper}
            onMouseDown={this.onMouseDown}
            onMouseMove={this.onMouseMove}
            onMouseUp={this.onMouseUp}
            onContextMenu={ e => e.preventDefault() }
          >
            <div className={zoneClass} style={ {display: 'none'} } ref="active"></div>
            {
              isTemplateLoaded &&
              isMappingVisible &&
              where(areas, {mapped: true}).map( (area, i) => {
                return this.renderArea(area, i);
              })
            }
            {
              isTemplateLoaded &&
              isMappingVisible &&
              where(areasRef, {mapped: true}).map( (area, i) => {
                return this.renderArea(area, i);
              })
            }
            {
              !isTemplateLoaded && <CssLoad />
            }
            {
              <img
                style={imgStyle}
                src={imageUrl}
                className={styles.templatePageImg}
                onLoad={this.onLoad}
                onError={this.imageNotLoaded}
                alt="Error loading page"
              />
            }
          </div>
        </div>
      </div>
    );
  }

}

export default TemplatePage;
