import 'toastr/build/toastr.min.css';

import React, { Component } from 'react';
import { connect } from 'react-redux';

import Header from 'components/Header';
import Sidebar from 'components/Sidebar';
import Footer from 'components/Footer';
import { lastActivity } from 'actions/auth';

class App extends Component {

  onActivity = () => {
    this.props.dispatch(lastActivity());
  };

  render() {
    const { auth } = this.props;
    const displaySidebar = auth.loggedIn ? '' : 'hide';
    return (
      <div className='main-wrapper theme-default sidebar-open' data-ref='breakpoints'>
        <div className={displaySidebar + ' main-sidebar'} data-ref='sideBar'>
          <Sidebar location={this.props.location} />
        </div>

        <div className="main-content container-fluid">
          <Header />
          <div className="inner row">
            <section className='content col-xs-12'>
              {this.props.children}
            </section>
            <Footer />
          </div>
        </div>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    auth: state.auth,
    selectedTemplateFile: state.template.selectedTemplateFile
  }
}

export default connect(mapStateToProps)(App);
