import React from 'react';
import styles from './cssLoad.scss';

export default function CssLoad ({style}) {
  return (
    <div className={styles.cssloadContainer} style={style}></div>
  );
}
