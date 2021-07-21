import { render, screen } from "@testing-library/react"
import { Form, Formik } from "formik"
import React from "react"
import FileField from "./file-field"
import FilePicker from "chakra-ui-file-picker"

jest.mock("chakra-ui-file-picker")
const FilePickerMock = FilePicker as jest.MockedClass<typeof FilePicker>

FilePickerMock.mockImplementation((props) => ({
	// ...jest.requireActual("chakra-ui-file-picker"),
	render: () => <div aria-label={props.inputProps?.["aria-label"]} placeholder={props.placeholder}>
		<p>Mock File Picker</p>
	</div>
} as FilePicker))

describe("File field", () => {
	it("renders the initial component", () => {
		render(
			// eslint-disable-next-line @typescript-eslint/no-empty-function
			<Formik initialValues={{}} onSubmit={jest.fn()}>
				<Form>
					<FileField name="file" inputPlaceholder="Place" />
				</Form>
			</Formik>
		)

		expect(screen.getByLabelText(/file/i)).toBeInTheDocument()
		expect(screen.getByPlaceholderText(/place/i)).toBeInTheDocument()
		expect(screen.getByText(/translated file-field.name/i)).toBeInTheDocument()
	})

	it("allows to provide helper text", () => {
		render(
			// eslint-disable-next-line @typescript-eslint/no-empty-function
			<Formik initialValues={{}} onSubmit={jest.fn()}>
				<Form>
					<FileField
						name="file"
						inputPlaceholder="Place"
						helperText="You can put your file on"
					/>
				</Form>
			</Formik>
		)

		expect(screen.getByText(/You can put your file on/i)).toBeInTheDocument()
	})

	describe("Formik", () => {

	})
})
