import React from "react"
import PropTypes from "prop-types"
import makeStyles from "@material-ui/core/styles/makeStyles"
import {Theme} from "@material-ui/core/styles/createMuiTheme"
import createStyles from "@material-ui/core/styles/createStyles"
import {RecipeDetails} from "../../../model"
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
import { useHistory } from "react-router-dom"

interface RecipeSearchListProps {
    recipes: Array<RecipeDetails>
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

const RecipeSearchList: React.FC<RecipeSearchListProps> = ({recipes, onDelete}) => {
    const history = useHistory()
    const classes = useStyles()

    const navigateToDetails = (id: number): void => history.push(`/recipe/${id}`)
    const navigateToEdit = (id: number): void => history.push(`/recipe/${id}/edit`)

    return !recipes?.length ?
        <Paper className={classes.paper}>
            No matching recipes
        </Paper> :
        <TableContainer component={Paper}>
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
                    {recipes.map(({id, name, recipeTypeName, userName}) =>
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
            </Table>
        </TableContainer>
}
RecipeSearchList.propTypes = {
    recipes: PropTypes.array.isRequired,
    onDelete: PropTypes.func.isRequired
}

export default RecipeSearchList