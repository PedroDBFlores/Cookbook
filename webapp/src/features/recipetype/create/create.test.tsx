import React from "react"
import { render, screen } from "@testing-library/react"
import CreateRecipeType from "./create"
import { WrapperWithRoutes, WrapWithCommonContexts } from "../../../../tests/render-helpers"
import createRecipeTypeService from "services/recipe-type-service"
import userEvent from "@testing-library/user-event"

jest.mock("services/recipe-type-service")
const createRecipeTypeServiceMock = createRecipeTypeService as jest.MockedFunction<typeof createRecipeTypeService>

jest.mock("components/recipe-type-form/recipe-type-form", () => ({
    __esModule: true,
    default: jest.fn().mockImplementation(({ onSubmit }) => <>
        <p>Create recipe type form</p>
        <button aria-label="Create recipe type"
            onClick={() => onSubmit({ id: 0, name: "Fish" })}>
            Create
        </button>
    </>)
}))

describe("Create recipe type", () => {
    const createRecipeTypeMock = jest.fn()

    createRecipeTypeServiceMock.mockImplementation(() => ({
        getAll: jest.fn(),
        update: jest.fn(),
        find: jest.fn(),
        delete: jest.fn(),
        create: createRecipeTypeMock
    }))

    beforeEach(jest.clearAllMocks)

    it("renders the initial form", () => {
        render(<WrapWithCommonContexts>
            <CreateRecipeType/>
        </WrapWithCommonContexts>)

        expect(screen.getByText(/create a new recipe type/i)).toBeInTheDocument()
        expect(screen.getByText(/Create recipe type form/i)).toBeInTheDocument()
    })

    it("creates the recipe type in the Cookbook API and navigates to the details", async() => {
        createRecipeTypeMock.mockResolvedValueOnce({ id: 1 })
        render(<WrapWithCommonContexts>
            <WrapperWithRoutes initialPath="/recipetype/new" routeConfiguration={[
                { path: "/recipetype/new", exact: true, component: () => <CreateRecipeType/> },
                {
                    path: "/recipetype/1/details",
                    exact: true,
                    component: () => <div>I'm the recipe type details page for id 1</div>
                }
            ]}/>
        </WrapWithCommonContexts>)

        userEvent.click(screen.getByLabelText(/create recipe type/i))

        expect(await screen.findByText(/^recipe type 'Fish' created successfully!$/i)).toBeInTheDocument()
        expect(await screen.findByText(/i'm the recipe type details page for id 1/i)).toBeInTheDocument()
        expect(createRecipeTypeMock).toHaveBeenCalledWith({ name: "Fish" })
    })

    it("shows an error message if the create API call fails", async() => {
        createRecipeTypeMock.mockRejectedValueOnce({ message: "Duplicate recipe type" })
        render(<WrapWithCommonContexts>
            <CreateRecipeType/>
        </WrapWithCommonContexts>)

        userEvent.click(screen.getByLabelText(/create recipe type/i))

        expect(await screen.findByText(/^an error occurred while creating the recipe type$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^duplicate recipe type$/i)).toBeInTheDocument()
        expect(createRecipeTypeMock).toHaveBeenCalledWith({ name: "Fish" })
    })
})
