import React, {useContext, useRef} from "react"
import PropTypes from "prop-types"
import {IfFulfilled, IfPending, IfRejected, useAsync} from "react-async"
import {MdDelete, MdEdit} from "react-icons/md"
import {useHistory} from "react-router-dom"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import {ApiHandlerContext} from "../../../services/api-handler"
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
import Loader from "../../../components/loader/loader"

const RecipeTypeDetails: React.FC<{ id: number }> = ({id}) => {
    const {setModalState} = useContext(ModalContext)
    const history = useHistory()
    const toast = useToast()

    const {find, delete: deleteRecipeType} = createRecipeTypeService(useContext(ApiHandlerContext))
    const findPromiseRef = useRef(() => find(id))
    const state = useAsync<RecipeType>({
        promiseFn: findPromiseRef.current
    })

    const showModal = (data: RecipeType) => {
        setModalState({
            isOpen: true,
            props: {
                title: "Question",
                content: "Are you sure you want to delete this recipe type?",
                actionText: "Delete",
                onAction: () => handleDelete(data.id, data.name),
                onClose: () => setModalState({isOpen: false})
            }
        })
    }

    const handleDelete = async (id: number, name: string) => {
        try {
            await deleteRecipeType(id)
            toast({title: `Recipe type ${name} was deleted`, status: "success"})
            history.push("/recipetype")
        } catch (err) {
            toast({
                title: `An error occurred while trying to delete this recipe type: ${err.message}`,
                status: "error"
            })
        }
    }

    const onEdit = (id: number) => history.push(`/recipetype/${id}/edit`)

    return <>
        <IfPending state={state}>
            <Loader/>
        </IfPending>
        <IfRejected state={state}>
            {err => <span>Error: {err?.message}</span>}
        </IfRejected>
        <IfFulfilled state={state}>
            {data => <Grid templateColumns="repeat(12, 1fr)" gap={6}>
                <GridItem colSpan={12}>
                    <Heading>Recipe type details</Heading>
                </GridItem>
                <GridItem colSpan={12}>
                    <StatGroup>
                        <Stat>
                            <StatLabel>Id:</StatLabel>
                            <StatNumber>{data.id}</StatNumber>
                        </Stat>
                        <Stat>
                            <StatLabel>Name:</StatLabel>
                            <StatNumber>{data.name}</StatNumber>
                        </Stat>
                    </StatGroup>
                    <ButtonGroup>
                        <Button aria-label={`Edit recipe type '${data.name}'`}
                                onClick={() => onEdit(data.id)}>
                            <MdEdit/>
                        </Button>
                        <Button aria-label={`Delete recipe type '${data.name}'`}
                                onClick={() => showModal(data)}>
                            <MdDelete/>
                        </Button>
                    </ButtonGroup>
                </GridItem>
            </Grid>
            }
        </IfFulfilled>
    </>
}
RecipeTypeDetails.propTypes = {
    id: PropTypes.number.isRequired
}

export default RecipeTypeDetails
