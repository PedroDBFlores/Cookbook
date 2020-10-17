import React, {useEffect, useState} from "react"
import {BrowserRouter, Switch, Route} from "react-router-dom"
import Layout from "./components/layout/layout"
import RecipeTypeListPage from "./features/recipetype/list/list-page"
import CreateRecipeType from "./features/recipetype/create/create"
import RecipeTypeDetails from "./features/recipetype/details/details"
import EditRecipeType from "./features/recipetype/edit/edit"
import createMuiTheme from "@material-ui/core/styles/createMuiTheme"
import {SnackbarProvider} from "notistack"
import ApiHandler, {ApiHandlerContext} from "./services/api-handler"
import Login from "./features/user/login/login"
import createCredentialsService, {AuthContext, AuthInfo} from "./services/credentials-service"
import "fontsource-roboto"
import {ThemeProvider} from "@material-ui/core/styles"
import Logout from "./features/user/logout/logout"
import RecipeSearchPage from "./features/recipe/search/search-page"
import CreateRecipe from "./features/recipe/create/create"
import jwt_decode from "jwt-decode"
import RecipeDetails from "./features/recipe/details/details"

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

const App: React.FC = () => {
    const [authInfo, setAuthInfo] = useState<AuthInfo | undefined>(undefined)
    const updateAuthContext = (newAuthInfo: AuthInfo | undefined): void => {
        setAuthInfo(newAuthInfo)
    }

    useEffect(() => {
        const token = localStorage.getItem("token")
        if (token && !authInfo) {
            const {sub, name, userName} = jwt_decode(token)
            setAuthInfo({userId: Number(sub), name, userName})
        }
    }, [])

    const credentialsService = createCredentialsService()

    return (
        <ApiHandlerContext.Provider value={ApiHandler("http://localhost:9000")}>
            <AuthContext.Provider value={authInfo}>
                <SnackbarProvider maxSnack={4}>
                    <ThemeProvider theme={theme}>
                        <BrowserRouter>
                            <Layout>
                                <Switch>
                                    <Route exact path="/login">
                                        <Login loginFn={credentialsService.login} onUpdateAuth={updateAuthContext}/>
                                    </Route>
                                    <Route exact path="/logout">
                                        <Logout onLogout={credentialsService.logout}
                                                onUpdateAuth={updateAuthContext}/>
                                    </Route>
                                    <Route exact path="/recipetype" render={() => <RecipeTypeListPage/>}/>
                                    <Route exact path="/recipetype/new" render={() => <CreateRecipeType/>}/>
                                    <Route exact path="/recipetype/:id/details" render={(x) => <RecipeTypeDetails id={Number(x.match.params.id)}/>}/>
                                    <Route exact path="/recipetype/:id/edit"
                                           render={(x) => <EditRecipeType id={Number(x.match.params.id)}/>}/>

                                    <Route exact path="/recipe" render={() => <RecipeSearchPage/>}/>
                                    <Route exact path="/recipe/new" render={() => <CreateRecipe/>}/>
                                    <Route exact path="/recipe/:id/details" render={(x) => <RecipeDetails id={Number(x.match.params.id)}/>}/>
                                </Switch>
                            </Layout>
                        </BrowserRouter>
                    </ThemeProvider>
                </SnackbarProvider>
            </AuthContext.Provider>
        </ApiHandlerContext.Provider>
    )
}

export default App
