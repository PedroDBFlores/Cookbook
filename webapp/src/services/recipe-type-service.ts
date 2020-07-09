import axios from "axios"
import { RecipeType, CreateResult } from "../dto"
import handleApiError from "../utils/error-handling"

const findRecipeType = async (id: number): Promise<RecipeType> => {
    try {
        const response = await axios.get<RecipeType>(`/api/recipetype/${id}`)
        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}

const getAllRecipeTypes = async (): Promise<Array<RecipeType>> => {
    try {
        const response = await axios.get<Array<RecipeType>>("/api/recipetype")
        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}

const createRecipeType = async (recipeType: Omit<RecipeType, "id">): Promise<CreateResult> => {
    try {
        const response = await axios.post<CreateResult>("/api/recipetype", recipeType)
        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}

const updateRecipeType = async (recipeType: RecipeType): Promise<void> => {
    try {
        await axios.put("/api/recipetype", recipeType)
    } catch (err) {
        throw handleApiError(err)
    }
}

const deleteRecipeType = async (id: number): Promise<void> => {
    try {
        await axios.delete(`/api/recipetype/${id}`)
    } catch (err) {
        throw handleApiError(err)
    }
}

export {
    findRecipeType,
    getAllRecipeTypes,
    createRecipeType,
    updateRecipeType,
    deleteRecipeType
}
