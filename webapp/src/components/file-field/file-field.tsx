import React from "react"
import {useTranslation} from "react-i18next"
import {FormControl, FormControlProps, FormHelperText, FormLabel} from "@chakra-ui/react"
import FilePicker from "chakra-ui-file-picker"

export interface FileFieldProps extends FormControlProps {
	name: string
	inputPlaceholder: string
	helperText?: string
}

const FileField: React.VFC<FileFieldProps> = (props) => {
	const { t } = useTranslation()

	const { name, inputPlaceholder, helperText, ...rest } = props

	return (
		<FormControl {...rest}>
			<FormLabel htmlFor={name}>{t("file-field.name")}</FormLabel>
			<FilePicker
				placeholder={inputPlaceholder}
				onFileChange={console.log}
				inputProps={{ id: name, "aria-label": name }} />
			{helperText && <FormHelperText>{helperText}</FormHelperText>}
		</FormControl>
	)
}

export default FileField
