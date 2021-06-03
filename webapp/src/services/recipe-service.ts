import {CreateResult, SearchResult} from "model"
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

const findRecipe = () => async (id: number): Promise<RecipeDetails> =>
    axios.get<RecipeDetails>(`/api/recipe/${id}`)
        .then(response => response.data)
        .catch(err => {
            throw handleApiError(err)
        })

const searchRecipes = () => async (searchParameters: SearchRecipeParameters): Promise<SearchResult<RecipeDetails>> =>
    axios.post<SearchResult<RecipeDetails>>("/api/recipe/search", searchParameters)
        .then(response => response.data)
        .catch(err => {
            throw handleApiError(err)
        })

const getAllRecipes = () => async (): Promise<Array<RecipeDetails>> =>
    axios.get<Array<RecipeDetails>>("/api/recipe")
        .then(response => response.data)
        .catch(err => {
            throw handleApiError(err)
        })

const createRecipe = () => async (recipe: Omit<Recipe, "id">): Promise<CreateResult> =>
    axios.post<CreateResult>("/api/recipe", recipe)
        .then(response => response.data)
        .catch(err => {
            throw handleApiError(err)
        })


const updateRecipe = () => async (recipe: Recipe): Promise<void> =>
    axios.put("/api/recipe", recipe)
        .then(response => response.data)
        .catch(err => {
            throw handleApiError(err)
        })

const deleteRecipe = () => async (id: number): Promise<void> =>
    axios.delete(`/api/recipe/${id}`)
        .then(response => response.data)
        .catch(err => {
            throw handleApiError(err)
        })

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
