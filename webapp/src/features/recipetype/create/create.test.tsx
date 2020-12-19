import React from "react"
import {render, screen} from "@testing-library/react"
import CreateRecipeType from "./create"
import {WrapWithCommonContexts, WrapperWithRoutes} from "../../../../tests/render-helpers"
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

        render(<WrapWithCommonContexts apiHandler={apiHandlerMock}>
            <CreateRecipeType/>
        </WrapWithCommonContexts>)

        expect(screen.getByText(/create a new recipe type/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/name/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/create recipe type/i)).toHaveAttribute("type", "submit")
        expect(screen.getByLabelText(/reset form/i)).toHaveAttribute("type", "button")
        expect(createRecipeTypeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
    })

    describe("Form validation", () => {
        it("displays an error when the name is empty on submitting", async () => {
            render(<WrapWithCommonContexts>
                <CreateRecipeType/>
            </WrapWithCommonContexts>)

            userEvent.click(screen.getByLabelText(/create recipe type/i))

            expect(await screen.findByText(/name is required/i)).toBeInTheDocument()
        })

        it("displays an error when the name exceeds 64 characters", async () => {
            render(<WrapWithCommonContexts>
                <CreateRecipeType/>
            </WrapWithCommonContexts>)

            userEvent.paste(screen.getByLabelText(/^name$/i), "a".repeat(65))
            userEvent.click(screen.getByLabelText(/create recipe type/i))

            expect(await screen.findByText(/name exceeds the character limit/i)).toBeInTheDocument()
        })
    })

    it("create the recipe type in the Cookbook API and navigates to the details", async () => {
        createRecipeTypeMock.mockResolvedValueOnce({id: 1})
        render(<WrapWithCommonContexts>
            <WrapperWithRoutes initialPath="/recipetype/new" routeConfiguration={[
                {path: "/recipetype/new", exact: true, component: () => <CreateRecipeType/>},
                {
                    path: "/recipetype/1/details",
                    exact: true,
                    component: () => <div>I'm the recipe type details page for id 1</div>
                }
            ]}/>
        </WrapWithCommonContexts>)

        await userEvent.type(screen.getByLabelText(/^name$/i), "Fish")
        userEvent.click(screen.getByLabelText(/create recipe type/i))

        expect(await screen.findByText(/^recipe type 'Fish' created successfully!$/i)).toBeInTheDocument()
        expect(await screen.findByText(/i'm the recipe type details page for id 1/i)).toBeInTheDocument()
        expect(createRecipeTypeMock).toHaveBeenCalledWith({name: "Fish"})
    })

    it("shows an error message if the create API call fails", async () => {
        createRecipeTypeMock.mockRejectedValueOnce({message: "Duplicate recipe type"})
        render(<WrapWithCommonContexts>
            <CreateRecipeType/>
        </WrapWithCommonContexts>)

        await userEvent.type(screen.getByLabelText(/^name$/i), "Fish")
        userEvent.click(screen.getByLabelText(/create recipe type/i))

        expect(await screen.findByText(/an error occurred while creating the recipe type: duplicate recipe type/i)).toBeInTheDocument()
        expect(createRecipeTypeMock).toHaveBeenCalledWith({name: "Fish"})
    })
})
