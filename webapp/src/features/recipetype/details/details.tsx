import React, {useRef, useState} from "react"
import {RecipeType} from "../../../dto"
import PropTypes from "prop-types"
import {findRecipeType} from "../../../services/recipe-type-service"
import {useAsync} from "react-async"
import Button from "react-bootstrap/Button"
import {faTrash} from "@fortawesome/free-solid-svg-icons"
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome"
import BasicModalDialog from "../../../components/modal/basic-modal-dialog"

interface RecipeTypeDetailsProps {
    id: number
    onDelete: (id: number) => void
}

const RecipeTypeDetails: React.FC<RecipeTypeDetailsProps> = ({id, onDelete}) => {
    const findPromiseRef = useRef(() => findRecipeType(id))
    const [showModal, setShowModal] = useState<boolean>(false)
    const {isPending, data: recipeType, error} = useAsync<RecipeType>({
        promiseFn: findPromiseRef.current
    })

    if (isPending) return <span>Loading...</span>
    if (error) return <span>Error: {error.message}</span>

    return (recipeType &&
      <>
        <dl>
          <dt>Id:</dt>
          <dd>{recipeType.id}</dd>
          <dt>Name:</dt>
          <dd>{recipeType.name}</dd>
        </dl>
        <Button aria-label={`Delete recipe type with id ${recipeType.id}`}
                onClick={() => setShowModal(true)}>
          <FontAwesomeIcon icon={faTrash}/>
        </Button>
          {showModal && <BasicModalDialog title="Question"
                                          content="Are you sure you want to delete this recipe type?"
                                          dismiss={{
                                              text: "Delete",
                                              onDismiss: () => onDelete(recipeType.id)
                                          }}
                                          onClose={() => setShowModal(false)}/>}
      </>) ?? null
}

RecipeTypeDetails.propTypes = {
    id: PropTypes.number.isRequired,
    onDelete: PropTypes.func.isRequired
}

export default RecipeTypeDetails
