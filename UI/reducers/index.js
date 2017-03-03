import { combineReducers } from 'redux';
import common from './common';
import oald from './oald';
import oaldProfile from './oaldProfile';
import oaldPortfolio from './oaldPortfolio';
import oaldExceptionManagement from './oaldExceptionManagement';
import accountExceptionManagement from './accountExceptionManagement';
import template from './template';
import templateAreas from './templateAreas';
import templateAreasRef from './templateAreasRef';
import templatePortfolio from './templatePortfolio';
import templateProfile from './templateProfile';
import auth from './auth';
import documentField from './documentField';
import documentFieldProfile from './documentFieldProfile';
import fieldDefinition from './fieldDefinition';
import fieldDefinitionProfile from './fieldDefinitionProfile';
import ingestionConfiguration from './ingestionConfiguration';
import previousVersion from './previousVersion';
import viewProfiles from './viewProfiles';
import extractionExceptions from './extractionExceptions';
import sourceProfile from './sourceProfile';
import sourceLocation from './sourceLocation';
import report from './report';
import reportProfile from './reportProfile';
import ingestionReprocessing from './ingestionReprocessing';
/**
 * combine all reducer in one three
 */
const rootReducer = combineReducers({
  common,
  oald,
  auth,
  oaldProfile,
  oaldPortfolio,
  oaldExceptionManagement,
  accountExceptionManagement,
  template,
  templateAreas,
  templateAreasRef,
  templatePortfolio,
  templateProfile,
  fieldDefinition,
  fieldDefinitionProfile,
  ingestionConfiguration,
  previousVersion,
  viewProfiles,
  extractionExceptions,
  documentField,
  documentFieldProfile,
  sourceProfile,
  sourceLocation,
  report,
  reportProfile,
  ingestionReprocessing
});

export default rootReducer
