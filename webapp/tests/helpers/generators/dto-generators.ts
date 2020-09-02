import {User, RecipeType, Recipe} from "../../../src/model"
import { name, internet, random } from "faker"

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
    recipeTypeName: random.word(),
    userId: random.number(),
    userName: name.firstName(),
    name: random.word(),
    description: random.word(),
    ingredients: random.word(),
    preparingSteps: random.word(),
    ...props
})