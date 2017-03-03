import React from 'react';

// Components
import { Tooltip, OverlayTrigger} from 'react-bootstrap';
import IngestionSwitch from 'components/ExceptionManagement/IngestionSwitch';
import CssLoad from 'components/Common/cssLoad';
import TableTab from 'TableTab';

export default function IngestionStatus (props) {
  const {
    status,
    batches,
    summary,
    getStatus,
    getSummary,
    getBatches,
    isFetchingStatus,
    isFetchingSummary,
    isFetchingBatches,
    subTab,
    onSubTabChange
  } = props;

  return (
    <div className="row">
      <ul className="nav nav-tabs" role="tablist">
        <li role="presentation" className={subTab === "batchExecutions" ? 'active' : ''}>
            <a href={'#batchExecutions'}
               aria-controls="batchExecutions"
               role="tab"
               data-toggle="tab"
               className="content-tab"
               onClick={() => onSubTabChange("batchExecutions")}>Batch Executions</a>
        </li>
        <li role="presentation" className={subTab === "ingestionTracker" ? 'active' : ''}>
            <a href={'#ingestionTracker'}
               aria-controls="ingestionTracker"
               role="tab"
               data-toggle="tab"
               className="content-tab"
               onClick={() => onSubTabChange("ingestionTracker")}>Ingestion Tracker</a>
        </li>
        <li role="presentation" className={subTab === "ingestionStatus" ? 'active' : ''}>
            <a href={'#ingestionStatus'}
               aria-controls="ingestionStatus"
               role="tab"
               data-toggle="tab"
               className="content-tab"
               onClick={() => onSubTabChange("ingestionStatus")}>Ingestion Status</a>
        </li>
      </ul>
      <div className="tabContent">
      { (subTab === "batchExecutions") &&
        <div role="tabpanel" class="tab-pane active" id="batch-executions">
          <TableTab
            getValues={getBatches}
            isFetchingValues={isFetchingBatches}
            values={batches}/>
        </div>
      }
      { (subTab === "ingestionTracker") &&
        <div role="tabpanel" class="tab-pane active" id="ingestionTracker">
          <TableTab
            getValues={getSummary}
            isFetchingValues={isFetchingSummary}
            values={summary}/>
        </div>
      }
      { (subTab === "ingestionStatus") &&
        <div role="tabpanel" class="tab-pane active" id="ingestion-status">
          <TableTab
            getValues={getStatus}
            isFetchingValues={isFetchingStatus}
            values={status}/>
        </div>
      }
      </div>
    </div>
  );
}
