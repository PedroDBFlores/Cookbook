/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable react/jsx-key */
import React from "react"
import {RecipeType} from "../../../dto"
import PropTypes from "prop-types"
import MaUTable from "@material-ui/core/Table"
import TableBody from "@material-ui/core/TableBody"
import TableCell from "@material-ui/core/TableCell"
import TableHead from "@material-ui/core/TableHead"
import TableRow from "@material-ui/core/TableRow"
import {useHistory} from "react-router-dom"
import {useTable} from "react-table"
import {ButtonGroup, Button} from "@material-ui/core"
import {Delete, Edit, Visibility} from "@material-ui/icons"

export interface RecipeTypeListProps {
    recipeTypes: Array<RecipeType>
    onDelete: (id: number) => void
}

const RecipeTypeList: React.FC<RecipeTypeListProps> = ({recipeTypes, onDelete}) => {
    const history = useHistory()

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
                Cell: ({value: id}: any) => (
                    <ButtonGroup>
                        <Button aria-label={`Recipe type details for id ${id}`} onClick={() => navigateToDetails(id)}>
                            <Visibility/>
                        </Button>
                        <Button aria-label={`Edit Recipe type with id ${id}`} onClick={() => navigateToEdit(id)}>
                            <Edit/>
                        </Button>
                        <Button aria-label={`Delete Recipe type with id ${id}`} onClick={() => onDelete(id)}>
                            <Delete/>
                        </Button>
                    </ButtonGroup>
                )
            }
        ], [])
    const {
        getTableProps, getTableBodyProps, headerGroups, rows, prepareRow
    } = useTable({columns, data})

    const navigateToDetails = (id: number): void => history.push(`/recipetype/${id}`)
    const navigateToEdit = (id: number): void => history.push(`/recipetype/${id}/edit`)

    const contentToRender = !recipeTypes?.length ? "No recipe types." :
        <MaUTable {...getTableProps()}>
            <TableHead>
                {headerGroups.map(headerGroup => (
                    <TableRow {...headerGroup.getHeaderGroupProps()}>
                        {headerGroup.headers.map(column => (
                            <TableCell {...column.getHeaderProps()}>
                                {column.render("Header")}
                            </TableCell>
                        ))}
                    </TableRow>
                ))}
            </TableHead>
            <TableBody>
                {rows.map((row, i) => {
                    prepareRow(row)
                    return (
                        <TableRow {...row.getRowProps()}>
                            {row.cells.map(cell => {
                                return (
                                    <TableCell {...cell.getCellProps()}>
                                        {cell.render("Cell")}
                                    </TableCell>
                                )
                            })}
                        </TableRow>
                    )
                })}
            </TableBody>
        </MaUTable>

    return <>
        {contentToRender}
    </>

}
export default RecipeTypeList

RecipeTypeList.propTypes = {
    recipeTypes: PropTypes.array.isRequired,
    onDelete: PropTypes.func.isRequired
}