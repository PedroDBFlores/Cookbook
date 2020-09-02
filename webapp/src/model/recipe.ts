export interface Recipe {
    id: number
    recipeTypeId: number
    recipeTypeName?: string
    userId : number
    userName ?: string
    name: string
    description: string
    ingredients: string
    preparingSteps: string
}