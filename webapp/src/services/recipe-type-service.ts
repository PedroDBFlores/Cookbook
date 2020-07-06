import axios from "axios"
import {RecipeType} from "../dto"
import handleApiError from "../utils/error-handling"

class RecipeTypeService {

    async find(id: number): Promise<RecipeType> {
        try {
            const response = await axios.get<RecipeType>(`/api/recipetype/${id}`)
            return response.data
        } catch (err) {
            throw handleApiError(err)
        }
    }

    async getAll(): Promise<Array<RecipeType>> {
        try {
            const response = await axios.get<Array<RecipeType>>("/api/recipetype")
            return response.data
        } catch (err) {
            throw new Error(err.response.data)
        }
    }

    async create(recipeType: Omit<RecipeType, "id">): Promise<number> {
        return 0
    }

    async update(recipeType: RecipeType): Promise<number> {
        return 0
    }

    async delete(id: number): Promise<number> {
        return 0
    }
}

export default RecipeTypeService
