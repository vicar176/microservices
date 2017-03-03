import React, { Component } from 'react';
import { connect } from 'react-redux';
import moment from 'moment';
import { Tooltip, OverlayTrigger} from 'react-bootstrap';

// components
import Switch from 'components/Common/Switch';
import ConfirmPopup from 'components/Common/ConfirmPopup';

// actions
import { updateIngestionState, fetchIngestionState, isIngestionShoutDown, cleanProfileData } from 'actions/common';
import {
  cleanPortfoliosId,
  fetchPortfoliosId
} from 'actions/extractionExceptions'

// styles
import styles from './index.scss';

class IngestionSwitch extends Component {
  constructor() {
    super();
    this.state = {
      leftDays: 14,
      updatedBy: '',
      updateDate: ''
    }
  }

  componentWillMount () {
    this.props.dispatch(fetchIngestionState()).then(response => {
      this.setIngestionState(response);
    });
  }

  setIngestionState(response) {
    this.props.dispatch(isIngestionShoutDown(!response.shutdownState));
    this.setState({
      updatedBy: response.updatedBy,
      updateDate: response.updateDate
    });
  }

  setCountDown () {
    const currDate = new Date().getTime();
    const dueDate = new Date((Date.parse(this.state.updateDate)+(86400000 * this.state.leftDays))).getTime();
    return Math.ceil((dueDate - currDate) / (1000*60*60*24));
  }

  onSwitchChange = e => {
    const { dispatch } = this.props;
    e.preventDefault();
    if (!e.target.checked) {
      $('#ingestionSwitchModal').modal('show');
    } else {
      dispatch(updateIngestionState(false)).then(response => {
        this.setIngestionState(response);
        this.refreshData();
      });
    }
  }

  onConfirm = () => {
    const { dispatch } = this.props;
    $('#ingestionSwitchModal').modal('hide');
    dispatch(updateIngestionState(true)).then(response => {
      this.setIngestionState(response);
      this.refreshData();
    });
  }

  refreshData = () => {
    const { dispatch } = this.props;
    dispatch(cleanPortfoliosId());
    dispatch(fetchPortfoliosId());
    dispatch(cleanProfileData());
  }

  render() {
    const { isFetchingIngestionState, isIngestionShoutDown } = this.props.common;
    const counter = this.setCountDown();

    const ingestionTooltip = <Tooltip id="tooltip-workflow">
                                Turn <strong>On/Off</strong> the ingestion of all documents.<br/>
                                Last updated by <strong>{this.state.updatedBy}</strong> on <strong>{moment(this.state.updateDate).format("MMM Do YY")}</strong>
                             </Tooltip>;

    return (
      <div className={styles.ingestionSwitch} style={this.props.style}>
          <div className={styles.switchinfo}>
            { this.props.label && <strong className={styles.switchLegend}>{this.props.label} </strong> }
            { this.state.updateDate && !isIngestionShoutDown &&
              <span className={counter < 5 ? 'text-warning' : null}>
                { counter > 0 ? ` ${counter} days left` : ' Time has expired' }
              </span>
            }
          </div>
        <OverlayTrigger placement="left" overlay={ingestionTooltip}>
          <div>
            <Switch
              id="ingestion-switch"
              isShoutDown={isIngestionShoutDown}
              isFetchingIngestionState={isFetchingIngestionState}
              onSwitchChange={this.onSwitchChange} />
          </div>
        </OverlayTrigger>

        <ConfirmPopup confirmId='ingestionSwitchModal' onConfirm={this.onConfirm} title='This will stop ingestion of all documents' content='The ingestion queue will only retain data for 14 days. If stopped for longer than 14 days, documents uploaded prior will not be ingested. Do you want to continue?'/>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    common: state.common
  };
}

export default connect(mapStateToProps)(IngestionSwitch);
