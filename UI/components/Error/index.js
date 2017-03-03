import React from 'react';
import styles from './index.scss';

export default function Error () {
  return(
     <div className="row">
      <div className="col-md-12">
          <div className= {styles.errorTemplate}>
              <h1 className="text-warning">
                  Oops!</h1>
                <h1 className="text-danger">
                  <strong>404</strong> Not Found</h1>
              <h3 className="text-muted">
                  Sorry, an error has occured, Requested page not found!
              </h3>
              <div className={styles.errorActions}>
              <button type="button" className="btn btn-primary btn-lg"> Take Me Home</button>
              <button type="button" className="btn btn-default btn-lg"> Contact Support</button>

              </div>
          </div>
      </div>
  </div>
  );
}
