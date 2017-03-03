import React from 'react';
import logo from 'images/everest_logo.png';
import logoText from 'images/everest_logo_text.png';
import styles from 'index.scss';

export default function HeaderCollapsable () {
  return (
    <header className="sidebar-header">
      <a href="/#/">
        <img className="logo" src={logo} alt="Everest"/>
        <img className="logoText logo" src={logoText} alt="Everest"/>
      </a>
      <button data-target="sidebar-toggle"><span /></button>
    </header>
  )
}
