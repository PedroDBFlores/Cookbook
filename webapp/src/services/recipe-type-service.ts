import axios, { AxiosError } from "axios"
import {CreateResult} from "model"
import handleApiError from "utils/error-handling"

export interface RecipeTypeService {
    find(id: number): Promise<RecipeType>

    getAll(): Promise<Array<RecipeType>>

    create(recipeType: Omit<RecipeType, "id">): Promise<CreateResult>

    update(recipeType: RecipeType): Promise<void>

    delete(id: number): Promise<void>
}

const findRecipeType = () => async (id: number): Promise<RecipeType> => {
    try {
        const response = await axios.get<RecipeType>(`/api/recipetype/${id}`)

        return response.data
    } catch (err) {
        throw handleApiError(err as AxiosError)
    }
}

const getAllRecipeTypes = () => async (): Promise<Array<RecipeType>> => {
    try {
        const response = await axios.get<Array<RecipeType>>("/api/recipetype")

        return response.data
    } catch (err) {
        throw handleApiError(err as AxiosError)
    }
}

const createRecipeType = () => async (recipeType: Omit<RecipeType, "id">): Promise<CreateResult> => {
    try {
        const response = await axios.post<CreateResult>("/api/recipetype", recipeType)

        return response.data
    } catch (err) {
        throw handleApiError(err as AxiosError)
    }
}

const updateRecipeType = () => async (recipeType: RecipeType): Promise<void> => {
    try {
        await axios.put("/api/recipetype", recipeType)
    } catch (err) {
        throw handleApiError(err as AxiosError)
    }
}

const deleteRecipeType = () => async (id: number): Promise<void> => {
    try {
        await axios.delete(`/api/recipetype/${id}`)
    } catch (err) {
        throw handleApiError(err as AxiosError)
    }
}

const createRecipeTypeService = (): RecipeTypeService => ({
    find: findRecipeType(),
    getAll: getAllRecipeTypes(),
    create: createRecipeType(),
    update: updateRecipeType(),
    delete: deleteRecipeType()
})

export interface RecipeType {
    id: number
    name: string
}

export default createRecipeTypeService
