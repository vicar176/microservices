import React, { Component } from 'react';
import { connect } from 'react-redux';
import Breadcrumbs from 'react-breadcrumbs';
import toastr from 'toastr';

import { selectTab, cleanProfileData } from 'actions/common';
import { enablePreviousVersion } from 'actions/previousVersion';

class MainContent extends Component {
  constructor(props) {
    super(props);
    this.onClickCallback = this.props.onClickCallback || (() => {});
  }

  componentDidMount() {
    const { dispatch, tabs } = this.props;
    $('.content-tab').on('shown.bs.tab', e =>
      dispatch(selectTab(e.target.getAttribute('data-ref'))
    ));
    if (tabs && tabs[0].id !== this.props.common.selectedTab) {
      dispatch(selectTab(tabs[0].id));
    }
    toastr.clear();
  }

  componentWillUnmount() {
    const { dispatch, tabs, common } = this.props;
    if (!common.isEditMode) {
      this.cleanProfile();
    }

    if (tabs) {
      dispatch(selectTab(''));
    }
    $('.content-tab').off('shown.bs.tab');
  }

  cleanProfile () {
    const { dispatch } = this.props;
    dispatch(cleanProfileData());
    dispatch(enablePreviousVersion(false));
  }

  onTabClickHandle = e => {
    const currTab = e.target.getAttribute('data-ref');
    if (this.props.common.selectedTab !== currTab) {
      this.cleanProfile();
    }
    this.onClickCallback(currTab);
    toastr.clear();
  }

  render() {
    const {
      routes,
      params,
      mainTitle,
      tabs = []
    } = this.props;

    return (
      <div>
        <div className="row">
          <div className="col-xs-12">
            <div className="content-header">
              <Breadcrumbs
                separator=''
                wrapperElement='ol'
                itemElement='li'
                customClass='breadcrumb'
                routes={routes}
                params={params}
                // excludes={['Home']}
              />
              <h1>{mainTitle}</h1>
            </div>
          </div>
        </div>
        { !!tabs.length &&
          <ul className="nav nav-tabs" role="tablist">
            { tabs.map((tab, index) =>
              <li key={tab.id} role="presentation" className={index === 0 ? 'active' : ''}>
                <a href={'#' + tab.id}
                   aria-controls={tab.id}
                   role="tab"
                   data-toggle="tab"
                   data-ref={tab.id}
                   className="content-tab"
                   onClick={this.onTabClickHandle}>{tab.name}</a>
              </li>
            )}
          </ul>
        }
        <div className="tab-content">
          {this.props.children}
        </div>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return { common: state.common }
}

export default connect(mapStateToProps)(MainContent);
