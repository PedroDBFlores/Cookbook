import React from "react"
import "./App.css"
import {BrowserRouter, Switch, Route} from "react-router-dom"
import Layout from "./components/layout/layout"
import RecipeTypeListPage from "./features/recipetype/list/list-page"
import BasicModalDialog from "./components/modal/basic-modal-dialog"
import CreateRecipeType from "./features/recipetype/create/create"
import RecipeTypeDetails from "./features/recipetype/details/details"
import EditRecipeType from "./features/recipetype/edit/edit"
import {ThemeProvider, createMuiTheme} from "@material-ui/core"
import {green, purple} from "@material-ui/core/colors"
import {SnackbarProvider} from "notistack";

const theme = createMuiTheme({
    palette: {
        primary: {
            main: purple[500],
        },
        secondary: {
            main: green[500],
        },
    },
})

const App: React.FC<unknown> = () => (
    <ThemeProvider theme={theme}>
        <SnackbarProvider maxSnack={4}>
            <BrowserRouter>
                <Layout>
                    <Switch>
                        <Route exact path="/recipetype" render={() => <RecipeTypeListPage/>}/>
                        <Route exact path="/recipetype/new" render={() => <CreateRecipeType/>}/>
                        <Route path="/recipetype/:id"
                               render={(x) => <RecipeTypeDetails id={Number(x.match.params.id)}/>}/>
                        <Route path="/recipetype/:id/edit"
                               render={(x) => <EditRecipeType id={Number(x.match.params.id)}/>}/>
                        <Route path="/test">
                            <BasicModalDialog title={"a"} content={"B"} dismiss={{
                                text: "OK", onDismiss: () => {
                                }
                            }} onClose={() => {
                            }}/>
                        </Route>
                    </Switch>
                </Layout>
            </BrowserRouter>
        </SnackbarProvider>
    </ThemeProvider>
)

export default App
