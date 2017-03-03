import React, { Component, Children, cloneElement } from 'react';
import ReactDOM from 'react-dom';
import { connect } from 'react-redux';
import { Tooltip } from 'react-bootstrap';

class ValidationTooltip extends Component {

  componentDidMount () {
    this.tooltipWrap = ReactDOM.findDOMNode(this);
    this.tooltipWrapProps = this.tooltipWrap.getBoundingClientRect();
  }

  posLeft() {
    return 0;
  }

  renderChildren = () => {
    return Children.map(this.props.children, child => {
      if (this.props.message.length && !this.props.common.isFormValid) {
        return cloneElement(child, {
          className: child.props.className + ' error'
        })
      }
      return child;
    })
  }

  renderTooltip() {
    const isHidden = (this.props.message.length) ? 'in' : '';
    return(<Tooltip ref="tooltip" id="nameValidationTip" className={isHidden} positionLeft={this.posLeft()} placement="top" style={{bottom: '100%', marginBottom: '5px'}}>{this.props.message}</Tooltip>);
  }

  render() {
    return(
      <div className="validator-tooltip" style={{position: 'relative'}}>
        {this.renderChildren()}
        {!this.props.common.isFormValid && this.renderTooltip()}
      </div>
    );
  }
}

function mapStateToProps(state) {
  return { common: state.common }
}

export default connect(mapStateToProps)(ValidationTooltip);
