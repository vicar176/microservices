import React from 'react';

export default function NavigationHeading ({ menu }) {
  return (
    <a href={'#' + menu.id}
      className={menu.expanded ? null : 'collapsed'}
      data-toggle='collapse'
      data-parent='#sidebarnav'>
      <i className={"glyphicon glyphicon-" + menu.iconClassName}></i>
      <span className='title'>{menu.name}</span>
    </a>
  )
}
