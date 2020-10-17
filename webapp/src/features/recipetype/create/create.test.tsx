import React from "react"
import {screen} from "@testing-library/react"
import CreateRecipeType from "./create"
import {renderWrappedInCommonContexts, renderWithRoutes} from "../../../../tests/render"
import createRecipeTypeService from "../../../services/recipe-type-service"
import userEvent from "@testing-library/user-event"

jest.mock("../../../../src/services/recipe-type-service")
const createRecipeTypeServiceMock = createRecipeTypeService as jest.MockedFunction<typeof createRecipeTypeService>

describe("Create recipe type", () => {
    const createRecipeTypeMock = jest.fn()
    createRecipeTypeServiceMock.mockImplementation(() => {
        return {
            getAll: jest.fn(),
            update: jest.fn(),
            find: jest.fn(),
            delete: jest.fn(),
            create: createRecipeTypeMock
        }
    })

    beforeEach(() => createRecipeTypeMock.mockReset())

    it("renders the initial form", () => {
        const apiHandlerMock = jest.fn().mockReturnValue("My api handler")

        renderWrappedInCommonContexts(<CreateRecipeType/>, apiHandlerMock)

        expect(screen.getByText(/create a new recipe type/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/name/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/create recipe type/i)).toHaveAttribute("type", "submit")
        expect(screen.getByLabelText(/reset form/i)).toHaveAttribute("type", "reset")
        expect(createRecipeTypeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
    })

    describe("Form validation", () => {
        it("displays an error when the name is empty on submitting", async () => {
            renderWrappedInCommonContexts(<CreateRecipeType/>)

            userEvent.click(screen.getByLabelText(/create recipe type/i))

            expect(await screen.findByText(/name is required/i)).toBeInTheDocument()
        })

        it("displays an error when the name exceeds 64 characters", async () => {
            renderWrappedInCommonContexts(<CreateRecipeType/>)

            await userEvent.type(screen.getByLabelText(/^name$/i), "a".repeat(65))
            userEvent.click(screen.getByLabelText(/create recipe type/i))

            expect(await screen.findByText(/name exceeds the character limit/i)).toBeInTheDocument()
        })
    })

    it("create the recipe type in the Cookbook API and navigates to the details", async () => {
        createRecipeTypeMock.mockResolvedValueOnce({id: 1})
        renderWithRoutes({
            "/recipetype/new": () => <CreateRecipeType/>,
            "/recipetype/1": () => <div>I'm the recipe type details page for id 1</div>
        }, "/recipetype/new")

        await userEvent.type(screen.getByLabelText(/^name$/i), "Fish")
        userEvent.click(screen.getByLabelText(/create recipe type/i))

        expect(await screen.findByText(/^recipe type 'Fish' created successfully!$/i)).toBeInTheDocument()
        expect(await screen.findByText(/i'm the recipe type details page for id 1/i)).toBeInTheDocument()
        expect(createRecipeTypeMock).toHaveBeenCalledWith({name: "Fish"})
    })

    it("shows an error message if the create API call fails", async () => {
        createRecipeTypeMock.mockRejectedValueOnce({message: "Duplicate recipe type"})
        renderWrappedInCommonContexts(<CreateRecipeType/>)

        await userEvent.type(screen.getByLabelText(/^name$/i), "Fish")
        userEvent.click(screen.getByLabelText(/create recipe type/i))

        expect(await screen.findByText(/an error occurred while creating the recipe type: duplicate recipe type/i)).toBeInTheDocument()
        expect(createRecipeTypeMock).toHaveBeenCalledWith({name: "Fish"})
    })
})
