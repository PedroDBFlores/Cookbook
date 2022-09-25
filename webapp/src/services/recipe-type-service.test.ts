import axios from "axios"
import MockAdapter from "axios-mock-adapter"
import * as errorHandler from "utils/error-handling"
import createRecipeTypeService, {RecipeType} from "./recipe-type-service"

jest.mock("utils/error-handling", () => ({
    __esModule: true,
    ...jest.requireActual("utils/error-handling")
}))

const mockedAxios = new MockAdapter(axios)
const handleErrorsSpy = jest.spyOn(errorHandler, "default")
const service = createRecipeTypeService()

describe("Recipe type service", () => {
    beforeEach(() => {
        localStorage.setItem("token", "A_TOKEN")
        mockedAxios.reset()
        handleErrorsSpy.mockClear()
    })

    describe("Find recipe type", () => {
        it("finds a recipe type by id", async () => {
            const expectedRecipeType: RecipeType = {id: 1, name: "A recipe type"}

            mockedAxios.onGet(`/api/recipetype/${expectedRecipeType.id}`)
                .replyOnce(200, expectedRecipeType)

            const response = await service.find(expectedRecipeType.id)

            expect(mockedAxios.history.get.length).toBe(1)
            expect(mockedAxios.history.get[0].url).toBe(`/api/recipetype/${expectedRecipeType.id}`)
            expect(response).toStrictEqual(expectedRecipeType)
        })

        it("calls the error handler", async () => {
            mockedAxios.onGet("/api/recipetype/1")
                .replyOnce(404, {
                    code: "NOT_FOUND",
                    message: "Entity not found"
                })

            await expect(service.find(1)).rejects.toBeDefined()
            expect(handleErrorsSpy).toHaveBeenCalled()
        })
    })

    describe("Get all", () => {
        it("gets all the recipe types", async () => {
            const expectedRecipeTypes: Array<RecipeType> = [{id: 1, name: "A recipe type"}, {
                id: 2,
                name: "Another recipe type"
            }]

            mockedAxios.onGet("/api/recipetype")
                .replyOnce(200, expectedRecipeTypes)

            const response = await service.getAll()

            expect(mockedAxios.history.get.length).toBe(1)
            expect(mockedAxios.history.get[0].url).toBe("/api/recipetype")
            expect(response).toStrictEqual(expectedRecipeTypes)
        })

        it("calls the error handler", async () => {
            mockedAxios.onGet("/api/recipetype")
                .replyOnce(500, {
                    code: "INTERNAL_SERVER_ERROR",
                    message: "Database Error"
                })

            await expect(service.getAll()).rejects.toBeDefined()
            expect(handleErrorsSpy).toHaveBeenCalled()
        })
    })

    describe("Create", () => {
        it("creates a recipe", async () => {
            const recipeTypeToCreate = {name: "Fish"}
            const expectedResponse = {id: 12345}

            mockedAxios.onPost("/api/recipetype", recipeTypeToCreate)
                .replyOnce(201, expectedResponse)

            const result = await service.create(recipeTypeToCreate)

            expect(mockedAxios.history.post.length).toBe(1)
            expect(mockedAxios.history.post[0].url).toBe("/api/recipetype")
            expect(result).toStrictEqual(expectedResponse)
        })

        it("calls the error handler", async () => {
            mockedAxios.onPost("/api/recipetype")
                .replyOnce(500, {
                    code: "INTERNAL_SERVER_ERROR",
                    message: "Database Error"
                })

            await expect(service.create({name: ""})).rejects.toBeDefined()
            expect(handleErrorsSpy).toHaveBeenCalled()
        })
    })

    describe("Update", () => {
        it("updates a recipe type", async () => {
            const recipeTypeToUpdate = {id: 1, name: "A better recipe type"}

            mockedAxios.onPut("/api/recipetype", recipeTypeToUpdate)
                .replyOnce(200)

            await service.update(recipeTypeToUpdate)

            expect(mockedAxios.history.put.length).toBe(1)
            expect(mockedAxios.history.put[0].url).toBe("/api/recipetype")

        })

        it("calls the error handler", async () => {
            mockedAxios.onPut("/api/recipetype")
                .replyOnce(500, {
                    code: "INTERNAL_SERVER_ERROR",
                    message: "Database Error"
                })

            await expect(service.update({id: 123, name: ""})).rejects.toBeDefined()
            expect(handleErrorsSpy).toHaveBeenCalled()
        })
    })

    describe("Delete", () => {
        it("deletes a recipe type", async () => {
            const idToDelete = 709

            mockedAxios.onDelete(`/api/recipetype/${idToDelete}`)
                .replyOnce(204)

            await service.delete(idToDelete)

            expect(mockedAxios.history.delete.length).toBe(1)
            expect(mockedAxios.history.delete[0].url).toBe(`/api/recipetype/${idToDelete}`)

        })

        it("calls the error handler", async () => {
            mockedAxios.onDelete("/api/recipetype/1")
                .replyOnce(500, {
                    code: "INTERNAL_SERVER_ERROR",
                    message: "Database Error"
                })

            await expect(service.delete(1)).rejects.toBeDefined()
            expect(handleErrorsSpy).toHaveBeenCalled()
        })
    })
})
