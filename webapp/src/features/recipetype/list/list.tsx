import React from "react"
import PropTypes from "prop-types"
import {useHistory} from "react-router-dom"
import {
    TableBody,
    TableCell,
    TableHead,
    TableRow,
    TableContainer,
    Table,
    Paper,
    ButtonGroup,
    Button
} from "@material-ui/core"
import {Delete, Edit, Visibility} from "@material-ui/icons"
import {makeStyles, createStyles, Theme} from "@material-ui/core/styles"
import {RecipeType} from "../../../services/recipe-type-service"

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

    const navigateToDetails = (id: number): void => history.push(`/recipetype/${id}/details`)
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
RecipeTypeList.propTypes = {
    recipeTypes: PropTypes.array.isRequired,
    onDelete: PropTypes.func.isRequired
}

export default RecipeTypeList