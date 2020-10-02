import React from "react"
import PropTypes from "prop-types"
import {Field} from "formik"
import {Select} from "formik-material-ui"
import InputLabel from "@material-ui/core/InputLabel"
import MenuItem from "@material-ui/core/MenuItem"
import FormControl from "@material-ui/core/FormControl"
import FormHelperText from "@material-ui/core/FormHelperText"
import If from "../flow-control/if"

interface FormikSelectorProps {
    options: Array<{ id: number; name: string }>
    label: string
    formName: string
    ariaLabel?: string
    className?: string
    error?: string
}

const FormikSelector: React.FC<FormikSelectorProps> = ({
                                                           options,
                                                           label,
                                                           formName,
                                                           ariaLabel,
                                                           className,
                                                           error
                                                       }) => {
    return <FormControl className={className ?? ""} error={!!error}>
        <InputLabel htmlFor={`${formName}-field`}>{label}</InputLabel>
        <Field
            component={Select}
            name={formName}
            autoWidth={true}
            inputProps={{
                id: `${formName}-field`,
                ["aria-label"]: ariaLabel ?? ""
            }}>
            <MenuItem value={0}> </MenuItem>
            {
                options.map(({id, name}) =>
                    <MenuItem key={`${formName}-${id}`}
                              value={id}>{name}</MenuItem>)
            }
        </Field>
        <If condition={!!error}>
            <FormHelperText>{error}</FormHelperText>
        </If>
    </FormControl>
}
FormikSelector.propTypes = {
    options: PropTypes.array.isRequired,
    label: PropTypes.string.isRequired,
    formName: PropTypes.string.isRequired,
    ariaLabel: PropTypes.string,
    className: PropTypes.string,
    error: PropTypes.string
}

export default FormikSelector
