import React from "react"
import RecipeTypeList from "./list"
import {deleteRecipeType, getAllRecipeTypes} from "../../../services/recipe-type-service"
import {ResponseError} from "../../../dto/response-error"
import {RecipeType} from "../../../dto"
import {Async} from "react-async"

const RecipeTypeListPage: React.FC<unknown> = () => {

    const onDelete = (id: number) => deleteRecipeType(id)

    return <>
        <h1>Recipe type list page</h1>
        <Async promiseFn={getAllRecipeTypes}>
            <Async.Loading>Loading...</Async.Loading>
            <Async.Rejected>
                {(error: ResponseError) =>
                    <span>Error: {error.message}</span>
                }
            </Async.Rejected>
            <Async.Fulfilled>
                {(data: Array<RecipeType>) =>
                    <RecipeTypeList recipeTypes={data} onDelete={(id => onDelete(id))}/>
                }
            </Async.Fulfilled>
        </Async>
    </>
}

export default RecipeTypeListPage
