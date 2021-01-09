import React, {useEffect} from "react"
import {render, screen} from "@testing-library/react"
import RecipeTypeDetails from "./details"
import Modal from "components/modal/modal"
import createRecipeTypeService, {RecipeType} from "services/recipe-type-service"
import userEvent from "@testing-library/user-event"
import {WrapperWithRoutes, WrapWithCommonContexts} from "../../../../tests/render-helpers"

jest.mock("services/recipe-type-service")
const createRecipeTypeServiceMock = createRecipeTypeService as jest.MockedFunction<typeof createRecipeTypeService>

jest.mock("components/modal/modal", () => {
    return {
        __esModule: true,
        default: jest.fn().mockImplementation(() => <div>Delete RecipeType Modal</div>)
    }
})
const basicModalDialogMock = Modal as jest.MockedFunction<typeof Modal>

describe("Recipe type details", () => {
    const baseRecipeType: RecipeType = {id: 99, name: "A recipe type"}

    const findRecipeTypeMock = jest.fn()
    const deleteRecipeTypeMock = jest.fn()

    createRecipeTypeServiceMock.mockImplementation(() => {
        return {
            getAll: jest.fn(),
            update: jest.fn(),
            find: findRecipeTypeMock,
            delete: deleteRecipeTypeMock,
            create: jest.fn()
        }
    })

    beforeEach(jest.clearAllMocks)

    it("renders the recipe type details", async () => {
        const apiHandlerMock = jest.fn().mockReturnValue("My api handler")
        findRecipeTypeMock.mockResolvedValueOnce(baseRecipeType)

        render(<WrapWithCommonContexts apiHandler={apiHandlerMock}>
            <RecipeTypeDetails id={99}/>
        </WrapWithCommonContexts>)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        expect(createRecipeTypeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
        expect(findRecipeTypeMock).toHaveBeenCalledWith(99)
        expect(await screen.findByText(/^recipe type details$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Id:$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Name:$/i)).toBeInTheDocument()
        expect(await screen.findByText(baseRecipeType.id.toString())).toBeInTheDocument()
        expect(await screen.findByText(baseRecipeType.name)).toBeInTheDocument()
    })

    it("renders an error if the recipe type cannot be obtained", async () => {
        findRecipeTypeMock.mockRejectedValueOnce(new Error("Failure"))

        render(<WrapWithCommonContexts>
            <RecipeTypeDetails id={99}/>
        </WrapWithCommonContexts>)

        expect(await screen.findByText(/^an error occurred while fetching the recipe type$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^failure$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^failed to fetch the recipe type$/i)).toBeInTheDocument()
        expect(findRecipeTypeMock).toHaveBeenCalled()
    })

    describe("Actions", () => {

        it("takes the user to the edit recipe type page", async () => {
            findRecipeTypeMock.mockResolvedValueOnce(baseRecipeType)
            render(<WrapWithCommonContexts>
                <WrapperWithRoutes initialPath={`/recipetype/${baseRecipeType.id}/details`} routeConfiguration={[
                    {
                        path: `/recipetype/${baseRecipeType.id}/details`,
                        exact: true,
                        component: () => <RecipeTypeDetails id={baseRecipeType.id}/>
                    },
                    {
                        path: `/recipetype/${baseRecipeType.id}/edit`,
                        exact: true,
                        component: () => <div>I'm the recipe type edit page</div>
                    }
                ]}/>
            </WrapWithCommonContexts>)

            userEvent.click(await screen.findByLabelText(`Edit recipe type '${baseRecipeType.name}'`))

            expect(screen.getByText(/I'm the recipe type edit page/i)).toBeInTheDocument()
        })

        it("deletes the recipe type", async () => {
            findRecipeTypeMock.mockResolvedValueOnce(baseRecipeType)
            deleteRecipeTypeMock.mockResolvedValueOnce({})
            basicModalDialogMock.mockImplementationOnce(({content, onAction}) => {
                useEffect(() => onAction(), [])
                return <div>{content}</div>
            })

            render(<WrapWithCommonContexts>
                <WrapperWithRoutes initialPath={`/recipetype/${baseRecipeType.id}/details`} routeConfiguration={[
                    {
                        path: `/recipetype/${baseRecipeType.id}/details`,
                        exact: true,
                        component: () => <RecipeTypeDetails id={baseRecipeType.id}/>
                    },
                    {
                        path: "/recipetype",
                        exact: true,
                        component: () => <div>I'm the recipe type list page</div>
                    }
                ]}/>
            </WrapWithCommonContexts>)

            userEvent.click(await screen.findByLabelText(`Delete recipe type '${baseRecipeType.name}'`))

            expect(screen.getByText(/are you sure you want to delete this recipe type?/i)).toBeInTheDocument()

            expect(deleteRecipeTypeMock).toHaveBeenCalledWith(baseRecipeType.id)
            expect(await screen.findByText(`Recipe type ${baseRecipeType.name} was deleted`)).toBeInTheDocument()
            expect(await screen.findByText(/I'm the recipe type list page/i)).toBeInTheDocument()
        })

        it("shows an error if deleting the recipe type fails", async () => {
            findRecipeTypeMock.mockResolvedValueOnce(baseRecipeType)
            deleteRecipeTypeMock.mockRejectedValueOnce({message: "In use"})
            basicModalDialogMock.mockImplementationOnce(({content, onAction}) => {
                useEffect(() => onAction(), [])
                return <div>{content}</div>
            })
            render(<WrapWithCommonContexts>
                <RecipeTypeDetails id={99}/>
            </WrapWithCommonContexts>)

            userEvent.click(await screen.findByLabelText(`Delete recipe type '${baseRecipeType.name}'`))

            expect(await screen.findByText(/^An error occurred while trying to delete this recipe type$/i)).toBeInTheDocument()
            expect(await screen.findByText(/^in use$/i)).toBeInTheDocument()
        })
    })
})
