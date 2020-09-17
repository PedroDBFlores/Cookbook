import React from "react"
import {Field} from "formik"
import PropTypes from "prop-types"
import {Select} from "formik-material-ui"
import InputLabel from "@material-ui/core/InputLabel"
import MenuItem from "@material-ui/core/MenuItem"
import FormControl from "@material-ui/core/FormControl"

interface FormikSelectorProps {
    options: Array<{ id: number; name: string }>
    label: string
    formName: string
    ariaLabel?: string
    className?: string
}

const FormikSelector: React.FC<FormikSelectorProps> = ({
                                                           options,
                                                           label,
                                                           formName,
                                                           ariaLabel,
                                                           className
                                                       }) => {
    return <FormControl className={className ?? ""}>
        <InputLabel htmlFor={`${formName}-field`}>{label}</InputLabel>
        <Field
            component={Select}
            name={formName}
            autoWidth={true}
            inputProps={{
                id: `${formName}-field`,
                ["aria-label"]: ariaLabel ?? ""
            }}>
            <MenuItem value={undefined}> </MenuItem>
            {
                options.map(({id, name}) =>
                    <MenuItem key={`${formName}-${id}`}
                              value={id}>{name}</MenuItem>)
            }
        </Field>
    </FormControl>
}
FormikSelector.propTypes = {
    options: PropTypes.array.isRequired,
    label: PropTypes.string.isRequired,
    formName: PropTypes.string.isRequired,
    ariaLabel: PropTypes.string,
    className: PropTypes.string,
}

export default FormikSelector
