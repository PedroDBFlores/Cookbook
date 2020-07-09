import React, { useState, useEffect } from "react"
import RecipeTypeList from "./list"
import { getAllRecipeTypes } from "../../../services/recipe-type-service"
import { ResponseError } from "../../../dto/response-error"
import { RecipeType } from "../../../dto"

const RecipeTypeListPage: React.FC<unknown> = () => {
    const [data, setData] = useState<RecipeType[]>()
    const [error, setError] = useState<ResponseError>()

    useEffect(() => {
        getAllRecipeTypes()
            .then(setData)
            .catch(setError)
    }, [])

    if (error) return <div>Error: {(error as ResponseError).message}</div>
    if (!data) return <div>Loading...</div>

    return <>
        <h1>Recipe type list</h1>
        {data ? <RecipeTypeList recipeTypes={data} /> : ""}
    </>
}

export default RecipeTypeListPage