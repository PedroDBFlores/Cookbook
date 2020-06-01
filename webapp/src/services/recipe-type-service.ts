import axios from 'axios'
import { RecipeType } from "../dto"

export interface RecipeTypeService {
    getAllRecipeTypes: () => Promise<Array<RecipeType>>
}

export const createRecipeTypeService = (): RecipeTypeService => {
    return {
        getAllRecipeTypes : getAllRecipeTypes
    }
}

const getAllRecipeTypes = (): Promise<Array<RecipeType>> =>
    axios.get("/api/recipetype")