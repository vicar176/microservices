import React from 'react';
// components
import TopHeader from 'TopHeader';
import Navigation from 'Navigation';
// resources
import dataNav from 'json!data/sidebar.json';

export default function Sidebar ({ location }) {
  const path = location.pathname.split('/').pop();
  return (
    <div className="inner">
      <TopHeader />
      <Navigation path={path} navigation={dataNav.data} />
    </div>
  )
}
