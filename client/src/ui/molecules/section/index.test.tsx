import { render, screen } from '@solidjs/testing-library'
import Section from '.'

describe('Section component', () => {
    it('renders a section with title and content', () => {
        render(() => <Section title="A title" >
            The content
        </Section>)

        expect(screen.getByText(/a title/i)).toBeInTheDocument()
        expect(screen.getByText(/the content/i)).toBeInTheDocument()
    })

    it('renders an actions component for the section', () => {
        const Actions = () => <button>Some action</button>

        render(() => <Section title="A title" actions={<Actions />}>
            The content
        </Section>)

        expect(screen.getByText('Some action')).toBeInTheDocument()
    })
})
