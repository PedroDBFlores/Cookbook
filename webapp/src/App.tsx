import React from "react"
import "./App.css"
import { BrowserRouter, Switch, Route } from "react-router-dom"
import Layout from "./components/layout/layout"
import RecipeTypeListPage from "./features/recipe-type/list/list-page"
import 'bootstrap/dist/css/bootstrap.min.css'

const App: React.FC<{}> = () => (
    <BrowserRouter>
        <Layout>
            <Switch>
                {<Route exact path="/users">
                    <RecipeTypeListPage />
                </Route>}
            </Switch>
        </Layout>
    </BrowserRouter>
)

export default App