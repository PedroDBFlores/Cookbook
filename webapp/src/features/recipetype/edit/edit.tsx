import React, {useRef} from "react"
import PropTypes from "prop-types"
import {IfFulfilled, IfPending, IfRejected, useAsync} from "react-async"
import {useHistory} from "react-router-dom"
import createRecipeTypeService, {RecipeType} from "services/recipe-type-service"
import {Text, useToast} from "@chakra-ui/react"
import Loader from "components/loader/loader"
import RecipeTypeForm from "components/recipe-type-form/recipe-type-form"
import Section from "components/section/section"

const EditRecipeType: React.FC<{ id: number }> = ({id}) => {
    const toast = useToast()
    const history = useHistory()

    const {find, update} = createRecipeTypeService()
    const findPromiseRef = useRef(() => find(id))
    const state = useAsync<RecipeType>({
        promiseFn: findPromiseRef.current,
        onReject: ({message}) => toast({
            title: "An error occurred while fetching the recipe type",
            description: message,
            status: "error",
            duration: 5000
        })
    })

    const handleOnSubmit = async (recipeType: RecipeType) => {
        try {
            await update(recipeType)
            toast({title: `Recipe type '${recipeType.name}' updated successfully!`, status: "success"})
            history.push(`/recipetype/${id}`)
        } catch ({message}) {
            toast({
                title: "An error occurred while updating the recipe type",
                description: message,
                status: "error",
                duration: 5000
            })
        }
    }

    return <Section title="Edit a recipe type">
        <IfPending state={state}>
            <Loader/>
        </IfPending>
        <IfRejected state={state}>
            <Text>Failed to fetch the recipe type</Text>
        </IfRejected>
        <IfFulfilled state={state}>
            {data => <RecipeTypeForm initialValues={data} onSubmit={handleOnSubmit}/>}
        </IfFulfilled>
    </Section>
}

EditRecipeType.propTypes = {
    id: PropTypes.number.isRequired
}

export default EditRecipeType
