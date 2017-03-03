import {
  SELECT_REVIEW_METHOD,
  ACTIVE_VARIFICATION,
  REQUEST_ACCOUNT_DATA,
  ERROR_REQUEST_ACCOUNT_DATA,
  RECEIVE_ACCOUNT_DATA,
  EDITING_MODE_ACTIVE
} from 'actions/accountExceptionManagement';

const initialState = {
  selectedReviewMethod: 'list',
  isAccountVerified: false,
  isFetchingAccountsData: false,
  accountsData: null,
  editProfile: false
};

export default function accountExceptionManagement (state = initialState, action) {

  switch (action.type) {
    case SELECT_REVIEW_METHOD:
      return Object.assign({}, state, { selectedReviewMethod: action.reviewMethod });

    case ACTIVE_VARIFICATION:
      return Object.assign({}, state, { isAccountVerified: action.verificationState });

    case EDITING_MODE_ACTIVE:
      return Object.assign({}, state, { editProfile: action.editedField });

    case REQUEST_ACCOUNT_DATA:
      return Object.assign({}, state, { isFetchingAccountsData: true });

    case ERROR_REQUEST_ACCOUNT_DATA:
      return Object.assign({}, state, { isFetchingAccountsData: false });

    case RECEIVE_ACCOUNT_DATA:
      return Object.assign({}, state, {
        isFetchingAccountsData: false,
        accountsData: action.accountsData
      });

    default:
      return state;
  }

}
