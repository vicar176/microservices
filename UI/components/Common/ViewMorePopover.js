import React from 'react';
import { Scrollbars } from 'react-custom-scrollbars';
import { OverlayTrigger, Popover } from 'react-bootstrap';

export default function ViewMorePopover ({
  id,
  title,
  keysList,
  listSize,
  trimLength = 40,
  placement = 'top',
  scrollItems = 10
}) {
  function renderList (keys) {
    const totalkeys = keys.length - 1;
    const renderKeys = [];
    keys.forEach((key, i) => {
      const divider = i !== totalkeys ? ',' : '';
      renderKeys.push(<span key={Math.random()}>{key+divider} </span>);
    });
    return renderKeys;
  }

  function renderTooltip () {
    const keyLength = keysList.length;
    if (keyLength > listSize) {
      const keyList = <p className="text-upper popover-indicator">{renderList(keysList)}</p>;
      const popover = (
        <Popover id={id} title={title}>
          { keyLength > scrollItems ?
            <Scrollbars style={{ width: 250, height: '12.5em' }}>
              {keyList}
            </Scrollbars>
            : keyList }
        </Popover>
      );
      return (
        <OverlayTrigger trigger="click" rootClose placement={placement} overlay={popover}>
          <a href="#" className="text-more" onClick={e => e.preventDefault()}>more...</a>
        </OverlayTrigger>
      );
    }
  }

  function trimList () {
    let trimmedList = keysList.slice(0, listSize).join(', ');
    if (trimmedList.length > trimLength && keysList.length > listSize) {
      trimmedList = trimmedList.substring(0, trimLength) + '...';
    }
    return trimmedList;
  }

  return (
    <div className="text-upper">
      {trimList()} {renderTooltip()}
    </div>
  );
}
