import axios from 'axios'
import { RecipeType } from "../dto"
import handleError from '../utils/error-handling'

export interface RecipeTypeService {
    get: (id: number) => Promise<RecipeType>
    getAll: () => Promise<Array<RecipeType>>
}

const get = async (id: number): Promise<RecipeType> => {
    try {
        const response = await axios.get<RecipeType>(`/api/recipetype/${id}`)
        return response.data
    } catch (err) {
        //throw new Error(err.response.data)
        throw handleError(err)
    }
}

const getAll = async (): Promise<Array<RecipeType>> => {
    try {
        const response = await axios.get<Array<RecipeType>>("/api/recipetype")
        return response.data
    } catch (err) {
        throw new Error(err.response.data)
    }
}

export const createRecipeTypeService = (): RecipeTypeService => {
    return {
        get: get,
        getAll: getAll
    }
}