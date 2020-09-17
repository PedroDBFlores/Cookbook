export default interface SearchRecipeParameters {
    name?: string
    description?: string
    recipeTypeId?: number
    pageNumber: number
    itemsPerPage: number
}