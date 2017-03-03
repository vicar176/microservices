import coloredLogo from 'images/encore-logo-colorized.png';
import React, { Component } from 'react';

class Footer extends Component {

  render() {
    return(
      <footer className="footer col-xs-12">
        <div className="inner">
          <div className="col-xs-12 col-sm-4">
             <ul className="list-inline footer-browser-list">
                <li className="chrome">
                 <span className="label label-default" title="Chrome 44+">44+</span>
                </li>
                <li className="edge">
                 <span className="label label-default" title="Edge 12+">12+</span>
                </li>
             </ul>
        </div>
        <div className="col-xs-12 col-sm-4 text-center">
          <p>2016 &copy; All Rights Reserved. Powered by <a href="#">Encore</a>.</p>
        </div>
        <div className="col-xs-12 col-sm-4 provider-logo">
          <img className="logo" src={coloredLogo} alt="" />
        </div>
    </div>
 </footer>
    );
  }
}

export default Footer;
