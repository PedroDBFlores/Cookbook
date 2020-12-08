import {random} from "faker"
import {RecipeType} from "../../../src/services/recipe-type-service"
import {Recipe, RecipeDetails} from "../../../src/services/recipe-service"

export const generateRecipeType = (props?: Record<string, unknown>): RecipeType => ({
    id: random.number(),
    name: random.word(),
    ...props
})

export const generateRecipe = (props?: Record<string, unknown>): Recipe => ({
    id: random.number(),
    recipeTypeId: random.number(),
    name: random.word(),
    description: random.word(),
    ingredients: random.word(),
    preparingSteps: random.word(),
    ...props
})

export const generateRecipeDetails = (props?: Record<string, unknown>): RecipeDetails => ({
    ...generateRecipe(),
    recipeTypeName: random.word(),
    ...props
})