import React from "react"
import { ColorMode, useColorMode } from "@chakra-ui/react"
import { render, screen } from "@testing-library/react"
import ThemeModeToggler from "./theme-mode-toggler"
import userEvent from "@testing-library/user-event"

jest.mock("@chakra-ui/react", () => ({

    // @ts-ignore
    ...jest.requireActual("@chakra-ui/react"),
    useColorMode: jest.fn()
}))
const useColorModeMock = useColorMode as jest.MockedFunction<typeof useColorMode>

describe("Theme mode toggler", () => {
    const toggleColorModeMock = jest.fn()

    useColorModeMock.mockImplementation(() => ({
        colorMode: "light",
        setColorMode: jest.fn(),
        toggleColorMode: toggleColorModeMock
    }))

    beforeEach(jest.clearAllMocks)

    it("calls the useColorMode hook on click", () => {
        render(<ThemeModeToggler/>)

        userEvent.click(screen.getByRole("button"))

        expect(toggleColorModeMock).toHaveBeenCalled()
    })

    test.each([
        ["dark", "light"],
        ["light", "dark"]
    ])("has the appropriate labeling the %s theme", (currentTheme, expectedTheme) => {
        useColorModeMock.mockReturnValueOnce({
            colorMode: currentTheme as ColorMode,
            setColorMode: jest.fn(),
            toggleColorMode: toggleColorModeMock
        })

        render(<ThemeModeToggler/>)

        expect(screen.getByLabelText(`Change to ${expectedTheme} theme`)).toBeInTheDocument()
    })

})
