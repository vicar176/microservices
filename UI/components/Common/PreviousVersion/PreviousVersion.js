import React, {Component} from 'react';
import { connect } from 'react-redux';
import CssLoad from 'cssLoad';
import moment from 'moment';
import styles from './PreviousVersion.scss';
import PreviousMode from 'PreviousMode';
import ConfirmPopup from 'components/Common/ConfirmPopup';

import {
  fetchVersion,
  enablePreviousVersion,
  selectPreviousVersion,
  selectPreviousVersionNumber,
  resetPreviousVersion
} from 'actions/previousVersion';

class PreviousVersion extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isModalActive: false,
      queryId: this.props.template.id || this.props.template.templateId
    };
  }

  componentWillReceiveProps(props) {
    const id = props.template.id || props.template.templateId;
    if (this.state.queryId !== id) {
      this.setState({ queryId: id });
    }
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch(resetPreviousVersion());
    dispatch(fetchVersion(`${this.props.query}/${this.state.queryId}`));
  }

  onValueChange = version => {
    const { dispatch } = this.props;
    if (version) {
      dispatch(selectPreviousVersionNumber(version));
      this.props.selectVersion(this.state.queryId, version).then(response => {
        dispatch(selectPreviousVersion(response));
        dispatch(enablePreviousVersion(true));
      });
    }
  }

  onCancelPreview (version) {
    const { dispatch } = this.props;
    this.props.selectVersion(this.state.queryId, version);
    dispatch(selectPreviousVersionNumber(version));
    dispatch(selectPreviousVersion(this.props.template));
    dispatch(enablePreviousVersion(false));
  }

  onApplyVersion = () => {
    if (this.props.previousVersion.isProfileEnable) {
      $('#applyPreviousVersion').modal('show');
      this.setState({ isModalActive: true });
    }
  }

  onConfirmVersionModal = () => {
    this.props.dispatch(enablePreviousVersion(false));
    this.setState({ isModalActive: false });
    $('#applyPreviousVersion').modal('hide');
  }

  render () {
    const {
      versionsList,
      isProfileEnable,
      selectedPreviousVersion,
      isFetchingPreviousVersion,
      selectedPreviousVersionNumber
    } = this.props.previousVersion;
    const previousDate = moment(selectedPreviousVersion.updateDate).format('MMMM Do YYYY, hh:mm:ss a');

    return (
      <div className={styles.previousVersion} style={{display: !this.props.template ? 'none' : 'block'}}>
        <div className={styles.previousVersionCta}>
          <div className={styles.previousVersionContent}>
            { versionsList && versionsList.length > 1 &&
              <select
                onChange={e => this.onValueChange(e.target.value)}
                value={selectedPreviousVersionNumber}
                name="productGroup"
                // disabled={isProfileEnable}
                className="form-control">
                <option value="">Previous Version</option>
                { versionsList.map((version, i) => {
                  if (i !== 0) {
                    return <option key={i} value={version}>{'Version ' + version}</option>;
                  }
                })}
              </select> }
            { versionsList && isFetchingPreviousVersion && <CssLoad style={{ marginTop: versionsList.length ? 0 : 18 }} /> }
          </div>
        </div>
        { selectedPreviousVersion && !!Object.keys(selectedPreviousVersion).length &&
          <div>
            <PreviousMode
              isShow={isProfileEnable}
              userName={selectedPreviousVersion.updatedBy}
              date={previousDate}
              selectedPreviousVersion={selectedPreviousVersionNumber}
              onApplyVersion={this.onApplyVersion}
              onCancelPreview={() => this.onCancelPreview(versionsList.length)} />

              <ConfirmPopup
              confirmId='applyPreviousVersion'
              onConfirm={this.onConfirmVersionModal}
              onCancelClick={() => this.setState({isModalActive: false})}
              title='Apply Previous Version'
              content='This will overwrite the current version. Continue?'/>
          </div>

        }
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    previousVersion: state.previousVersion
  }
}

export default connect(mapStateToProps)(PreviousVersion);
