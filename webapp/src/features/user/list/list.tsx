/* eslint-disable react/jsx-key */
import React, { useMemo } from "react"
import {
    useTable,
    useGroupBy,
    useFilters,
    useSortBy,
    useExpanded,
    usePagination
} from 'react-table'
import Table from "react-bootstrap/Table"

const UserList: React.FC<{}> = () => {
    const columns = useMemo(() => [
        {
            Header : "Name",
            useAccessor: "name"
        },
        {
            Header : "First Name",
            useAccessor: "firstName"
        },
        {
            Header : "Last Name",
            useAccessor: "lastName"
        },
        {
            Header : "User Name",
            useAccessor: "userName"
        },
        {
            Header : "Email",
            useAccessor: "email"
        },
    ], [])
    const data = React.useMemo( () => [],[])
    const {
        getTableProps,
        getTableBodyProps,
        headerGroups,
        rows,
        prepareRow,
      } = useTable({ columns, data })

      return (
        <Table striped bordered {...getTableProps()}>
          <thead>
            {headerGroups.map(headerGroup => (
              <tr {...headerGroup.getHeaderGroupProps()}>
                {headerGroup.headers.map(column => (
                  <th {...column.getHeaderProps()}>{column.render('Header')}</th>
                ))}
              </tr>
            ))}
          </thead>
          <tbody {...getTableBodyProps()}>
            {rows.map(row => {
              prepareRow(row)
              return (
                <tr {...row.getRowProps()}>
                  {row.cells.map(cell => {
                    return <td {...cell.getCellProps()}>{cell.render('Cell')}</td>
                  })}
                </tr>
              )
            })}
          </tbody>
        </Table>
      )
}

export default UserList