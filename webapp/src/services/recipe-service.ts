import { CreateResult, SearchResult } from "model"
import axios from "axios"
import handleApiError from "utils/error-handling"

export interface RecipeService {
    find(id: number): Promise<RecipeDetails>

    search(searchParameters: SearchRecipeParameters): Promise<SearchResult<RecipeDetails>>

    getAll(): Promise<Array<RecipeDetails>>

    create(recipe: Omit<Recipe, "id">): Promise<CreateResult>

    update(recipe: Recipe): Promise<void>

    delete(id: number): Promise<void>
}

const findRecipe = () => async(id: number): Promise<RecipeDetails> => {
    try {
        const response = await axios.get<RecipeDetails>(`/api/recipe/${id}`)

        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}

const searchRecipes = () => async(searchParameters: SearchRecipeParameters): Promise<SearchResult<RecipeDetails>> => {
    try {
        const response = await axios.post<SearchResult<RecipeDetails>>("/api/recipe/search", searchParameters)

        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}

const getAllRecipes = () => async(): Promise<Array<RecipeDetails>> => {
    try {
        const response = await axios.get<Array<RecipeDetails>>("/api/recipe")

        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}

const createRecipe = () => async(recipe: Omit<Recipe, "id">): Promise<CreateResult> => {
    try {
        const response = await axios.post<CreateResult>("/api/recipe", recipe)

        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}

const updateRecipe = () => async(recipe: Recipe): Promise<void> => {
    try {
        const response = await axios.put("/api/recipe", recipe)

        return response.data
    } catch (err) {
        throw handleApiError(err)
    }
}
const deleteRecipe = () => async(id: number): Promise<void> => {
    try {
        await axios.delete(`/api/recipe/${id}`)
    } catch (err) {
        throw handleApiError(err)
    }
}

const createRecipeService = (): RecipeService => ({
    find: findRecipe(),
    search: searchRecipes(),
    getAll: getAllRecipes(),
    create: createRecipe(),
    update: updateRecipe(),
    delete: deleteRecipe()
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
