import React, { Component } from 'react';
import { connect } from 'react-redux';
import Auth from 'components/Auth';
import logo from 'components/Sidebar/images/everest_logo.png';
import logoText from 'components/Sidebar/images/everest_logo_text.png';

class Header extends Component {

  render () {
    const { loggedIn } = this.props.auth;

    return (
      <header id="PageHeader" className="main-header row">
        <div className="header-nav-inner col-xs-12">
            { !loggedIn ?
              <div className="sidebar-header col-xs-6">
                <a href="/#/">
                  <img className="logo" src={logo} alt="Everest"/>
                  <img className="logoText logo" src={logoText} alt="Everest"/>
                </a>
              </div>
              :
              <img className="panelLogo pull-left" src={logoText} alt="Everest"/>
            }
          <Auth/>
        </div>
      </header>
    );
  }
}

function mapStateToProps(state) {
  return {
    auth: state.auth
  };
}

export default connect(mapStateToProps)(Header);
