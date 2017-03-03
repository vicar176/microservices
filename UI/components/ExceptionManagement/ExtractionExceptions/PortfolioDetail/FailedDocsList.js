import React from 'react';
import { Scrollbars } from 'react-custom-scrollbars';
import styles from '../index.scss';

export default function FailedDocsList({ failedDocList, onClickFailedDocument }) {
  return(
    <div className={styles.popOverWrap}>
      { failedDocList.totalItems &&
        <Scrollbars style={{ width: 150, height: (failedDocList.items.length < 7) ? ((failedDocList.items.length*2) + .4 + 'em') : '10em' }}>
          <ul className={styles.failedDocsList}>
            { failedDocList.items.map(failedDoc => {
              return (
                <li key={failedDoc.documentId}>
                  <a
                    href={"#"+failedDoc.documentId}
                    title="Failed Documents"
                    data-target="failed-docs-modal"
                    data-pdf={failedDoc.documentNameString}
                    data-id={failedDoc.documentId}
                    data-bucketName={failedDoc.bucketName}
                    onClick={onClickFailedDocument}>{failedDoc.documentId}</a>
                </li>
              );
            })}
          </ul>
        </Scrollbars> }
    </div>
  );
}
