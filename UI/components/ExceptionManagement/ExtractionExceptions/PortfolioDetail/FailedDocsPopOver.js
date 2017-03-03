import React from 'react';
import { Overlay, Popover } from 'react-bootstrap';
import CssLoad from 'components/Common/cssLoad';
import FailedDocsList from 'FailedDocsList';
import { findDOMNode } from 'react-dom';
import styles from '../index.scss';

export default function FailedDocsPopOver ({
  id,
  target,
  failedDocList,
  isFetchingFailedDocs,
  onOpenPopOver,
  onClickFailedDocument
}) {
  function renderTitle() {
    return <span className={styles.failedDocsClosePopOver}>Docs Failed <a href="#" id="popOverCloseBtn" className="glyphicon glyphicon-remove pull-right" onClick={onOpenPopOver} /></span>
  }

  return (
    <Overlay
      id={"popover-overlay" + id}
      container={this}
      show={!!target}
      target={()=> findDOMNode(target)}
      onHide={() => {}}
      animation={false}
      rootClose>
      <Popover id={styles.failedDocsPopOver} title={renderTitle()} className={styles.failedDocsPopOver}>
        { isFetchingFailedDocs ? <CssLoad /> : <FailedDocsList failedDocList={failedDocList} onClickFailedDocument={onClickFailedDocument} /> }
      </Popover>
    </Overlay>
  );
}
