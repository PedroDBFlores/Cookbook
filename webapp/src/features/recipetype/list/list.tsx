import React from "react"
import {RecipeType} from "../../../model"
import PropTypes from "prop-types"
import {useHistory} from "react-router-dom"
import TableBody from "@material-ui/core/TableBody"
import TableCell from "@material-ui/core/TableCell"
import TableHead from "@material-ui/core/TableHead"
import TableRow from "@material-ui/core/TableRow"
import Delete from "@material-ui/icons/Delete"
import Edit from "@material-ui/icons/Edit"
import Visibility from "@material-ui/icons/Visibility"
import Paper from "@material-ui/core/Paper"
import TableContainer from "@material-ui/core/TableContainer"
import Table from "@material-ui/core/Table"
import ButtonGroup from "@material-ui/core/ButtonGroup"
import Button from "@material-ui/core/Button"
import makeStyles from "@material-ui/core/styles/makeStyles"
import {Theme} from "@material-ui/core/styles/createMuiTheme"
import createStyles from "@material-ui/core/styles/createStyles"

interface RecipeTypeListProps {
    recipeTypes: Array<RecipeType>
    onDelete: (id: number) => void
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        paper: {
            padding: theme.spacing(2),
            color: theme.palette.text.primary,
        },
    }),
)

const RecipeTypeList: React.FC<RecipeTypeListProps> = ({recipeTypes, onDelete}) => {
    const history = useHistory()
    const classes = useStyles()

    const navigateToDetails = (id: number): void => history.push(`/recipetype/${id}`)
    const navigateToEdit = (id: number): void => history.push(`/recipetype/${id}/edit`)

    return !recipeTypes?.length ?
        <Paper className={classes.paper}>
            No recipe types.
        </Paper>
        :
        <TableContainer component={Paper}>
            <Table stickyHeader>
                <TableHead>
                    <TableRow>
                        <TableCell>Id</TableCell>
                        <TableCell>Name</TableCell>
                        <TableCell align="center">Actions</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {recipeTypes.map(({id, name}) =>
                        <TableRow key={`recipeType-${id}`}>
                            <TableCell>{id}</TableCell>
                            <TableCell>{name}</TableCell>
                            <TableCell align="center">
                                <ButtonGroup>
                                    <Button aria-label={`Recipe type details for id ${id}`}
                                            onClick={() => navigateToDetails(id)}>
                                        <Visibility/>
                                    </Button>
                                    <Button aria-label={`Edit Recipe type with id ${id}`}
                                            onClick={() => navigateToEdit(id)}>
                                        <Edit/>
                                    </Button>
                                    <Button aria-label={`Delete Recipe type with id ${id}`}
                                            onClick={() => onDelete(id)}>
                                        <Delete/>
                                    </Button>
                                </ButtonGroup>
                            </TableCell>
                        </TableRow>
                    )}
                </TableBody>
            </Table>
        </TableContainer>
}
export default RecipeTypeList

RecipeTypeList.propTypes = {
    recipeTypes: PropTypes.array.isRequired,
    onDelete: PropTypes.func.isRequired
}