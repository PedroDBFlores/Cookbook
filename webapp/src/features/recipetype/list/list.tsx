/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable react/jsx-key */
import React from "react"
import { RecipeType } from "../../../dto"
import PropTypes from "prop-types"
import Table from "react-bootstrap/Table"
import { useTable } from "react-table"
import Button from "react-bootstrap/Button"
import ButtonGroup from "react-bootstrap/ButtonGroup"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import { faEye, faEdit, faTrash } from "@fortawesome/free-solid-svg-icons"

export interface RecipeTypeListProps {
  recipeTypes: Array<RecipeType>
  onDelete: (id: number) => void
}

const RecipeTypeList: React.FC<RecipeTypeListProps> = ({ recipeTypes, onDelete }) => {
  const data = React.useMemo(() => recipeTypes, [])
  const columns = React.useMemo(
    () => [
      {
        Header: "Id",
        accessor: (r: RecipeType) => r.id
      },
      {
        Header: "Name",
        accessor: (r: RecipeType) => r.name
      },
      {
        Header: "Actions",
        id: "id",
        accessor: (r: RecipeType) => r.id,
        Cell: ({ value: id }: any) => (
          <ButtonGroup>
            <Button aria-label={`Recipe type details for id ${id}`}>
              <FontAwesomeIcon icon={faEye} />
            </Button>
            <Button aria-label={`Edit Recipe type with id ${id}`}>
              <FontAwesomeIcon icon={faEdit} />
            </Button>
            <Button aria-label={`Delete Recipe type with id ${id}`} onClick={() => onDelete(id)}>
              <FontAwesomeIcon icon={faTrash} />
            </Button>
          </ButtonGroup>
        )
      }
    ], [])
  const {
    getTableProps, getTableBodyProps, headerGroups, rows, prepareRow
  } = useTable({ columns, data })

  const contentToRender = !recipeTypes?.length ? "No recipe types." :
    <Table striped bordered hover {...getTableProps}>
      <thead>
        {headerGroups.map(headerGroup => (
          <tr {...headerGroup.getHeaderGroupProps()}>
            {headerGroup.headers.map(column => (
              <th  {...column.getHeaderProps()} >
                {column.render("Header")}
              </th>
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
                return (
                  <td   {...cell.getCellProps()} >
                    {cell.render("Cell")}
                  </td>
                )
              })}
            </tr>
          )
        })}
      </tbody>
    </Table>

  return <>
    {contentToRender}
  </>

}
export default RecipeTypeList

RecipeTypeList.propTypes = {
  recipeTypes: PropTypes.array.isRequired,
  onDelete: PropTypes.func.isRequired
}