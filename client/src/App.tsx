import type { Component } from 'solid-js'
import DataDisplay from './ui/atoms/data-display'
import Section from './ui/molecules/section'

const App: Component = () => {
  return (
    <div>
      <DataDisplay title='Arroz' content='Pato' />
      <Section title="xxx">
        LOL
      </Section>
    </div>
  )
}

export default App
