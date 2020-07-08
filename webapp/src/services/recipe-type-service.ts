import axios from "axios"
import { RecipeType, CreateResult } from "../dto"
import handleApiError from "../utils/error-handling"

export interface RecipeTypeService {
    find: (id: number) => Promise<RecipeType>
    getAll: () => Promise<Array<RecipeType>>
    create: (recipeType: Omit<RecipeType, "id">) => Promise<CreateResult>
    update: (recipeType: RecipeType) => Promise<void>
    delete: (id: number) => Promise<void>
}

const find = async (id: number): Promise<RecipeType> => {
    try {
        const response = await axios.get<RecipeType>(`/api/recipetype/${id}`)
        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}

const getAll = async (): Promise<Array<RecipeType>> => {
    try {
        const response = await axios.get<Array<RecipeType>>("/api/recipetype")
        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}

const create = async (recipeType: Omit<RecipeType, "id">): Promise<CreateResult> => {
    try {
        const response = await axios.post<CreateResult>("/api/recipetype", recipeType)
        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}

const update = async (recipeType: RecipeType): Promise<void> => {
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

function createRecipeTypeService() : RecipeTypeService{
    return{
        find: find,
        getAll: getAll,
        create: create,
        update: update,
        delete: deleteRecipeType
    }
}

export default createRecipeTypeService
