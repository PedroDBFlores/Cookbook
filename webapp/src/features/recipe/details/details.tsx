import React, {useContext, useRef} from "react"
import PropTypes from "prop-types"
import {useAsync} from "react-async"
import {MdDelete, MdEdit} from "react-icons/md"
import {ApiHandlerContext} from "../../../services/api-handler"
import {Choose, When} from "../../../components/flow-control/choose"
import {useHistory} from "react-router-dom"
import createRecipeService, {RecipeDetails as RecipeDetail} from "../../../services/recipe-service"
import {
    Button,
    ButtonGroup,
    Grid,
    GridItem,
    Heading,
    Stat,
    StatGroup,
    StatLabel,
    StatNumber,
    useToast
} from "@chakra-ui/react"
import ModalContext from "../../../components/modal/modal-context"

const RecipeDetails: React.FC<{ id: number }> = ({id}) => {
    const {setModalState} = useContext(ModalContext)
    const history = useHistory()
    const toast = useToast()

    const {find, delete: deleteRecipe} = createRecipeService(useContext(ApiHandlerContext))
    const findPromiseRef = useRef(() => find(id))
    const state = useAsync<RecipeDetail>({
        promiseFn: findPromiseRef.current
    })

    const showModal = () => {
        setModalState({
            isOpen: true,
            props: {
                title: "Question",
                content: "Are you sure you want to delete this recipe?",
                actionText: "Delete",
                onAction: () => handleDelete(Number(state.data?.id)),
                onClose: () => setModalState({isOpen: false})
            }
        })
    }

    const handleDelete = (id: number) => deleteRecipe(id)
        .then(() => {
            toast({title: `Recipe ${id} was deleted`, status: "success"})
            history.push("/recipe")
        }).catch(err => toast({
            title: `An error occurred while trying to delete this recipe: ${err.message}`,
            status: "error"
        }))

    const onEdit = (id: number) => history.push(`/recipe/${id}/edit`)

    return <Choose>
        <When condition={state.isLoading}>
            <span>Loading...</span>
        </When>
        <When condition={state.isRejected}>
            <span>Error: {state.error?.message}</span>
        </When>
        <When condition={state.isFulfilled}>
            <Grid templateColumns="repeat(12, 1fr)" gap={6}>
                <GridItem colSpan={12}>
                    <Heading>Recipe details</Heading>
                </GridItem>
                <GridItem colSpan={12}>
                    <StatGroup>
                        <Stat>
                            <StatLabel>Id</StatLabel>
                            <StatNumber>{state.data?.id}</StatNumber>
                        </Stat>
                        <Stat>
                            <StatLabel>Name</StatLabel>
                            <StatNumber>{state.data?.name}</StatNumber>
                        </Stat>
                        <Stat>
                            <StatLabel>Description</StatLabel>
                            <StatNumber>{state.data?.description}</StatNumber>
                        </Stat>
                        <Stat>
                            <StatLabel>Ingredients</StatLabel>
                            <StatNumber>{state.data?.ingredients}</StatNumber>
                        </Stat>
                        <Stat>
                            <StatLabel>Preparing steps</StatLabel>
                            <StatNumber>{state.data?.preparingSteps}</StatNumber>
                        </Stat>
                    </StatGroup>
                </GridItem>
                <GridItem colSpan={12}>
                    <ButtonGroup>
                        <Button aria-label={`Edit recipe with id ${state.data?.id}`}
                                onClick={() => onEdit(Number(state.data?.id))}>
                            <MdEdit/>
                        </Button>
                        <Button aria-label={`Delete recipe with id ${state.data?.id}`}
                                onClick={showModal}>
                            <MdDelete/>
                        </Button>
                    </ButtonGroup>
                </GridItem>
            </Grid>
        </When>
    </Choose>
}

RecipeDetails.propTypes = {
    id: PropTypes.number.isRequired
}

export default RecipeDetails