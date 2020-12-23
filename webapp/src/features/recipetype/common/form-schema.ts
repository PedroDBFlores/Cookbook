import * as yup from "yup"

const RecipeTypeFormSchema = yup.object({
    name: yup.string()
        .required("Name is required")
        .min(1, "Name is required")
        .max(64, "Name exceeds the character limit")
})

export default RecipeTypeFormSchema