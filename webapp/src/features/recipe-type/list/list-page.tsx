import React, { useState, useEffect } from "react"
import RecipeTypeList from "./list"
import { createRecipeTypeService } from "../../../services/recipe-type-service"
import { RecipeType } from "../../../dto"

const RecipeTypeListPage: React.FC<unknown> = () => {
    const [recipeTypes, setRecipeTypes] = useState<RecipeType[]>([])

    useEffect(() => {
        createRecipeTypeService().getAllRecipeTypes().then(setRecipeTypes)
    }, [])

    return <>
        <span>Recipe type List</span>
        <RecipeTypeList recipeTypes={recipeTypes} />
        <hr />
    </>
}

export default RecipeTypeListPage