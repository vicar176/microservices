import React, { Component } from 'react';
import { Link } from 'react-router';
import { logout } from 'actions/auth';
import { connect } from 'react-redux';

class Auth extends Component {
  render() {
    const { loggedIn } = this.props.auth;
    let { user } = this.props.auth;
    user = user || {};

    return(
      <ul className="nav">
        { loggedIn ?
          <li className="dropdown">
            <button className="btn-profile dropdown-toggle" type="toggle-profile" name="button" data-toggle="dropdown">
              <i className="glyphicon glyphicon-user"></i>
              <span className="user-profile">{user.firstName +' '+ user.lastName}</span>
            </button>
            <ul className="dropdown-menu">
              <li onClick={ () => this.props.dispatch(logout())}><a href="#"><i className="glyphicon glyphicon-log-out"></i> Logout</a></li>
            </ul>
          </li>
        :
          <li className="dropdown">
            <button className="btn-profile dropdown-toggle" type="toggle-profile" name="button" data-toggle="dropdown">
              <i className="glyphicon glyphicon-log-in"></i>
              <span className="user-profile">Login</span>
            </button>
          </li>
        }
      </ul>
    );
  }
}

Auth.propTypes = {
  auth: React.PropTypes.object.isRequired
}

function mapStateToProps(state) {
  return {
    auth: state.auth
  };
}

export default connect(mapStateToProps)(Auth);
