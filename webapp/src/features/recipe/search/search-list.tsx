import React, {useState} from "react"
import PropTypes from "prop-types"
import {RecipeDetails, SearchResult} from "../../../model"
import Paper from "@material-ui/core/Paper"
import Table from "@material-ui/core/Table"
import TableHead from "@material-ui/core/TableHead"
import TableRow from "@material-ui/core/TableRow"
import TableCell from "@material-ui/core/TableCell"
import TableContainer from "@material-ui/core/TableContainer"
import TableBody from "@material-ui/core/TableBody"
import ButtonGroup from "@material-ui/core/ButtonGroup"
import Button from "@material-ui/core/Button"
import Visibility from "@material-ui/icons/Visibility"
import Edit from "@material-ui/icons/Edit"
import Delete from "@material-ui/icons/Delete"
import {useHistory} from "react-router-dom"
import TablePagination from "@material-ui/core/TablePagination"
import TableFooter from "@material-ui/core/TableFooter"
import TablePaginationActions from "../../../components/table-pagination-actions/table-pagination-actions"

interface RecipeSearchListProps {
    searchResult: SearchResult<RecipeDetails>
    onDelete: (id: number) => void
    onPageChange: (page: number) => void
}

const RecipeSearchList: React.FC<RecipeSearchListProps> = ({
                                                               searchResult, onDelete, onPageChange
                                                           }) => {
    const [page, setPage] = useState<number>(0)
    const history = useHistory()

    const navigateToDetails = (id: number): void => history.push(`/recipe/${id}`)
    const navigateToEdit = (id: number): void => history.push(`/recipe/${id}/edit`)

    const handleOnChangePage = (event: React.MouseEvent<HTMLButtonElement> | null, page: number) => {
        setPage(page)
        onPageChange(page)
    }

    return <TableContainer component={Paper}>
        <Table stickyHeader>
            <TableHead>
                <TableRow>
                    <TableCell>Id</TableCell>
                    <TableCell>Name</TableCell>
                    <TableCell>Recipe type</TableCell>
                    <TableCell>User</TableCell>
                    <TableCell align="center">Actions</TableCell>
                </TableRow>
            </TableHead>
            <TableBody>
                {
                    !searchResult?.count ?
                        <TableRow>
                            <TableCell colSpan={5}>
                                No matching recipes
                            </TableCell>
                        </TableRow> :
                        searchResult.results.map(({id, name, recipeTypeName, userName}) =>
                            <TableRow key={`recipe-${id}`}>
                                <TableCell>{id}</TableCell>
                                <TableCell>{name}</TableCell>
                                <TableCell>{recipeTypeName}</TableCell>
                                <TableCell>{userName}</TableCell>
                                <TableCell align="center">
                                    <ButtonGroup>
                                        <Button aria-label={`Recipe details for id ${id}`}
                                                onClick={() => navigateToDetails(id)}>
                                            <Visibility/>
                                        </Button>
                                        <Button aria-label={`Edit Recipe with id ${id}`}
                                                onClick={() => navigateToEdit(id)}>
                                            <Edit/>
                                        </Button>
                                        <Button aria-label={`Delete Recipe with id ${id}`}
                                                onClick={() => onDelete(id)}>
                                            <Delete/>
                                        </Button>
                                    </ButtonGroup>
                                </TableCell>
                            </TableRow>)
                }
            </TableBody>
            <TableFooter>
                <TableRow>
                    <TablePagination
                        count={searchResult.count}
                        rowsPerPage={10}
                        page={page}
                        colSpan={5}
                        rowsPerPageOptions={[10, 20, 50]}
                        SelectProps={{
                            inputProps: {"aria-label": "rows per page"},
                            native: true,
                        }}
                        onChangePage={handleOnChangePage}
                        ActionsComponent={TablePaginationActions}
                    />
                </TableRow>
            </TableFooter>
        </Table>
    </TableContainer>
}
RecipeSearchList.propTypes = {
    searchResult: PropTypes.any.isRequired,
    onDelete: PropTypes.func.isRequired,
    onPageChange: PropTypes.func.isRequired
}

export default RecipeSearchList