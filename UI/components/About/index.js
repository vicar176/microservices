import React, { Component } from 'react';
import MainContent from 'components/Common/MainContent';
import { Nav, NavItem, TabContainer, TabContent, TabPane } from 'react-bootstrap';

import styles from './index.scss';

export default class About extends Component {
  constructor(props) {
    super(props);
  }

  handleSelect (key) {
    console.log('handleSelect', key);
  }

  render() {
    return (
      <MainContent routes={this.props.routes} mainTitle='About MEDIA APP'>
        <div role="tabpanel" className="tab-pane active">
          <h2 className="page-header">Product Information</h2>
           <TabContainer id="about-page" defaultActiveKey="about">
            <div className="row">
              <div className="col-xs-12 col-sm-3">
                <Nav bsStyle="pills" defaultActiveKey="about" stacked>
                  <NavItem eventKey="about">About Media DIA</NavItem>
                  <NavItem eventKey="versions">API Versions</NavItem>
                  <NavItem eventKey="support">Support</NavItem>
                </Nav>
              </div>
              <div className="col-xs-12 col-sm-9">
                <TabContent animation>
                  <TabPane eventKey="about">
                    <h1>About Media DIA</h1>
                    <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras dictum lacus vel leo placerat semper. Duis pulvinar turpis et malesuada elementum. Nullam vulputate tempor dolor ut volutpat. Integer vestibulum finibus tellus non posuere.</p>
                    <p>Fusce ultricies metus sem, at posuere ligula gravida ut. Nam mollis ex ipsum, id eleifend enim rhoncus sed. Cras vitae enim ipsum. Cras tempus quam a mollis efficitur. Sed gravida rutrum nisl nec fringilla.</p>
                    <p>Proin sodales scelerisque mi, sit amet efficitur purus efficitur in. Mauris semper suscipit risus, sagittis consectetur purus. Mauris auctor tellus ante. Aenean semper magna ut augue gravida, sed faucibus arcu molestie. Cras vel auctor felis, eu ullamcorper neque.</p>
                  </TabPane>
                  <TabPane eventKey="versions">
                    <h1>API Versions</h1>
                    <p>This project was build using the following API versions:</p>
                    <ul className="list-group">
                      <a className="list-group-item" href="http://tfs-prd.internal.mcmcg.com:8080/tfs/Encore/ICO/Media/_git/MEDIA-DIA-CLIENT" target="_blank">
                        <span className="label label-info pull-right">1.0.0</span>
                        MEDIA DIA
                      </a>
                      <a className="list-group-item" href="http://tfs-prd.internal.mcmcg.com:8080/tfs/Encore/ICO/Media/_git/GBS_Style_Guide" target="_blank">
                        <span className="label label-info pull-right">2.0.1</span>
                        Encore Style Guide
                      </a>
                    </ul>
                  </TabPane>
                  <TabPane eventKey="support">
                    <h1>Support</h1>
                    <p>Please contact us to any following email addresses if you are experiencing unexpected behaviors.</p>
                    <ul className="list-group">
                      <span className="list-group-item">email@example.com</span>
                      <span className="list-group-item">info@example.com</span>
                    </ul>
                  </TabPane>
                </TabContent>
              </div>
            </div>
           </TabContainer>
        </div>
      </MainContent>
    );
  }
}
