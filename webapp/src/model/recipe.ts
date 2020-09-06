export default interface Recipe {
    id: number
    recipeTypeId: number
    userId : number
    name: string
    description: string
    ingredients: string
    preparingSteps: string
}

export interface RecipeDetails extends Recipe{
    recipeTypeName: string
    userName : string
}
