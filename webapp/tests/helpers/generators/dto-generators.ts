import {name, internet, random} from "faker"
import {User} from "../../../src/model"
import {RecipeType} from "../../../src/services/recipe-type-service"
import {Recipe, RecipeDetails} from "../../../src/services/recipe-service"

/**
 * Provides a User with random data
 * @see User
 */
export const generateUser = (props?: Record<string, unknown>): User => ({
    id: random.number(),
    name: name.firstName(),
    userName: internet.userName(),
    ...props
})

export const generateRecipeType = (props?: Record<string, unknown>): RecipeType => ({
    id: random.number(),
    name: random.word(),
    ...props
})

export const generateRecipe = (props?: Record<string, unknown>): Recipe => ({
    id: random.number(),
    recipeTypeId: random.number(),
    userId: random.number(),
    name: random.word(),
    description: random.word(),
    ingredients: random.word(),
    preparingSteps: random.word(),
    ...props
})

export const generateRecipeDetails = (props?: Record<string, unknown>): RecipeDetails => ({
    ...generateRecipe(),
    recipeTypeName: random.word(),
    userName: name.firstName(),
    ...props
})