import React, { Component } from 'react';
import NavigationHeading from 'NavigationHeading';
import SubNavigation from 'SubNavigation';

export default function Navigation ({ navigation, path }) {
  return (
    <nav className="main-navigation">
      <ul id='sidebarnav' className='nav' role='tablist' aria-multiselectable='false'>
        {navigation &&
          navigation.items.map( menu => {
            const expanded = !!Object.keys(menu.links).filter(item => item.indexOf(path) > -1)[0];
            return (
              <li className='subnav panel' key={menu.id}>
                <NavigationHeading menu={menu} />
                <SubNavigation navId={menu.id} path={path} subNav={menu.links} isExpanded={expanded} />
              </li>
            )
          })
        }
      </ul>
    </nav>
  )
}
