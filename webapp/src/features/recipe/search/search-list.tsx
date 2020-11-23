import React, {useState} from "react"
import PropTypes from "prop-types"
import {SearchResult} from "../../../model"
import {
    Paper,
    Table,
    TableHead,
    TableRow,
    TableCell,
    TableContainer,
    TableBody,
    TablePagination,
    TableFooter,
    ButtonGroup,
    Button
} from "@material-ui/core"
import {Visibility, Edit, Delete} from "@material-ui/icons"
import {useHistory} from "react-router-dom"
import {RecipeDetails} from "../../../services/recipe-service"

interface RecipeSearchListProps {
    searchResult: SearchResult<RecipeDetails>
    onDelete: (id: number) => void
    onNumberOfRowsChange: (rowsPerPage: number) => void
    onPageChange: (page: number) => void
}

const RecipeSearchList: React.FC<RecipeSearchListProps> = ({
                                                               searchResult,
                                                               onDelete,
                                                               onNumberOfRowsChange,
                                                               onPageChange
                                                           }) => {
    const [rowsPerPage, setRowsPerPage] = useState<number>(10)
    const [page, setPage] = useState<number>(0)
    const history = useHistory()

    const navigateToDetails = (id: number): void => history.push(`/recipe/${id}/details`)
    const navigateToEdit = (id: number): void => history.push(`/recipe/${id}/edit`)

    const handleOnChangePage = (event: React.MouseEvent<HTMLButtonElement> | null, page: number) => {
        setPage(page)
        onPageChange(page)
    }

    const handleOnRowsPerPageChange = (rowsPerPage: number) => {
        setRowsPerPage(rowsPerPage)
        onNumberOfRowsChange(rowsPerPage)
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
                        rowsPerPage={rowsPerPage}
                        page={page}
                        colSpan={5}
                        rowsPerPageOptions={[10, 20, 50]}
                        SelectProps={{
                            inputProps: {"aria-label": "rows per page"},
                            native: true,
                        }}
                        onPageChange={handleOnChangePage}
                        onRowsPerPageChange={event => handleOnRowsPerPageChange(Number(event.target.value))}
                    />
                </TableRow>
            </TableFooter>
        </Table>
    </TableContainer>
}
RecipeSearchList.propTypes = {
    searchResult: PropTypes.shape({
        count: PropTypes.number.isRequired,
        numberOfPages: PropTypes.number.isRequired,
        results: PropTypes.array.isRequired
    }).isRequired,
    onNumberOfRowsChange: PropTypes.func.isRequired,
    onPageChange: PropTypes.func.isRequired,
    onDelete: PropTypes.func.isRequired
}

export default RecipeSearchList