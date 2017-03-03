import React, { Component } from 'react';
import { connect } from 'react-redux';
import { hashHistory } from 'react-router';
import { Scrollbars } from 'react-custom-scrollbars';
import { Tabs, Tab } from 'react-bootstrap';
import moment from 'moment';

// components
import CssLoad from 'components/Common/cssLoad';
import ViewProfiles from 'components/ViewProfiles';
import FailedDocsPopOver from 'FailedDocsPopOver';
import FailedDocumentModal from 'FailedDocumentModal';
import ConfirmPopup from 'components/Common/ConfirmPopup';
import toastr from 'toastr';

// actions
import {
  fetchFailedDocs,
  fetchPortfolioDetail,
  cleanFailedDocuments,
  templateForReprocess,
  reprocessTemplate,
  selectPortfoliosId,
  isPortfolioSelected,
  fetchPortfoliosId,
  cleanPortfolioDetail,
  setPortfolioActiveTab
} from 'actions/extractionExceptions';
import { saveProfileData, isEditMode } from 'actions/common';
import { cleanProfileGrid } from 'actions/viewProfiles';

// styles
import styles from '../index.scss';

class PortfolioDetail extends Component {
  constructor(props) {
    super(props);
    this.state = {
      key: this.props.templateData.templateFoundCount ? 1 : 2,
      popOverTarget: null,
      activeModal: false,
      iframeSrc: '',
      bucketName: '',
      isPdfPresent: true
    };
  }

  componentWillUnmount() {
    this.cleanFailedPopOver();
  }

  getTemplateFilterHeading = () => {
    return [
      { key: 'templateName',
        query: 'extraction.templateMappingProfile.name',
        name: 'Template Name',
        sortable: true,
        filterable: true
      },{
        key: 'version',
        query: 'extraction.templateMappingProfile.version',
        name: 'Version #',
        type: 'number',
        sortable: true,
        filterable: true
      },{
        key: 'updatedBy',
        query: 'extraction.templateMappingProfile.updatedBy',
        name: 'Modified By',
        sortable: true,
        filterable: true
      },{
        key: 'updateDate',
        query: 'extraction.templateMappingProfile.updateDate',
        name: 'Modified',
        sortable: true,
        filterable: true,
        rowFormatter: this.renderUpdateDate,
        type: "date"
      },{
        key: 'lastRun',
        name: 'Last Run',
        rowFormatter: this.renderLastRunDate
      },{
        key: 'documentsFailed',
        name: 'Docs Failed',
        sortable: true,
        rowFormatter: this.renderFailedCta
      },{
        key: 'configId',
        className: 'text-center',
        style: { 'maxWidth': 50 },
        rowFormatter: this.renderEditRowButton
      },{
        key: 'reprocess',
        btnHead: 'filter',
        className: 'text-center',
        style: { 'maxWidth': 50 },
        rowFormatter: this.renderReprocessCta
      }
    ];
  }

  getTemplateFilterRows = query => {
    const requestedParams = {
      portfolioId: this.props.templateData.portfolioNumber,
      templateType: 'templatesFound',
      queryString: query
    };
    return this.props.dispatch(fetchPortfolioDetail(requestedParams)).then(response => {
      // this.reformatTemplates(query, response, { configId: 0});
      return {
        totalItems: response.totalItems,
        itemsPerPage: response.itemsPerPage,
        filters: query.filters,
        sorts: query.sorts,
        items: response.items
      }
    });
  }

  getNotFoundFilterHeading = () => {
    return [
      { key: 'documentType.code',
        sortcol: 'originalDocumentType.code',
        query: 'document.originalDocumentType',
        name: 'Document Type',
        className: 'text-upper',
        sortable: true,
        filterable: true,
        rowFormatter: this.renderDocTypeRow
      },{
        key: 'originalLender',
        query: 'originalLenderName',
        name: 'Original Lender',
        sortable: true,
        filterable: true
      },{
        key: 'documentsFailed',
        name: 'Document Failed',
        sortable: true,
        rowFormatter: this.renderFailedCta
      },{
        key: 'configId',
        btnFilter: 'clean',
        className: 'text-center',
        style: {'maxWidth': 50},
        rowFormatter: this.renderEditRowButton
      },{
        key: 'reprocess',
        btnHead: 'filter',
        className: 'text-center',
        style: { 'maxWidth': 50 },
        rowFormatter: this.renderReprocessCta
      }
    ];
  }

  getNotFoundFilterRows = query => {
    const requestedParams = {
      portfolioId: this.props.templateData.portfolioNumber,
      templateType: 'templatesNotFound',
      queryString: query
    };
    return this.props.dispatch(fetchPortfolioDetail(requestedParams)).then(response => {
      // this.reformatTemplates(query, response, { configId: 0});
      return {
        totalItems: response.totalItems,
        itemsPerPage: response.itemsPerPage,
        filters: query.filters,
        sorts: query.sorts,
        items: response.items
      }
    });
  }

  addNewColumns(arr, val) {
    Object.keys(arr).forEach(el => arr[el] = val);
    return arr;
  }

  // reformatTemplates(query, templateData, newCols) {
  //   const filledGrid = [];
  //   if (templateData.totalItems) {
  //     for (let i = 0; i < templateData.totalItems; i++) {
  //       filledGrid.push(Object.assign({}, templateData.items[i], this.addNewColumns(newCols, i)));
  //     }
  //   }
  //   return {
  //     totalItems: templateData.totalItems,
  //     itemsPerPage: templateData.itemsPerPage,
  //     filters: query.filters,
  //     sorts: query.sorts,
  //     items: filledGrid
  //   }
  // }

  cleanFailedPopOver() {
    this.setState({ popOverTarget: null });
    this.props.dispatch(cleanFailedDocuments());
  }

  onChangeDetailTab = key => {
    const { dispatch } = this.props;
    const {templatesFound,templatesNotFound} = this.props.extractionExceptions
    if (key !== 1 && templatesFound || key !== 2 && templatesNotFound) {
      this.setState({ popOverTarget: null, key });
      dispatch(cleanProfileGrid());
      dispatch(cleanPortfolioDetail());
      dispatch(cleanFailedDocuments());
    }
  }

  onOpenPopOver = e => {
    const { dispatch } = this.props;
    const elemTarget = e.target;
    e.preventDefault();
    this.cleanFailedPopOver();
    if (elemTarget !== this.state.popOverTarget && elemTarget.id !== 'popOverCloseBtn') {
      this.setState({ popOverTarget: elemTarget });
      // Notes: dev[JPM] N.1
      // react 0.13.3, IE10 e.target.dataset is undefined
      // but e.target.getAttribute('data-mojitos') works.
      // the other browsers are fine.
      dispatch(fetchFailedDocs(elemTarget.dataset));
    }
  }

  onModalClose = () => {
    this.setState({ activeModal: false });
  }

  onModalOpen = e => {
    e.preventDefault();
    console.warn("open")
    this.setState({
      activeModal: e.target.getAttribute('data-target'),
      failedPdf: e.target.getAttribute('data-pdf'),
      documentId: e.target.getAttribute('data-id'),
      bucketName: e.target.getAttribute('data-bucketName')
    })
  }

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

  onConfirmReprocessModal = () => {
    this.onReprocessDocuments();
    this.setState({ activeModal: false });
    $('#reprocessModal').modal('hide');
  }

  onReprocessDocuments = () => {
    const { templateForReprocess } = this.props.extractionExceptions;
    const { portfolioNumber } = this.props.templateData;
    this.props.dispatch(reprocessTemplate(templateForReprocess, portfolioNumber)).then(() => {
      this.props.dispatch(selectPortfoliosId(null));
      this.props.dispatch(setPortfolioActiveTab(null));
      this.props.dispatch(isPortfolioSelected(false));
      this.props.dispatch(fetchPortfoliosId());
    });
    this.setState({ activeModal: false });
  }

  onEditTemplate = e => {
    e.preventDefault();
    const templateType = e.target.getAttribute('data-template-type');
    const rowId = e.target.id.split('-')[0];
    const templateProps = this.props.extractionExceptions[templateType]['items'][rowId];
    let templateParam = { exception: true };
    if (templateType === 'templatesNotFound') {
      templateParam = { isNotFoundTemplate: true }
    }
    const template = Object.assign(templateProps, templateParam);
    this.props.dispatch(isEditMode(true));
    this.props.dispatch(saveProfileData(template));
    hashHistory.push('media-profile/template-profile');
  }

  onOpenReprocessModal = e => {
    e.preventDefault();
    const templateType = e.target.getAttribute('data-template-type');
    const rowId = e.target.id.split('-')[0];
    const template = Object.assign(this.props.extractionExceptions[templateType]['items'][rowId], { activeModal: true });
    this.props.dispatch(templateForReprocess(template));
    $('#reprocessModal').modal('show');
  }

  renderEditRowButton = props => {
    if (!props.reprocess) {
      return <a
        href="#"
        title="Configure"
        id={props.rowId}
        className={"glyphicon glyphicon-edit " + styles.reprocessCta}
        data-template-type={props.templateName ? 'templatesFound' : 'templatesNotFound'}
        onClick={this.onEditTemplate}></a>
    }
    return "-";
  }

  renderReprocessCta = props => {
    if (!props.reprocess) {
      return <a
        href="#"
        title="Reprocess"
        id={props.rowId}
        data-target="reprocess-modal"
        className={"glyphicon glyphicon-check " + styles.reprocessCta}
        data-template-type={props.templateName ? 'templatesFound' : 'templatesNotFound'}
        onClick={this.onOpenReprocessModal}></a>
    }
    return <span className={"glyphicon glyphicon-refresh "+ styles.rotate}></span>
  }

  renderDocTypeRow = props => {
    if (props.documentType && props.documentType.code) {
      return <span>{props.documentType.code}</span>
    }
  }

  renderFailedCta = val => {
    if (!val.reprocess) {
      const dataType = val.templateId ? val.templateId : val.documentType.code;
      const portfolio = this.props.extractionExceptions.activePortfolioTabId;
      const lenderName = val.originalLender;
      // dispatch(fetchFailedDocs(elemTarget.dataset));
      return <a
        href="#"
        title={val.text}
        data-template={val.templateName}
        data-portfolio={portfolio}
        data-type={dataType}
        data-lender={lenderName}
        id={'popover-trigger-' + portfolio + '-' + val.rowId}
        onClick={this.onOpenPopOver}>{val.text}</a>
    }
    return "Reprocessing";
  }

  renderUpdateDate = val => {
    return <span>{val.updateDate && moment(val.updateDate).utc().format('MMMM Do YYYY, hh:mm:ss a')}</span>
  }

  renderLastRunDate = val => {
    return <span>{val.lastRun && moment(val.lastRun).utc().format('MMMM Do YYYY, hh:mm:ss a')}</span>
  }

  render() {
    const {
      failedDocList,
      isFetchingFailedDocs,
      isFetchingFoundTemplates,
      isFetchingNotFoundTemplates
    } = this.props.extractionExceptions;
    const { templateFoundCount, templateNotFoundCount } = this.props.templateData;

    return (
      <div className="portfolio-detail-wrap">
        <Tabs
          id="tabs-portfolio-detail"
          animation={false}
          activeKey={this.state.key}
          defaultActiveKey={this.state.key}
          onSelect={this.onChangeDetailTab}>
          <Tab eventKey={1} title="Templates" disabled={!templateFoundCount}>
            { this.state.key === 1 &&
              <ViewProfiles
                getFilterHeading={this.getTemplateFilterHeading}
                getFilterRows={this.getTemplateFilterRows}
                isFetchingRows={isFetchingFoundTemplates}
                onEditProfileButton={this.onEditProfileButton} /> }
          </Tab>
          <Tab eventKey={2} title="Templates Not found" disabled={!templateNotFoundCount}>
            { this.state.key === 2 &&
              <ViewProfiles
                getFilterHeading={this.getNotFoundFilterHeading}
                getFilterRows={this.getNotFoundFilterRows}
                isFetchingRows={isFetchingNotFoundTemplates}
                onEditProfileButton={this.onEditProfileButton} /> }
          </Tab>
        </Tabs>
        <FailedDocsPopOver
          id="failed-documents"
          failedDocList={failedDocList}
          target={this.state.popOverTarget}
          onOpenPopOver={this.onOpenPopOver}
          onClickFailedDocument={this.onModalOpen}
          isFetchingFailedDocs={isFetchingFailedDocs}/>
        <FailedDocumentModal
          activeModal={this.state.activeModal}
          failedPdf={this.state.failedPdf}
          bucketName={this.state.bucketName}
          documentId={this.state.documentId}
          iframeSrc={this.state.iframeSrc}
          onClose={this.onModalClose}
          onFinishLoadingPdf={this.onFinishLoadingPdf}
          isPdfPresent={this.state.isPdfPresent}/>
        <ConfirmPopup
          confirmId='reprocessModal'
          onConfirm={this.onConfirmReprocessModal}
          onCancelClick={() => this.setState({activeModal: false})}
          title='Reprocess Template'
          content='Are you sure you want to reprocess the selected template. Continue?'/>
      </div>
    )
  }
}

function mapStateToProps(state) {
  return {
    viewProfiles: state.viewProfiles,
    extractionExceptions: state.extractionExceptions
  };
}

export default connect(mapStateToProps)(PortfolioDetail);
