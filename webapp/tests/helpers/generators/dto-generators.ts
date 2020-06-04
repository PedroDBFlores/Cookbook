import { User, RecipeType } from "../../../src/dto"
import { name, internet, random } from "faker"

/**
 * Provides a User with random data
 * @see User
 */
export const generateUser = (props?: Record<string, unknown>): User => ({
    id: random.number(),
    firstName: name.firstName(),
    lastName: name.lastName(),
    userName: internet.userName(),
    email: internet.email(),
    ...props
})

export const generateRecipeType = (props?: Record<string, unknown>): RecipeType => ({
    id: random.number(),
    name: random.word(),
    ...props
})