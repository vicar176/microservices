// By default, the filtered fields are text type. set a type of date for to show the date picker on the field.

import React from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';

export default function FilterField ({ col, currFilter, onFieldBlur, onCleanFilterField, onSetFilterValue, onSetFilterDateValue }) {
  return (
    <th className="grid-row" style={col.style}>
      <label htmlFor={col.key}>
        { col.type === "date" ?
        <DatePicker
          showYearDropdown
          id={`${col.key}-field`}
          placeholderText={currFilter}
          className="filter-field"
          onChange={e => onSetFilterDateValue(e, col.key)} />
        :
        <input
          type={ col.type === "number" ? "number" : "text"}
          id={`${col.key}-field`}
          defaultValue={currFilter}
          data-filter={col.key}
          className="filter-field"
          onBlur={onFieldBlur}
          onKeyUp={e => onSetFilterValue(e, col.key)} />
        }
        { currFilter &&
          <i
             className={'glyphicon ' + (currFilter ? 'filtered' : '')}
             onClick={() => onCleanFilterField(col.key, col.type)} />
        }
      </label>
    </th>
  )
}
