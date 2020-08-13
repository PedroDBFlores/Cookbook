import React, {useRef, useState} from "react"
import {RecipeType} from "../../../dto"
import PropTypes from "prop-types"
import {deleteRecipeType, findRecipeType} from "../../../services/recipe-type-service"
import {IfFulfilled, IfPending, IfRejected, useAsync} from "react-async"
import BasicModalDialog from "../../../components/modal/basic-modal-dialog"
import {Button} from "@material-ui/core"
import {Delete, Edit} from "@material-ui/icons"
import {useHistory} from "react-router-dom"
import If from "../../../components/flow-control/if"

interface RecipeTypeDetailsProps {
    id: number
}

const RecipeTypeDetails: React.FC<RecipeTypeDetailsProps> = ({id}) => {
    const history = useHistory()
    const findPromiseRef = useRef(() => findRecipeType(id))
    const [showModal, setShowModal] = useState<boolean>(false)
    const state = useAsync<RecipeType>({
        promiseFn: findPromiseRef.current
    })

    const onDelete = (id: number) => deleteRecipeType(id)
        .then(() => history.push("/recipetype"))

    const onEdit = (id: number) => history.push(`/recipetype/${id}/edit`)

    return <>
        <IfPending state={state}>
            <span>Loading...</span>
        </IfPending>
        <IfRejected state={state}>
            {(error) => <span>Error: {error.message}</span>}
        </IfRejected>
        <IfFulfilled state={state}>
            {(data) => (
                <>
                    <dl>
                        <dt>Id:</dt>
                        <dd>{data.id}</dd>
                        <dt>Name:</dt>
                        <dd>{data.name}</dd>
                    </dl>
                    <Button aria-label={`Edit recipe type with id ${data.id}`}
                            onClick={() => onEdit(data.id)}>
                        <Edit/>
                    </Button>
                    <Button aria-label={`Delete recipe type with id ${data.id}`}
                            onClick={() => setShowModal(true)}>
                        <Delete/>
                    </Button>
                    <If condition={showModal}>
                        <BasicModalDialog title="Question"
                                          content="Are you sure you want to delete this recipe type?"
                                          dismiss={{
                                              text: "Delete",
                                              onDismiss: () => onDelete(data.id)
                                          }}
                                          onClose={() => setShowModal(false)}/>
                    </If>
                </>
            )}
        </IfFulfilled>
    </>
}

RecipeTypeDetails.propTypes = {
    id: PropTypes.number.isRequired,
}

export default RecipeTypeDetails
