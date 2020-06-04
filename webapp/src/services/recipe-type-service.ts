import axios from 'axios'
import { RecipeType } from "../dto"

export interface RecipeTypeService {
    getAllRecipeTypes: () => Promise<Array<RecipeType>>
}

export const createRecipeTypeService = (): RecipeTypeService => {
    return {
        getAllRecipeTypes: getAllRecipeTypes
    }
}

const getAllRecipeTypes = async (): Promise<Array<RecipeType>> => {
    const response = await axios.get<Array<RecipeType>>("/api/recipetype")
    return response.data
}