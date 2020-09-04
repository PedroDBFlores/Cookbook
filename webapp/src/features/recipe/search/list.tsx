import React from "react"
import PropTypes from "prop-types"
import makeStyles from "@material-ui/core/styles/makeStyles"
import {Theme} from "@material-ui/core/styles/createMuiTheme"
import createStyles from "@material-ui/core/styles/createStyles"
import {Recipe} from "../../../model"
import Paper from "@material-ui/core/Paper";

interface RecipeListProps {
    recipes: Array<Recipe>
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        paper: {
            padding: theme.spacing(2),
            color: theme.palette.text.primary,
        },
    }),
)

const RecipeList: React.FC<RecipeListProps> = ({recipes}) => {
    const classes = useStyles()

    return !recipes?.length ?
        <Paper className={classes.paper}>
            No matching recipes
        </Paper> : <></>
}
RecipeList.propTypes = {
    recipes: PropTypes.array.isRequired
}

export default RecipeList