import axios from 'axios'
import { generateRecipeType } from "../helpers/generators/dto-generators"
import { createRecipeTypeService } from "../../src/services/recipe-type-service"

jest.mock('axios')

const mockedAxios = axios as jest.Mocked<typeof axios>

describe("Recipe type service", () => {
    describe("Get all recipe types", () => {
        it("get all the recipe types", async () => {
            const allRecipeTypes = [generateRecipeType(), generateRecipeType()]

            mockedAxios.get.mockResolvedValue(allRecipeTypes)
            
            const res = await createRecipeTypeService().getAllRecipeTypes()
            expect(res).toStrictEqual(allRecipeTypes)
        })
    })
})