import React, {useContext, useRef} from "react"
import PropTypes from "prop-types"
import {IfFulfilled, IfPending, IfRejected, useAsync} from "react-async"
import {MdDelete, MdEdit} from "react-icons/md"
import {ApiHandlerContext} from "../../../services/api-handler"
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
    Text,
    useToast
} from "@chakra-ui/react"
import ModalContext from "../../../components/modal/modal-context"
import Loader from "../../../components/loader/loader"

const RecipeDetails: React.FC<{ id: number }> = ({id}) => {
    const {setModalState} = useContext(ModalContext)
    const history = useHistory()
    const toast = useToast()

    const {find, delete: deleteRecipe} = createRecipeService(useContext(ApiHandlerContext))
    const findPromiseRef = useRef(() => find(id))
    const state = useAsync<RecipeDetail>({
        promiseFn: findPromiseRef.current,
        onReject: ({message}) => toast({
            title: "An error occurred while fetching the recipe",
            description: message,
            status: "error",
            duration: null
        })
    })

    const showModal = (id: number, name: string) => {
        setModalState({
            isOpen: true,
            props: {
                title: "Question",
                content: "Are you sure you want to delete this recipe?",
                actionText: "Delete",
                onAction: () => handleDelete(id, name),
                onClose: () => setModalState({isOpen: false})
            }
        })
    }

    const handleDelete = async (id: number, name: string) => {
        try {
            await deleteRecipe(id)
            toast({title: `Recipe '${name}' was deleted`, status: "success"})
            history.push("/recipe")
        } catch ({message}) {
            toast({
                title: "An error occurred while trying to delete this recipe",
                description: message,
                status: "error",
                duration: null
            })
        }
    }

    const onEdit = (id: number) => history.push(`/recipe/${id}/edit`)

    return <>
        <IfPending state={state}>
            <Loader/>
        </IfPending>
        <IfRejected state={state}>
            <Text>Failed to fetch the recipe</Text>
        </IfRejected>
        <IfFulfilled state={state}>
            {data => <Grid templateColumns="repeat(12, 1fr)" gap={6}>
                <GridItem colSpan={12}>
                    <Heading>Recipe details</Heading>
                </GridItem>
                <GridItem colSpan={12}>
                    <StatGroup>
                        <Stat>
                            <StatLabel>Id</StatLabel>
                            <StatNumber>{data.id}</StatNumber>
                        </Stat>
                        <Stat>
                            <StatLabel>Name</StatLabel>
                            <StatNumber>{data.name}</StatNumber>
                        </Stat>
                        <Stat>
                            <StatLabel>Description</StatLabel>
                            <StatNumber>{data.description}</StatNumber>
                        </Stat>
                        <Stat>
                            <StatLabel>Ingredients</StatLabel>
                            <StatNumber>{data.ingredients}</StatNumber>
                        </Stat>
                        <Stat>
                            <StatLabel>Preparing steps</StatLabel>
                            <StatNumber>{data.preparingSteps}</StatNumber>
                        </Stat>
                    </StatGroup>
                </GridItem>
                <GridItem colSpan={12}>
                    <ButtonGroup>
                        <Button aria-label={`Edit recipe '${data.name}'`}
                                onClick={() => onEdit(data.id)}>
                            <MdEdit/>
                        </Button>
                        <Button aria-label={`Delete recipe '${data.name}'`}
                                onClick={() => showModal(data.id, data.name)}>
                            <MdDelete/>
                        </Button>
                    </ButtonGroup>
                </GridItem>
            </Grid>
            }
        </IfFulfilled>
    </>
}

RecipeDetails.propTypes = {
    id: PropTypes.number.isRequired
}

export default RecipeDetails