import React, { Component } from 'react';
import { connect } from 'react-redux';
import ManageList from './ManageList';

import {
  reportNameChange,
  reportDescriptionChange,
  reportUrlChange,
  reportCategoryChange,
  reportSubCategoryChange,
  addCategory,
  addSubCategory,
  categoryChange,
  subcategoryChange,
  categoryRemove,
  subcategoryRemove,
  newCategoryNameChange,
  newSubcategoryNameChange
} from 'actions/report';

// components
import MainContent from 'components/Common/MainContent';
import ViewProfiles from 'components/ViewProfiles';
import ReportForm from './ReportForm';

class Report extends Component {

  getFilterRows = () => {
    const numberOfRows = 60;
    const _rows = [];

    for (let i = 0; i < numberOfRows; i++) {
      _rows.push({
        id: i,
        reportName: ['OALD Profiles', 'Metadata  Template Profles', 'Extracted metadata', 'Extraction Failures', 'OALD Received', 'OALD Validated'][Math.floor((Math.random() * 6))],
        reportDescription: ['View all OALD profiles', 'View all Metadata  Template profiles', 'Summary counts of documents with extracted metadataby porfolio'][Math.floor((Math.random() * 3))],
        reportCategory: ['Profile', 'Document', 'Account' , 'Process', 'Portfolio', 'Other'][Math.floor((Math.random() * 6))],
        reportSubCategory: ['OALD', 'Metadata', 'Validation', 'Verification', 'Other'][Math.floor((Math.random() * 5))],
        modified: ['04/20/2016', '05/15/2014', '01/02/2016', '03/25/2015'][Math.floor((Math.random() * 4))],
        configId: i,
      });
    }

    return _rows;
  };

  getFilterHeading = () => {
    return [{
      key: 'id',
      name: 'ID',
      width: 50,
      sortable: true
    },{
      key: 'reportName',
      name: 'Name',
      sortable: true,
      filterable: true
    },{
      key: 'reportDescription',
      name: 'Description',
      sortable: true,
      filterable: true
    },{
      key: 'reportCategory',
      name: 'Category',
      sortable: true,
      filterable: true
    },{
      key: 'reportSubCategory',
      name: 'Subcategory',
      sortable: true,
      filterable: true
    },{
      key: 'modified',
      name: 'Modified',
      sortable: true,
      width: 100,
      filterable: true
    }];
  };

  render () {
    const { selectedTab } = this.props.common;
    const { reportCategories, reportSubcategories, newCatgoryName, newSubcatgoryName } = this.props.report;
    const isSavingReport = false;
    return (
      <MainContent routes={this.props.routes} mainTitle='Define Reports' tabs={[{id: 'configReport', name: 'Configure Report'}, {id: 'viewReports', name: 'View Reports'}]}>
        <div role="tabpanel" className="tab-pane active" id="configReport">
          <h2 className="page-header">Define Report</h2>
          <div className="wrap-forms">
            <form onSubmit={ e => e.preventDefault() }>
              <ReportForm
                reportProfile={this.props.reportProfile}
                onNameChange={(value) => this.props.dispatch(reportNameChange(value))}
                onDescriptionChange={(value) => this.props.dispatch(reportDescriptionChange(value))}
                onUrlChange={(value) => this.props.dispatch(reportUrlChange(value))}
                onPreviewReport={()=>console.log('preview changed')}
                onCategoryChange={(value) => this.props.dispatch(reportCategoryChange(value))}
                onSubCategoryChange={(value) => this.props.dispatch(reportSubCategoryChange(value))}
                onManageCategoryList={()=>jQuery('#manageCategoryList').modal('show')}
                onManageSubCategoryList={()=>jQuery('#manageSubcategoryList').modal('show')}
              />
            <ManageList
              manageListId='manageCategoryList'
              title='Manage Categories'
              currentNewName={newCatgoryName}
              currentNewNameChange={(value) => this.props.dispatch(newCategoryNameChange(value))}
              currentList={reportCategories}
              addItem={(value) => this.props.dispatch(addCategory(value))}
              editItem={(categoryID, value) => this.props.dispatch(categoryChange(categoryID, value))}
              removeItem={(value) => this.props.dispatch(categoryRemove(value))}
              />
            <ManageList
              manageListId='manageSubcategoryList'
              title='Manage Sub Categories'
              currentNewName={newSubcatgoryName}
              currentNewNameChange={(value) => this.props.dispatch(newSubcategoryNameChange(value))}
              currentList={reportSubcategories}
              addItem={(value) => this.props.dispatch(addSubCategory(value))}
              editItem={(subcategoryID, value) => this.props.dispatch(subcategoryChange(subcategoryID, value))}
              removeItem={(value) => this.props.dispatch(subcategoryRemove(value))}
              />
              <div className="row">
                <div className="col-md-offset-5 col-xs-4">
                  <button name="save" className="btn btn-primary" style={{marginRight: "1em"}} onClick={()=> console.log('saving')}>{isSavingReport ? 'Saving' : 'Save'}</button>
                  <button name="cancel" className="btn btn-default" onClick={() => console.log('cancel')}>Cancel</button>
                </div>
              </div>
            </form>
          </div>
        </div>
        <div role="tabpanel" className="tab-pane " id="viewReports">
          <h2 className="page-header">View Reports</h2>
          <div className="wrap-forms">
            { (selectedTab === 'viewReports') &&
              <ViewProfiles
                onEditProfileButton={this.onEditProfileButton}
                getFilterHeading={this.getFilterHeading}
                getFilterRows={this.getFilterRows} />
            }
          </div>
        </div>
      </MainContent>
    )
  }
}

function mapStateToProps(state) {
  return {
    common: state.common,
    report: state.report,
    reportProfile: state.reportProfile
  };
}

export default connect(mapStateToProps)(Report);
