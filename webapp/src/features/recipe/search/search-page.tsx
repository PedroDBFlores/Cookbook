import React, {useState} from "react"
import SearchRecipeParameters from "../../../model/search-recipe-parameters"
import {RecipeDetails} from "../../../model"
import RecipeSearchList from "./search-list"
import Grid from "@material-ui/core/Grid"
import Typography from "@material-ui/core/Typography"
import RecipeType from "../../../model/recipe-type"
import Paper from "@material-ui/core/Paper"
import makeStyles from "@material-ui/core/styles/makeStyles";
import {Theme} from "@material-ui/core/styles/createMuiTheme";
import createStyles from "@material-ui/core/styles/createStyles";
import TextField from "@material-ui/core/TextField"

interface RecipeSearchPageProps {
    getAllRecipeTypes: () => Promise<RecipeType>
    searchFn: (parameters: SearchRecipeParameters) => Promise<void>
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        paper: {
            padding: theme.spacing(2),
            color: theme.palette.text.primary,
        },
    }),
)

const RecipeSearchPage: React.FC<unknown> = () => {
    const [recipes, setRecipes] = useState<Array<RecipeDetails>>([])

    const classes = useStyles()

    return <Grid container spacing={3}>
        <Grid item xs={11}>
            <Typography variant="h4">Search recipes</Typography>
        </Grid>
        <Grid item xs={12}>
            <Paper className={classes.paper}>
                <Typography variant="subtitle1">Parameters</Typography>
                <Grid container spacing={3}>
                    <Grid item xs>
                        <TextField id="name" label="Name" variant="outlined" />
                    </Grid>
                    <Grid item xs>
                        <TextField id="description" label="Description" variant="outlined" />
                    </Grid>
                    <Grid item xs>
                        <TextField id="recipetype" label="Recipe type" variant="outlined" />
                    </Grid>
                </Grid>
            </Paper>
        </Grid>
        <Grid item xs={12}>
            <RecipeSearchList recipes={recipes} onDelete={() => {
            }}/>
        </Grid>
    </Grid>
}

export default RecipeSearchPage