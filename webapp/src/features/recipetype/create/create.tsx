import React, {useContext} from "react"
import {useHistory} from "react-router-dom"
import createRecipeTypeService, {RecipeType} from "services/recipe-type-service"
import {ApiHandlerContext} from "services/api-handler"
import {Heading, useToast} from "@chakra-ui/react"
import RecipeTypeForm from "../../../components/recipe-type-form/recipe-type-form"

const CreateRecipeType: React.FC = () => {
    const {create} = createRecipeTypeService(useContext(ApiHandlerContext))
    const toast = useToast()
    const history = useHistory()

    const handleOnSubmit = async ({name}: Omit<RecipeType, "id">) => {
        try {
            const {id} = await create({name})
            toast({title: `Recipe type '${name}' created successfully!`, status: "success"})
            history.push(`/recipetype/${id}/details`)
        } catch ({message}) {
            toast({
                title: "An error occurred while creating the recipe type",
                description: message,
                status: "error",
                duration: 5000
            })
        }
    }

    return <>
        <Heading>Create a new recipe type</Heading>
        <RecipeTypeForm onSubmit={handleOnSubmit} />
    </>
}

export default CreateRecipeType
