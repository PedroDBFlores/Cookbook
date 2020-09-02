import {CreateResult, Recipe, SearchRecipeRepresenter} from "../model"
import {AxiosInstance} from "axios"
import handleApiError from "../utils/error-handling"

export interface RecipeService {
    find(id: number): Promise<Recipe>

    search(searchParameters: SearchRecipeRepresenter): Promise<Array<Recipe>>

    getAll(): Promise<Array<Recipe>>

    create(recipe: Omit<Recipe, "id">): Promise<CreateResult>

    update(recipe: Recipe): Promise<void>

    delete(id: number): Promise<void>
}

const findRecipe = (instance: AxiosInstance) => async (id: number): Promise<Recipe> => {
    try {
        const response = await instance.get<Recipe>(`/recipe/${id}`)
        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}

const searchRecipes = (instance: AxiosInstance) => async (searchParameters: SearchRecipeRepresenter): Promise<Array<Recipe>> => {
    try {
        const response = await instance.post<Array<Recipe>>("/recipe/search", searchParameters)
        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}

const getAllRecipes = (instance: AxiosInstance) => async (): Promise<Array<Recipe>> => {
    try {
        const response = await instance.get<Array<Recipe>>("/recipe")
        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}

const createRecipe = (instance: AxiosInstance) => async (recipe: Omit<Recipe, "id">): Promise<CreateResult> => {
    try {
        const response = await instance.post<CreateResult>("/recipe", recipe)
        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}

const updateRecipe = (instance: AxiosInstance) => async (recipe: Recipe): Promise<void> => {
    try {
        const response = await instance.put("/recipe", recipe)
        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}
const deleteRecipe = (instance: AxiosInstance) => async (id: number): Promise<void> => {
    try {
        await instance.delete(`/recipe/${id}`)
    } catch (err) {
        throw handleApiError(err)
    }
}


const createRecipeService = (instance: AxiosInstance): RecipeService => ({
    find: findRecipe(instance),
    search: searchRecipes(instance),
    getAll: getAllRecipes(instance),
    create: createRecipe(instance),
    update: updateRecipe(instance),
    delete: deleteRecipe(instance)
})
export default createRecipeService