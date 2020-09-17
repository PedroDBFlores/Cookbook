import React, {useState} from "react"
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
import {ThemeProvider} from "@material-ui/core/styles"
import Logout from "./features/user/logout/logout"
import AuthContext, {AuthInfo} from "./contexts/auth-context"
import RecipeSearchPage from "./features/recipe/search/search-page"
import createRecipeService from "./services/recipe-service"

const theme = createMuiTheme({
    palette: {
        primary: {
            light: "#757ce8",
            main: "#3f50b5",
            dark: "#002884",
            contrastText: "#fff",
        },
        secondary: {
            light: "#ff7961",
            main: "#f44336",
            dark: "#ba000d",
            contrastText: "#000",
        },
    },
})

const App: React.FC<unknown> = () => {
    const [auth, setAuth] = useState<AuthInfo>({
        isLoggedIn: false
    })
    const updateAuthContext = (update: AuthInfo): void => setAuth({...auth, ...update})

    const recipeTypeService = createRecipeTypeService(ApiHandler("http://localhost:9000"))
    const recipeService = createRecipeService(ApiHandler("http://localhost:9000"))
    const credentialsService = createCredentialsService()

    return (
        <ThemeProvider theme={theme}>
            <SnackbarProvider maxSnack={4}>
                <AuthContext.Provider value={auth}>
                    <BrowserRouter>
                        <Layout>
                            <Switch>
                                <Route exact path="/recipetype"
                                       render={() => <RecipeTypeListPage onGetAll={recipeTypeService.getAll}
                                                                         onDelete={recipeTypeService.delete}/>}/>
                                <Route exact path="/recipetype/new"
                                       render={() => <CreateRecipeType onCreate={recipeTypeService.create}/>}/>
                                <Route path="/recipetype/:id"
                                       render={(x) => <RecipeTypeDetails id={Number(x.match.params.id)}
                                                                         onFind={recipeTypeService.find}
                                                                         onDelete={recipeTypeService.delete}/>}/>
                                <Route path="/recipetype/:id/edit"
                                       render={(x) => <EditRecipeType id={Number(x.match.params.id)}
                                                                      onFind={recipeTypeService.find}
                                                                      onUpdate={recipeTypeService.update}/>
                                       }/>

                                <Route path="/recipe" render={() => <RecipeSearchPage
                                    searchFn={recipeService.search}
                                    getAllRecipeTypesFn={recipeTypeService.getAll}/>}
                                />

                                <Route path="/login">
                                    <Login loginFn={credentialsService.login} onUpdateAuth={updateAuthContext}/>
                                </Route>
                                <Route path="/logout">
                                    <Logout onLogout={credentialsService.logout}
                                            onUpdateAuth={updateAuthContext}/>
                                </Route>
                            </Switch>
                        </Layout>
                    </BrowserRouter>
                </AuthContext.Provider>
            </SnackbarProvider>
        </ThemeProvider>
    )
}

export default App
