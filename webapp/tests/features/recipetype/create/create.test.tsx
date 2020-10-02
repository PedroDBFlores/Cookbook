import React from "react"
import {screen, fireEvent, waitFor} from "@testing-library/react"
import CreateRecipeType from "../../../../src/features/recipetype/create/create"
import {renderWrappedInCommonContexts, renderWithRoutes} from "../../../render"
import createRecipeTypeService from "../../../../src/services/recipe-type-service"

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
        const submitButton = screen.getByLabelText(/create recipe type/i)
        expect(submitButton).toHaveAttribute("type", "submit")
        const resetButton = screen.getByLabelText(/reset form/i)
        expect(resetButton).toHaveAttribute("type", "reset")

        expect(createRecipeTypeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
    })

    describe("Form validation", () => {
        it("displays an error when the name is empty on submitting", async () => {
            renderWrappedInCommonContexts(<CreateRecipeType/>)

            const submitButton = screen.getByLabelText(/create recipe type/i)
            fireEvent.submit(submitButton)

            await waitFor(() =>
                expect(screen.getByText(/name is required/i)).toBeInTheDocument()
            )
        })

        it("displays an error when the name exceeds 64 characters", async () => {
            renderWrappedInCommonContexts(<CreateRecipeType/>)

            const nameInput = screen.getByLabelText(/name/i)
            fireEvent.change(nameInput, {target: {value: "a".repeat(65)}})
            const submitButton = screen.getByLabelText(/create recipe type/i)
            fireEvent.submit(submitButton)

            await waitFor(() =>
                expect(screen.getByText(/name exceeds the character limit/i)).toBeInTheDocument()
            )
        })
    })

    it("create the recipe type in the Cookbook API and navigates to the details", async () => {
        createRecipeTypeMock.mockResolvedValueOnce({id: 1})
        renderWithRoutes({
            "/recipetype/new": () => <CreateRecipeType/>,
            "/recipetype/1": () => <div>I'm the recipe type details page for id 1</div>
        }, "/recipetype/new")

        const nameInput = screen.getByLabelText(/name/i)
        fireEvent.change(nameInput, {target: {value: "Fish"}})
        const submitButton = screen.getByLabelText(/create recipe type/i)
        fireEvent.submit(submitButton)

        await waitFor(() => {
            expect(createRecipeTypeMock).toHaveBeenCalledWith({name: "Fish"})
            expect(screen.getByText(/^recipe type 'Fish' created successfully!$/i)).toBeInTheDocument()
            expect(screen.getByText(/i'm the recipe type details page for id 1/i)).toBeInTheDocument()
        })
    })

    it("shows an error message if the create API call fails", async () => {
        createRecipeTypeMock.mockRejectedValueOnce({message: "Duplicate recipe type"})
        renderWrappedInCommonContexts(<CreateRecipeType/>)

        const nameInput = screen.getByLabelText(/name/i)
        fireEvent.change(nameInput, {target: {value: "Fish"}})
        const submitButton = screen.getByLabelText(/create recipe type/i)
        fireEvent.submit(submitButton)

        await waitFor(() => {
            expect(createRecipeTypeMock).toHaveBeenCalledWith({name: "Fish"})
            expect(screen.getByText(/an error occurred while creating the recipe type: duplicate recipe type/i)).toBeInTheDocument()
        })
    })
})
