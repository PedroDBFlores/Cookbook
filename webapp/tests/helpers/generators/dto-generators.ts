import { User } from "../../../src/dto"
import { name, internet, random } from "faker"

/**
 * Provides a User with random data
 * @see User
 */
export const generateUser = (props?: object): User => ({
    id: random.number(),
    firstName: name.firstName(),
    lastName: name.lastName(),
    userName: internet.userName(),
    email: internet.email(),
    ...props
})