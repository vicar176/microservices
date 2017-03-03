import React, { Component } from 'react';
import { connect } from 'react-redux';
import { login } from 'actions/auth';
import config from 'config';
import styles from './login.scss';

class LoginPage extends Component {

  componentDidMount() {
    const { dispatch } = this.props;
    const baseUrl = config.oktaService;
    const oktaSignIn = new OktaSignIn({baseUrl: baseUrl});
    oktaSignIn.renderEl({ el: "#okta-login-container" }, res => {
      if (res.status === "SUCCESS") {
        dispatch(login(res));
      }
    });
  }

  componentWillUnmount() {
    Backbone.history.stop();
  }

  render() {
    return (
        <div>
          <div id="okta-login-container" className={styles.loginContainer}></div>
        </div>
    );
  }

}

function mapStateToProps(state) {
  return {
    auth: state.auth
  };
}

export default connect(mapStateToProps)(LoginPage);
