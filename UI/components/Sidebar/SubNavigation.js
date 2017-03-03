import React from 'react';
import { Link } from 'react-router';

export default function SubNavigation ({ navId, subNav, path, isExpanded }) {
  return (
    <ul id={navId} className={"nav nav-second-level collapse" + (isExpanded ? ' in' : '')}>
      {Object.keys(subNav).map(tabId => {
        const id = tabId.split('/').pop();
        return (
          <li key={tabId}>
            <Link id={id} to={'/'+tabId} className={id === path ? 'active' : ''}>{subNav[tabId].name}</Link>
          </li>
        )
      })}
    </ul>
  )
}
