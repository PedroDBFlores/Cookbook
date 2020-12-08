import {CreateResult, SearchResult} from "../model"
import {AxiosInstance} from "axios"
import handleApiError from "../utils/error-handling"

export interface RecipeService {
    find(id: number): Promise<RecipeDetails>

    search(searchParameters: SearchRecipeParameters): Promise<SearchResult<RecipeDetails>>

    getAll(): Promise<Array<RecipeDetails>>

    create(recipe: Omit<Recipe, "id">): Promise<CreateResult>

    update(recipe: Recipe): Promise<void>

    delete(id: number): Promise<void>
}

const findRecipe = (instance: AxiosInstance) => async (id: number): Promise<RecipeDetails> => {
    try {
        const response = await instance.get<RecipeDetails>(`/recipe/${id}`)
        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}

const searchRecipes = (instance: AxiosInstance) => async (searchParameters: SearchRecipeParameters): Promise<SearchResult<RecipeDetails>> => {
    try {
        const response = await instance.post<SearchResult<RecipeDetails>>("/recipe/search", searchParameters)
        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}

const getAllRecipes = (instance: AxiosInstance) => async (): Promise<Array<RecipeDetails>> => {
    try {
        const response = await instance.get<Array<RecipeDetails>>("/recipe")
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

export interface Recipe {
    id: number
    recipeTypeId: number
    name: string
    description: string
    ingredients: string
    preparingSteps: string
}

export interface RecipeDetails extends Recipe {
    recipeTypeName: string
}

export interface SearchRecipeParameters {
    name?: string
    description?: string
    recipeTypeId?: number
    pageNumber: number
    itemsPerPage: number
}

export default createRecipeService