import React from "react"
import {render, screen} from "@testing-library/react"
import {Choose, Otherwise, When} from "../../../src/components/flow-control/choose"

describe("Choose flow control component", () => {
    describe("with only one 'When' element", () => {
        it("displays 'Hello from when!' if the only one element has a true condition", () => {
            render(<Choose>
                <When condition={true}>
                    <p>Hello from when!</p>
                </When>
            </Choose>)

            expect(screen.getByText(/^hello from when!$/i)).toBeInTheDocument()
        })

        it("displays nothing if the only one element has a false condition", () => {
            render(<Choose>
                <When condition={false}>
                    <p>Hello from when!</p>
                </When>
            </Choose>)

            expect(screen.queryByText(/^hello from when!$/i)).not.toBeInTheDocument()
        })

        it("displays 'Hello from the otherwise!' if the when is false but there's an 'Otherwise' component", () => {
            render(<Choose>
                <When condition={false}>
                    <p>Hello from when!</p>
                </When>
                <Otherwise>
                    <p>Hello from the otherwise!</p>
                </Otherwise>
            </Choose>)

            expect(screen.getByText(/^hello from the otherwise!$/i)).toBeInTheDocument()
            expect(screen.queryByText(/^hello from when!$/i)).not.toBeInTheDocument()
        })

        it("displays 'Hello from when!' if the when is true but there's an 'Otherwise' component", () => {
            render(<Choose>
                <When condition={true}>
                    <p>Hello from when!</p>
                </When>
                <Otherwise>
                    <p>Hello from the otherwise!</p>
                </Otherwise>
            </Choose>)

            expect(screen.getByText(/^hello from when!$/i)).toBeInTheDocument()
            expect(screen.queryByText(/^hello from the otherwise!$/i)).not.toBeInTheDocument()
        })
    })

    describe("with two or more 'When' elements", () => {
        it("displays 'The first one' from the element with the true condition", () => {
            render(<Choose>
                <When condition={true}>
                    <p>The first one</p>
                </When>
                <When condition={false}>
                    <p>The second one</p>
                </When>
                <When condition={false}>
                    <p>The third</p>
                </When>
            </Choose>)

            expect(screen.getByText(/^the first one$/i)).toBeInTheDocument()
            expect(screen.queryByText(/^the second one$/i)).not.toBeInTheDocument()
            expect(screen.queryByText(/^the third one$/i)).not.toBeInTheDocument()
        })

        it("displays nothing when all 'When' are false", () => {
            render(<Choose>
                <When condition={false}>
                    <p>The first one</p>
                </When>
                <When condition={false}>
                    <p>The second one</p>
                </When>
                <When condition={false}>
                    <p>The third</p>
                </When>
            </Choose>)

            expect(screen.queryByText(/^the first one$/i)).not.toBeInTheDocument()
            expect(screen.queryByText(/^the second one$/i)).not.toBeInTheDocument()
            expect(screen.queryByText(/^the third one$/i)).not.toBeInTheDocument()
        })

        it("displays 'The first one' even if there's an 'Otherwise'", () => {
            render(<Choose>
                <When condition={true}>
                    <p>The first one</p>
                </When>
                <When condition={true}>
                    <p>The second one</p>
                </When>
                <When condition={true}>
                    <p>The third</p>
                </When>
                <Otherwise>
                    <p>Hello from the otherwise!</p>
                </Otherwise>
            </Choose>)

            expect(screen.getByText(/^the first one$/i)).toBeInTheDocument()
            expect(screen.queryByText(/^the second one$/i)).not.toBeInTheDocument()
            expect(screen.queryByText(/^the third one$/i)).not.toBeInTheDocument()
            expect(screen.queryByText(/^hello from the otherwise!$/i)).not.toBeInTheDocument()
        })

        it("displays 'Hello from the otherwise!' if all the 'When' are false", () => {
            render(<Choose>
                <When condition={false}>
                    <p>The first one</p>
                </When>
                <When condition={false}>
                    <p>The second one</p>
                </When>
                <When condition={false}>
                    <p>The third</p>
                </When>
                <Otherwise>
                    <p>Hello from the otherwise!</p>
                </Otherwise>
            </Choose>)

            expect(screen.getByText(/^hello from the otherwise!$/i)).toBeInTheDocument()
            expect(screen.queryByText(/^the first one$/i)).not.toBeInTheDocument()
            expect(screen.queryByText(/^the second one$/i)).not.toBeInTheDocument()
            expect(screen.queryByText(/^the third one$/i)).not.toBeInTheDocument()
        })
    })
})