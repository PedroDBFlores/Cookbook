import React from "react"
import {render, screen} from "@testing-library/react"
import If from "../../../src/components/flow-control/if"

describe("If flow control component", () => {
    it("renders the 'truthy' component", () => {
        render(<If condition={true}>
            You know it's true
        </If>)

        expect(screen.getByText(/you know it's true/i)).toBeInTheDocument()
    })

    it("renders the 'falsy' component", () => {
        render(<If condition={false} elseRender={<div>Cause you are GOLD!</div>}>
            You know it's true
        </If>)

        expect(screen.getByText(/cause you are gold!/i)).toBeInTheDocument()
    })
})