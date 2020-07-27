import React from "react"
import "./App.css"
import {BrowserRouter, Switch, Route} from "react-router-dom"
import Layout from "./components/layout/layout"
import "bootstrap/dist/css/bootstrap.min.css"
import RecipeTypeListPage from "./features/recipetype/list/list-page"
import BasicModalDialog from "./components/modal/basic-modal-dialog"
import CreateRecipeType from "./features/recipetype/create/create"

const App: React.FC<unknown> = () => (
    <BrowserRouter>
        <Layout>
            <Switch>
                <Route path="/recipetype">
                    <RecipeTypeListPage/>
                </Route>
                <Route exact path="/recipetype/new">
                    <CreateRecipeType />
                </Route>
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
)

export default App
