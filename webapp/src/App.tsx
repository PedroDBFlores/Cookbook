import React from "react"
import {BrowserRouter, Switch, Route} from "react-router-dom"
import Layout from "./components/layout/layout"
import RecipeTypeListPage from "./features/recipetype/list/list-page"
import CreateRecipeType from "./features/recipetype/create/create"
import RecipeTypeDetails from "./features/recipetype/details/details"
import EditRecipeType from "./features/recipetype/edit/edit"
import createMuiTheme from "@material-ui/core/styles/createMuiTheme"
import {SnackbarProvider} from "notistack"
import createRecipeTypeService from "./services/recipe-type-service"
import ApiHandler from "./services/api-handler"
import Login from "./features/user/login/login"
import createCredentialsService from "./services/credentials-service"
import "fontsource-roboto"
import red from "@material-ui/core/colors/red"
import grey from "@material-ui/core/colors/grey"
import {ThemeProvider} from "@material-ui/core/styles"

const theme = createMuiTheme({
    palette: {
        primary: {
            main: red[500],
        },
        secondary: {
            main: grey[500],
        },
    },
})

const App: React.FC<unknown> = () => {
    const recipeTypeService = createRecipeTypeService(ApiHandler("http://localhost:9000"))
    const credentialsService = createCredentialsService()

    return <ThemeProvider theme={theme}>
        <SnackbarProvider maxSnack={4}>
            <BrowserRouter>
                <Layout>
                    <Switch>
                        <Route exact path="/recipetype"
                               render={() => <RecipeTypeListPage recipeTypeService={recipeTypeService}/>}/>
                        <Route exact path="/recipetype/new"
                               render={() => <CreateRecipeType recipeTypeService={recipeTypeService}/>}/>
                        <Route path="/recipetype/:id"
                               render={(x) => <RecipeTypeDetails recipeTypeService={recipeTypeService}
                                                                 id={Number(x.match.params.id)}/>}/>
                        <Route path="/recipetype/:id/edit"
                               render={(x) => <EditRecipeType recipeTypeService={recipeTypeService}
                                                              id={Number(x.match.params.id)}/>}/>
                        <Route path="/login">
                            <Login credentialsService={credentialsService}/>
                        </Route>
                    </Switch>
                </Layout>
            </BrowserRouter>
        </SnackbarProvider>
    </ThemeProvider>
}

export default App
