import React, { Component } from 'react';
import { connect } from 'react-redux';
import ReactDOM from 'react-dom';
import CssLoad from 'components/Common/cssLoad';
import baseToastrConfig from 'lib/baseToastrConfig';
import validator from 'validator';
import toastr from 'toastr';
import moment from 'moment';
import config from 'config';
const { utilitiesService } = config;

// actions
import {
  isFetchingDocuments,
  selectReviewMethod,
  selectDocumentList,
  fetchDocumentsList,
  fetchDocuments,
  clearDocuments,
  setCurrentPage,
  updateDocument,
  setItemsPerPage,
  receiveDocuments,
  updateDocumentField
} from 'actions/oaldExceptionManagement';

// components
import Pagination from 'components/Common/Pagination';
import SelectDocumentReview from 'SelectDocumentReview';
import SearchOALDDocument from 'SearchOALDDocument';
import OALDDocument from 'OALDDocument';
import FailedDocumentModal from 'components/ExceptionManagement/ExtractionExceptions/PortfolioDetail/FailedDocumentModal';

// style
import styles from './OALDDocument.scss';

class OALDExceptionManagement extends Component {
  constructor(props) {
    super(props);
    this.state = {
      activeModal: false,
      isPdfPresent: true
    };
  }

  componentWillMount() {
    toastr.options = baseToastrConfig();
    this.onReviewMethod('exceptionList');
  }

  showDocumentModal = (e, singleDocument) => {
    e.preventDefault();
    console.warn("doc")
    this.setState({
      activeModal: true,
      failedPdf: singleDocument.document.documentNameString,
      documentId: singleDocument.documentId,
      bucketName: singleDocument.document.bucketName
    });
  }

  onReviewMethod = reviewMethod => {
    const { dispatch } = this.props;
    dispatch(clearDocuments());
    dispatch(setCurrentPage(0));
    dispatch(isFetchingDocuments(false));
    dispatch(selectReviewMethod(reviewMethod));
    dispatch(setItemsPerPage(25));

    if (reviewMethod === 'exceptionList') {
      dispatch(fetchDocuments());
    } else {
      dispatch(fetchDocumentsList());
    }
  };

  onSelectDocumentList = selectedDocumentList => {
    const { dispatch } = this.props;
    if (selectedDocumentList) {
      // const filterByDocumentList = Array.isArray(selectedDocumentList) ? selectedDocumentList : [selectedDocumentList];
      dispatch(selectDocumentList(selectedDocumentList.map( doc => doc.value)));
    } else {
      dispatch(selectDocumentList(null));
      dispatch(receiveDocuments([]));
    }
  };

  onSearchDocuments = e => {
    e.preventDefault();
    const { selectedDocumentList } = this.props.oaldExceptionManagement;
    if (selectedDocumentList.length) {
      this.props.dispatch(setCurrentPage(0));
      this.props.dispatch(fetchDocuments());
    }
  };

  onFinishLoadingPdf = () => {
    // Check if pdf is loaded
    const iframe = document.getElementById('documentImage').contentDocument;
    const contentType = iframe.contentType || iframe.mimeType;
    if (contentType === "application/json") {
      this.setState({
        isPdfPresent: false
      });
    } else {
      this.setState({
        isPdfPresent: true
      });
    }
  }

  // onBlur = e => {
  //   const newDocumentList = e.target.value.split(',').filter( value => value.length && !isNaN(value)).map( value => ({ value: parseInt(value, 10)}) );
  //   if (newDocumentList.length) {
  //     this.props.dispatch(addDocumentList(newDocumentList));
  //   }
  // };

  onViewDataExtracted = e => {
    $(e.target).parents('.table').find('.snippet-wrap').toggleClass('show');
  }

  onUpdateDataExtracted = singleDocument => {
    let isFormValid = true;

    singleDocument.dataElements.forEach( dataElement => {
      const { value, fieldDefinition } = dataElement;
      const { fieldType, fieldDescription } = fieldDefinition;

      if (!value) {
        toastr.error("Please complete all the document fields.");
        isFormValid = false;
        return;
      }

      if (dataElement.fieldDefinition.fieldRequired || dataElement.fieldEdited) {
        if (fieldType === "number" && !(validator.isNumeric(value) || validator.isDecimal(value))) {
          toastr.error(`The field ${fieldDescription} must be a number.`);
          isFormValid = false;
          return;
        }
        if (fieldType === "alphanumeric" && !validator.isAlphanumeric(value.replace(/\s/g, ''))) {
          toastr.error(`The field ${fieldDescription} must be alphanumeric.`);
          isFormValid = false;
          return;
        }
        if (fieldType === "date" && !moment(value, "MM DD YYYY").isValid()) {
          toastr.error(`The field ${fieldDescription} must be a date with the format MM/DD/YYYY.`);
          isFormValid = false;
          return;
        }
      }
    });

    if (isFormValid) {
      this.props.dispatch(updateDocument(singleDocument));
    }
  }

  onModalClose = () => {
    this.setState({ activeModal: false });
  }

  onPageChanged = newPage => {
    const { dispatch } = this.props;
    // console.warn('onPageChanged:: ', newPage);
    dispatch(setCurrentPage(newPage));
    dispatch(fetchDocuments());
  };

  onItemsPerPageChange = e => {
    const { dispatch } = this.props;
    dispatch(setCurrentPage(0));
    dispatch(setItemsPerPage(Number(e.target.value)));
    dispatch(fetchDocuments());
  }

  onUpdateExceptionField = (e, documentIndex, fieldName) => {
    const value = e.target ? e.target.value : moment(e._d).format('L');
    this.props.dispatch(updateDocumentField(documentIndex, fieldName, value));
  }

  render () {
    const {
      currentPage,
      selectedReviewMethod,
      selectedDocumentList,
      isFetchingDocumentList,
      documentList,
      documents,
      isFetchingDocuments,
      noDocuments,
      isFetchingDocument,
      totalItems,
      itemsPerPage,
      dropdownItems
    } = this.props.oaldExceptionManagement;

    const isExceptionList = selectedReviewMethod === 'exceptionList';

    return(
      <div className={styles.oaldDocument}>
        <form onSubmit={ e => e.preventDefault() }>
          <SelectDocumentReview onReviewMethod={this.onReviewMethod} selectedReviewMethod={selectedReviewMethod}/>
          { !isExceptionList &&
            <SearchOALDDocument
              onSelectDocumentList={this.onSelectDocumentList}
              onSearchDocuments={this.onSearchDocuments}
              selectedDocumentList={selectedDocumentList}
              isFetchingDocumentList={isFetchingDocumentList}
              documentList={documentList}
              selectedReviewMethod={selectedReviewMethod} /> }
        </form>
        { isFetchingDocuments && <CssLoad style={{ marginBottom: 10 }} /> }
        <div className="documentsWrapper">
          { isFetchingDocuments && <div className="documentOverlay" /> }
          { noDocuments ? <p className="text-center">No documents available</p> :
            <OALDDocument
              documents={documents}
              currentPage={currentPage}
              itemsPerPage={itemsPerPage}
              onViewDataExtracted={this.onViewDataExtracted}
              onUpdateDataExtracted={this.onUpdateDataExtracted}
              onUpdateExceptionField={this.onUpdateExceptionField}
              showDocumentModal={this.showDocumentModal}
              isFetchingDocument={isFetchingDocument} /> }
          { isFetchingDocuments && documents.length > 2 && <CssLoad /> }
          { documents.length > 0 &&
            <Pagination
              totalItems={totalItems}
              itemsPerPage={itemsPerPage}
              totalPages={Math.ceil(totalItems / itemsPerPage)}
              dropdownItems={dropdownItems}
              currentPage={currentPage}
              onPageChanged={this.onPageChanged}
              onItemsPerPageChange={this.onItemsPerPageChange} /> }
        </div>
        <FailedDocumentModal
          activeModal={this.state.activeModal}
          failedPdf={this.state.failedPdf}
          documentId={this.state.documentId}
          bucketName={this.state.bucketName}
          onClose={this.onModalClose}
          onFinishLoadingPdf={this.onFinishLoadingPdf}
          isPdfPresent={this.state.isPdfPresent} />
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    oaldExceptionManagement: state.oaldExceptionManagement
  };
}

export default connect(mapStateToProps)(OALDExceptionManagement);
