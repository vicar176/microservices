import { difference } from 'lodash';
import {
  SELECT_DOCTYPE,
  REQUEST_SELLERS,
  ERROR_REQUEST_SELLERS,
  RECEIVE_SELLERS,
  SELECT_TEMPLATE_SELLER,
  SELECT_TEMPLATE_LENDER,
  ADD_TEMPLATE_AFFINITIES,
  REQUEST_LENDER_AFFINITIES,
  ERROR_REQUEST_LENDER_AFFINITIES,
  RECEIVE_LENDER_AFFINITIES,
  SET_TEMPLATE_WIDTH,
  SELECT_ZOOM_LEVEL,
  SELECT_TEMPLATE_NAME,
  SELECT_TEMPLATE_FILE,
  CANCEL_TEMPLATE,
  REQUEST_TEMPLATE_PROFILE,
  ERROR_REQUEST_TEMPLATE_PROFILE,
  RECEIVE_TEMPLATE_PROFILE,
  SAVING_TEMPLATE_PROFILE,
  SAVED_TEMPLATE_PROFILE,
  ADD_LENDER_AFFINITIES,
  REMOVE_LENDER_AFFINITIES,
  REQUEST_LENDERS,
  ERROR_REQUEST_LENDERS,
  RECEIVE_LENDERS,
  CREATE_NEW_TEMPLATE,
  RESET_LENDER_AFFINITIES,
  SET_TOTAL_PAGES,
  SELECT_PAGE,
  SHOW_MAPPING,
  SAVING_TEMPLATE_ERROR,
  SELECTED_DOCTYPE_LENDER_SELLER_AFFINITIES,
  RECEIVE_METADATA_TEMPLATE,
  IS_FETCHING_FILTER_GRID,
  SAVE_SAMPLE_TEMPLATE_NAME,
  CLEAN_SAMPLE_TEMPLATE_NAME
} from 'actions/template';

const initialState = {
  selectedDocType: null,
  isFetchingSellers: false,
  selectedSeller: null,
  isFetchingLenders: false,
  selectedLender: null,
  isFetchingAffinities: false,
  // move to constants
  addAffinities: '',
  selectedTemplateName: '',
  selectedTemplateFile: '',
  zoomLevel: 100,
  isTemplateLoaded: false,
  templateWidth: 0,
  wrapperWidth: 0,
  isFetchingProfile: false,
  isProfileLoaded: false,
  isSavingProfile: false,
  associatedAffinities: [],
  createNewTemplate: false,
  selectedPage: 1,
  totalPages: 1,
  isMappingVisible: true,
  templateLength: 0,
  isFetchingFilterGrid: true,
  templateSaved: false,
  sampleTemplateName: ''
}

export default function template (state = initialState, action) {

  switch (action.type) {

    case SELECT_DOCTYPE:
      return Object.assign({}, initialState, {
        selectedDocType: action.docType
      });

    case SELECT_TEMPLATE_LENDER:
      return Object.assign({}, initialState, {
        selectedDocType: state.selectedDocType,
        selectedSeller: state.selectedSeller,
        selectedLender: action.lender,
      });

    case SELECT_TEMPLATE_SELLER:
      return Object.assign({}, initialState, {
        selectedDocType: state.selectedDocType,
        selectedSeller: action.seller,
      });

    case REQUEST_SELLERS:
      return Object.assign({}, state, { isFetchingSellers: true });

    case ERROR_REQUEST_SELLERS:
      return Object.assign({}, state, { isFetchingSellers: false });

    case RECEIVE_SELLERS:
      return Object.assign({}, state, { isFetchingSellers: false });

    case REQUEST_LENDERS:
      return Object.assign({}, state, { isFetchingLenders: true });

    case ERROR_REQUEST_LENDERS:
      return Object.assign({}, state, { isFetchingLenders: false });

    case RECEIVE_LENDERS:
      return Object.assign({}, state, { isFetchingLenders: false });

    case ADD_TEMPLATE_AFFINITIES:
      return Object.assign({}, state, {
        addAffinities: action.value,
        selectedTemplateName: '',
        selectedTemplateFile: '',
      });

    case REQUEST_LENDER_AFFINITIES:
      return Object.assign({}, state, { isFetchingAffinities: true });

    case ERROR_REQUEST_LENDER_AFFINITIES:
      return Object.assign({}, state, { isFetchingAffinities: false });

    case RECEIVE_LENDER_AFFINITIES:
      return Object.assign({}, state, { isFetchingAffinities: false});

    case SET_TEMPLATE_WIDTH:
      return Object.assign({}, state, {
        isTemplateLoaded: true,
        templateWidth: action.templateWidth,
        wrapperWidth: action.wrapperWidth,
      });

    case SELECT_ZOOM_LEVEL:
      return Object.assign({}, state, {
        zoomLevel: action.zoomLevel,
      });

    case SELECTED_DOCTYPE_LENDER_SELLER_AFFINITIES:
      return Object.assign({}, state, {
        selectedTemplateName: createTemplateName(state)
      });

    case SELECT_TEMPLATE_NAME:
      return Object.assign({}, state, {
        selectedTemplateName: action.name,
        associatedAffinities: [],
      });

    case SELECT_TEMPLATE_FILE:
      return Object.assign({}, state, {
        selectedTemplateFile: action.file,
        zoomLevel: 100,
        isTemplateLoaded: false,
        selectedPage: 1,
      });

    case CREATE_NEW_TEMPLATE:
      return Object.assign({}, state, {
        createNewTemplate: action.value,
        selectedTemplateName: action.value ? createTemplateName(state) : '',
        selectedTemplateFile: '',
        zoomLevel: 100,
        isTemplateLoaded: false,
        associatedAffinities: []
      });

    case CANCEL_TEMPLATE:
      return Object.assign({}, initialState);

    case REQUEST_TEMPLATE_PROFILE:
      return Object.assign({}, state, { isFetchingProfile: action.isFetching});

    case ERROR_REQUEST_TEMPLATE_PROFILE:
      return Object.assign({}, state, { isFetchingProfile: false});

    case RECEIVE_TEMPLATE_PROFILE:
      return Object.assign({}, state, {
        isFetchingProfile: false,
        isProfileLoaded: true,
        templateLength: action.profile.templates ? action.profile.templates.length + 1 : 1,
      });

    case SAVING_TEMPLATE_PROFILE:
      return Object.assign({}, state, { isSavingProfile: true });

    case SAVED_TEMPLATE_PROFILE:
      return Object.assign({}, state, {
        isSavingProfile: false,
        templateSaved: true
      });

    case ADD_LENDER_AFFINITIES:
      if (!action.affinities) {
        return state;
      }
      return Object.assign({}, state, { associatedAffinities: state.associatedAffinities.concat(action.affinities) });

    case REMOVE_LENDER_AFFINITIES:
      return Object.assign({}, state, { associatedAffinities: difference(state.associatedAffinities, action.affinities) });

    case RESET_LENDER_AFFINITIES:
      return Object.assign({}, state, { associatedAffinities: [] });

    case SET_TOTAL_PAGES:
      return Object.assign({}, state, { totalPages: action.pages });

    case SELECT_PAGE:
      return Object.assign({}, state, { selectedPage: action.page });

    case SHOW_MAPPING:
      return Object.assign({}, state, { isMappingVisible: action.visible });

    case SAVING_TEMPLATE_ERROR:
      return Object.assign({}, state, { isSavingProfile: false });

    case IS_FETCHING_FILTER_GRID:
      return Object.assign({}, state, { isFetchingFilterGrid: action.isFetching });

    case SAVE_SAMPLE_TEMPLATE_NAME:
      return Object.assign({}, state, { sampleTemplateName: action.sampleName});

    case CLEAN_SAMPLE_TEMPLATE_NAME:
      return Object.assign({}, state, { sampleTemplateName: ''});

    default:
      return state;
  }
}

function createTemplateName (state) {
  if (state.selectedDocType && state.selectedSeller && state.selectedLender) {
    if (state.addAffinities === 'yes') {
      return state.selectedDocType.documentType.code.replace(/\s/g,'_').toUpperCase() + '_' + state.selectedSeller.name + '_' + state.selectedLender.name + '_Affinity_' + state.templateLength;
    }
    return state.selectedDocType.documentType.code.replace(/\s/g,'_').toUpperCase() + '_' + state.selectedSeller.name + '_' + state.selectedLender.name;
  }
}
