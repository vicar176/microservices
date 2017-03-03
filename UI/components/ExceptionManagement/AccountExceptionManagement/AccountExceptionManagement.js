import React, { Component } from 'react';
import { connect } from 'react-redux';
import accountsData from 'json!data/accountExceptionDataValues.json';
import accountsMetaData from 'json!data/accountExceptionDocumentMetadataValues.json';
// import demoPdf from 'data/sample.pdf';

import {
  selectReviewMethod,
  activeVerification,
  fetchAccountDetail,
  editAccountField,
  setCurrentPage
} from 'actions/accountExceptionManagement';

// Styles
import 'AccountExceptionManagement.scss';

// Components
import NavigationMethodType from 'NavigationMethodType';
import ListPager from 'ListPager';
import PortfolioSearch from 'PortfolioSearch';
import DocumentUpload from 'DocumentUpload';
import CTASection from 'CTASection';
import AccountDetail from 'AccountDetail';
import Pager from 'components/Common/Pager';

class AccountExceptionManagement extends Component {

  // App on ready state
  componentDidMount() {
    this.props.dispatch(fetchAccountDetail(accountsData));
  }

  // Main top navigation switch
  onSelectRewiewMethod = (reviewMethod) => {
    const { dispatch } = this.props;
    dispatch(selectReviewMethod(reviewMethod));
  };

  // Verified or unverified account depends of checkbox
  onVerificationActive = (verificationState) => {
    const { dispatch } = this.props;
    dispatch(activeVerification(verificationState));
  };

  // Edit Field
  onEditAccountField = (field) => {
    const { dispatch } = this.props;
    dispatch(editAccountField(field));
    $('.error-box').toggleClass('edit-mode');
  };

  handlePageChanged = (newPage) => {
    this.props.dispatch(setCurrentPage(newPage));
  };

  // @TODO:JSRef
  // Uptate this value by the account number once we have the corresponding endpoint setup
  generateID(min, max) {
    return Math.floor(Math.random() * (max - min)) + min;
  }

  renderDemoList() {
    const { isAccountVerified } = this.props.exceptionManagement;
    const totalPages = 1, currentPage = 0, documents=[{test: 'test'}];
    return (
      <div>
        <AccountDetail
          isAccountVerified={this.props.isAccountVerified}
          editProfile={this.editProfile}
          onVerificationActive={this.onVerificationActive}
          onEditAccountField={this.onEditAccountField}
          generateID={this.generateID}
        />
        <hr />
        <AccountDetail
          isAccountVerified={this.props.isAccountVerified}
          editProfile={this.editProfile}
          onVerificationActive={this.onVerificationActive}
          onEditAccountField={this.onEditAccountField}
          generateID={this.generateID}
        />
        <hr />
        <AccountDetail
          isAccountVerified={this.props.isAccountVerified}
          editProfile={this.editProfile}
          onVerificationActive={this.onVerificationActive}
          onEditAccountField={this.onEditAccountField}
          generateID={this.generateID}
        />
        <hr />
        <AccountDetail
          isAccountVerified={this.props.isAccountVerified}
          editProfile={this.editProfile}
          onVerificationActive={this.onVerificationActive}
          onEditAccountField={this.onEditAccountField}
          generateID={this.generateID}
        />
        <hr />
        <Pager totalPages={totalPages} handlePageChanged={this.handlePageChanged} currentPage={currentPage} isShow={documents.length? true:false} />
      </div>
    );
  }

  renderDemoItem() {
    const { isAccountVerified } = this.props.exceptionManagement;
    return (
      <AccountDetail
        isAccountVerified={this.props.isAccountVerified}
        editProfile={this.editProfile}
        onVerificationActive={this.onVerificationActive}
        onEditAccountField={this.onEditAccountField}
        generateID={this.generateID}
      />
    );
  }

  renderModal() {
    return(
      <div className="modal fade bs-example-modal-lg" tabIndex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
        <div className="modal-dialog modal-lg">
          <div className="modal-content">
            <div className="modal-header">
              <div className="row">
                <div className="col-md-4 text-left">Account Number: 8550000001</div>
                <div className="col-md-4">Portfolio Number: 1555</div>
                <div className="col-md-3">Seller: Citibank</div>
                <div className="col-md-1">
                  <button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                </div>
              </div>
            </div>
            <div className="modal-body">
              {/* <iframe src={demoPdf + '#page=1&zoom=100'} width="100%" height="450px" /> */}
            </div>
            <div className="modal-footer text-center">
              <button type="button" data-dismiss="modal">Close</button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  render() {
    const { selectedReviewMethod } = this.props.exceptionManagement;

    return (
      <div className="wrap-forms">
        <form onSubmit={ e => e.preventDefault() } className="AccountException" >
          <NavigationMethodType
            selectedReviewMethod={selectedReviewMethod}
            onSelectRewiewMethod={this.onSelectRewiewMethod} />
          {(() => {
            switch ( selectedReviewMethod ) {
              case 'list': return <ListPager />;
              case 'portfolio': return <PortfolioSearch />;
              case 'upload': return <DocumentUpload />;
              default: return <ListPager />;
            }
          })()}
          {(() => {
            // TEMPORAL CONTENT TO FILL UP THE PAGE
            // @TODO:JSRef Move this content to a json file
            return (selectedReviewMethod === 'list') ? this.renderDemoItem() : this.renderDemoList();
            // TEMPORAL
          })()}
          <CTASection />
        </form>
        { this.renderModal() }
      </div>
    );
  }

}

function mapStateToProps(state) {
  return {
    exceptionManagement: state.accountExceptionManagement
  };
}

export default connect(mapStateToProps)(AccountExceptionManagement);
