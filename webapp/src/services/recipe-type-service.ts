import {AxiosInstance} from "axios"
import {CreateResult} from "../model"
import handleApiError from "../utils/error-handling"

export interface RecipeTypeService {
    find(id: number): Promise<RecipeType>

    getAll(): Promise<Array<RecipeType>>

    create(recipeType: Omit<RecipeType, "id">): Promise<CreateResult>

    update(recipeType: RecipeType): Promise<void>

    delete(id: number): Promise<void>
}

const findRecipeType = (instance: AxiosInstance) => async (id: number): Promise<RecipeType> => {
    try {
        const response = await instance.get<RecipeType>(`/recipetype/${id}`)
        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}

const getAllRecipeTypes = (instance: AxiosInstance) => async (): Promise<Array<RecipeType>> => {
    try {
        const response = await instance.get<Array<RecipeType>>("/recipetype")
        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}

const createRecipeType = (instance: AxiosInstance) => async (recipeType: Omit<RecipeType, "id">): Promise<CreateResult> => {
    try {
        const response = await instance.post<CreateResult>("/recipetype", recipeType)
        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}

const updateRecipeType = (instance: AxiosInstance) => async (recipeType: RecipeType): Promise<void> => {
    try {
        await instance.put("/recipetype", recipeType)
    } catch (err) {
        throw handleApiError(err)
    }
}

const deleteRecipeType = (instance: AxiosInstance) => async (id: number): Promise<void> => {
    try {
        await instance.delete(`/recipetype/${id}`)
    } catch (err) {
        throw handleApiError(err)
    }
}

const createRecipeTypeService = (instance: AxiosInstance): RecipeTypeService => ({
    find: findRecipeType(instance),
    getAll: getAllRecipeTypes(instance),
    create: createRecipeType(instance),
    update: updateRecipeType(instance),
    delete: deleteRecipeType(instance)
})

export interface RecipeType{
    id: number
    name: string
}

export default createRecipeTypeService
