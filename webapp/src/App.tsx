import React from "react"
import "./App.css"
import { BrowserRouter, Switch, Route } from "react-router-dom"
import Layout from "./components/layout/layout"
import UserListPage from "./features/user/list/list-page"
import 'bootstrap/dist/css/bootstrap.min.css';

const App: React.FC<{}> = () => (
    <BrowserRouter>
        <Layout>
            <Switch>
                {<Route exact path="/users">
                    <UserListPage />
                </Route>}
            </Switch>
        </Layout>
    </BrowserRouter>
)

export default App