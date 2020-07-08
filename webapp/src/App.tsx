import React from "react"
import "./App.css"
import { BrowserRouter, Switch, Route } from "react-router-dom"
import Layout from "./components/layout/layout"
import RecipeTypeListPage from "./features/recipetype/list/list-page"
import "bootstrap/dist/css/bootstrap.min.css"
import RecipeTypeList from "./features/recipetype/list/list"

const App: React.FC<unknown> = () => (
    <BrowserRouter>
        <Layout>
            <Switch>
                {<Route exact path="/users">
                    <RecipeTypeList recipeTypes={[{
                        id:1, name:"XPTO"
                    }]} />
                </Route>}
            </Switch>
        </Layout>
    </BrowserRouter>
)

export default App