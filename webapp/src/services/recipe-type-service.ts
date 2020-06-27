import axios from 'axios'
import { RecipeType } from "../dto"
import handleApiError from '../utils/error-handling'

export interface RecipeTypeService {
    get: (id: number) => Promise<RecipeType>
    getAll: () => Promise<Array<RecipeType>>
    create: (recipeType: Omit<RecipeType, "id">) => Promise<number>
    update: (recipeType: RecipeType) => Promise<void>
    delete: (id: number) => Promise<void>
}

const getRecipeType = async (id: number): Promise<RecipeType> => {
     try {
        const response = await axios.get<RecipeType>(`/api/recipetype/${id}`)
        return response.data
    } catch (err) {
        //throw new Error(err.response.data)
        throw handleApiError(err)
    }
}

const getAllRecipeTypes = async (): Promise<Array<RecipeType>> => {
    try {
        const response = await axios.get<Array<RecipeType>>("/api/recipetype")
        return response.data
    } catch (err) {
        throw new Error(err.response.data)
    }
}

const createRecipeType = async (recipeType: Omit<RecipeType, "id">): Promise<number> => {
    return 0
}

const updateRecipeType = async (recipeType: RecipeType): Promise<void> => {
    return
}

const deleteRecipeType = async(id:number) : Promise<void> => {
    return
}


export const createRecipeTypeService = (): RecipeTypeService => {
    return {
        get: getRecipeType,
        getAll: getAllRecipeTypes,
        create: createRecipeType,
        update: updateRecipeType,
        delete: deleteRecipeType
    }
}