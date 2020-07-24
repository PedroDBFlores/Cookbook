import React from "react"
import "./App.css"
import { BrowserRouter, Switch, Route } from "react-router-dom"
import Layout from "./components/layout/layout"
import "bootstrap/dist/css/bootstrap.min.css"
import RecipeTypeListPage from "./features/recipetype/list/list-page"

const App: React.FC<unknown> = () => (
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