import axios from "axios"
import { generateRecipeType } from "../helpers/generators/dto-generators"
import RecipeTypeService from "../../src/services/recipe-type-service"
import MockAdapter from "axios-mock-adapter"
import * as errors from "../../src/utils/error-handling"

const mockedAxios = new MockAdapter(axios)
const handleErrorsSpy = jest.spyOn(errors, "handleApiError")

describe("Recipe type service", () => {
    beforeEach(() => {
        mockedAxios.reset()
    })

    describe("Get a recipe type by id", () => {
        it("gets a recipe type", async () => {
            const expectedRecipeType = generateRecipeType()
            mockedAxios.onGet(`/api/recipetype/${expectedRecipeType.id}`)
                .reply(200, expectedRecipeType)

            const response = await new RecipeTypeService().find(expectedRecipeType.id)
            expect(mockedAxios.history.get.length).toBe(1)
            expect(response).toStrictEqual(expectedRecipeType)
        })

        fdescribe("Error handling", () => {

            it("throws an error with the message provided by the API on a 404", async () => {
                mockedAxios.onGet("/api/recipetype/1")
                    .reply(404, "Entity not found")

                await expect(new RecipeTypeService().find(1)).
                    rejects.toStrictEqual({
                        status: 404,
                        message: "Entity not found"
                    })
                console.log(handleErrorsSpy.mock.calls.length)
                expect(handleErrorsSpy).toHaveBeenCalled()

            })

            xit("throws an error with the message provided by the API on a 500", async () => {
                mockedAxios.onGet("/api/recipetype/1")
                    .reply(500, "Database Error")

                await expect(new RecipeTypeService().find(1)).
                    rejects.toStrictEqual(new Error("Database Error"))
                expect(handleErrorsSpy).toHaveBeenCalled()
            })
        })
    })

    describe("Get all recipe types", () => {
        it("get all the recipe types", async () => {
            const expectedRecipeTypes = [generateRecipeType(), generateRecipeType()]
            mockedAxios.onGet("/api/recipetype")
                .reply(200, expectedRecipeTypes)

            const response = await new RecipeTypeService().getAll()
            expect(mockedAxios.history.get.length).toBe(1)
            expect(response).toStrictEqual(expectedRecipeTypes)
        })

        it("throws an error with the message provided by the API on a 500", async () => {
            mockedAxios.onGet("/api/recipetype")
                .reply(500, "Database Error")

            await expect(new RecipeTypeService().getAll()).
                rejects.toStrictEqual(new Error("Database Error"))
        })
    })
})