import React from "react"
import {BrowserRouter, Route, Switch} from "react-router-dom"
import Layout from "components/layout/layout"
import RecipeTypeListPage from "features/recipetype/list/list-page"
import CreateRecipeType from "features/recipetype/create/create"
import RecipeTypeDetails from "features/recipetype/details/details"
import EditRecipeType from "features/recipetype/edit/edit"
import RecipeSearchPage from "features/recipe/search/search-page"
import CreateRecipe from "features/recipe/create/create"
import RecipeDetails from "features/recipe/details/details"
import EditRecipe from "features/recipe/edit/edit"
import {ChakraProvider} from "@chakra-ui/react"
import {WithModal} from "components/modal/modal-context"

const App: React.FC = () => (
    <ChakraProvider>
        <WithModal>
            <BrowserRouter>
                <Layout>
                    <Switch>
                        <Route exact path="/recipetype" render={() => <RecipeTypeListPage/>}/>
                        <Route exact path="/recipetype/new" render={() => <CreateRecipeType/>}/>
                        <Route exact path="/recipetype/:id/details"
                               render={x => <RecipeTypeDetails id={Number(x.match.params.id)}/>}/>
                        <Route exact path="/recipetype/:id/edit"
                               render={x => <EditRecipeType id={Number(x.match.params.id)}/>}/>

                        <Route exact path="/recipe" render={() => <RecipeSearchPage/>}/>
                        <Route exact path="/recipe/new" render={() => <CreateRecipe/>}/>
                        <Route exact path="/recipe/:id/details"
                               render={x => <RecipeDetails id={Number(x.match.params.id)}/>}/>
                        <Route exact path="/recipe/:id/edit"
                               render={x => <EditRecipe id={Number(x.match.params.id)}/>}/>
                    </Switch>
                </Layout>
            </BrowserRouter>
        </WithModal>
    </ChakraProvider>
)

export default App
