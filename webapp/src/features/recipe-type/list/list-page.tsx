import React from "react"
import RecipeTypeList from "./list"
import { createRecipeTypeService } from "../../../services/recipe-type-service"
import { RecipeType } from "../../../dto"
import { useQuery } from "react-query"

const RecipeTypeListPage: React.FC<unknown> = () => {
    const { status, data, error } = useQuery<Array<RecipeType>, string>(
        "recipeTypeList", createRecipeTypeService().getAllRecipeTypes)

    if (status === 'loading') {
        return <span>Loading...</span>
    }

    if (status === 'error') {
        return <span>Error: {error?.message}</span>
    }

    return <>
        <h1>Recipe type List</h1>
        {data ? <RecipeTypeList recipeTypes={data} /> : "No recipe types found"}

    </>
}

export default RecipeTypeListPage