package org.storm.vault

class ComponentController {
    def create() {
        [component: new Component()]
    }

    def index() {
        [components: Component.list(params)]
    }

    def save() {
        upsert(new Component())
    }

    def show() {
        with { component -> [component: component] }
    }

    private def upsert(Component component) {
        component.properties['type', 'name', 'detail'] = params

        if (component.save(failOnError: true)) flash.message = 'Component saved'
        else flash.message = 'Failed to save Component'

        redirect action: 'index'
    }

    private def with(Closure closure) {
        def component = Component.get(params['id'])
        if (component) closure(component)
        else {
            flash.message = "Component '${params.id}' not found"
            redirect action: 'index'
        }
    }
}
