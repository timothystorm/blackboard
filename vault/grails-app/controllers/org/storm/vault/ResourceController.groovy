package org.storm.vault

class ResourceController {
    def create() {
        [resource: new Resource(), assets: Resource.list(), contacts: Contact.list()]
    }

    def delete() {
        with { resource ->
            resource.delete()
            flash.message = "Resource '${params.id}' removed"
            redirect action: 'index'
        }
    }

    def edit() {
        with { resource ->
            // exclude self in resource lists
            def resources = Resource.list().findAll { it != resource }
            [resource: resource, assets: resources, assetOf: resources, contacts: Contact.list()]
        }
    }

    def index() {
        [resources: Resource.list(params)]
    }

    def save() {
        upsave(new Resource())
    }

    def show() {
        with { resource -> [resource: resource] }
    }

    def update() {
        with { resource ->
            resource.clear()
            upsave(resource)
        }
    }

    /**
     * updates or saves resource by assigning request params to the resource
     * @param resource - to be updated/saved
     * @return a redirect to action:index
     */
    private def upsave(Resource resource) {
        // securely capture resource params with subscript operator
        resource.properties['eai', 'name', 'desc', 'contacts', 'components'] = params

        // update assets
        Resource.getAll(params.assets).each { resource.addToAssets(it) }

        // save
        if (resource.save(failOnError: true)) flash.message = "Resource ${resource.eai} - ${resource.name} saved"
        else flash.message = "Failed to save resource"

        redirect action: 'index'
    }

    private def with(Closure closure) {
        def resource = Resource.get(params['id'])
        if (resource) closure(resource)
        else {
            flash.message = "Resource '${params.id}' not found"
            redirect action: 'index'
        }
    }
}
