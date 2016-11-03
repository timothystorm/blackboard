package org.storm.vault

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

class ResourceController {
  def create() {
    [resource:new Resource(), assets:Resource.list(), contacts:Contact.list()]
  }

  def delete() {
    with { resource -> 
      resource.delete()
      flash.message = "Resource '${params.id}' removed"
      redirect action:'index'
    }
  }

  def edit() {
    with { resource -> 
      // exclude self in resource lists
      def resources = Resource.list().findAll { it != resource }
      [resource:resource, assets:resources, assetOf:resources, contacts:Contact.list()]
    }
  }

  def index() {
    [resources:Resource.list(params)]
  }

  def save() {
    upsert(new Resource())
  }

  def show() {
    with { resource -> [resource:resource] }
  }

  def update() {
    with{ resource ->
      resource.clear() 
      upsert(resource)
    }
  }

  private def upsert(Resource resource) {
    // securely capture resource params
    resource.properties['eai', 'name', 'desc', 'contacts'] = params

    // update assets
    Resource.getAll(params.assets).each{ resource.addToAssets(it) }

    // save
    if(resource.save()) flash.message = "Resource ${params.eai} saved"
    else flash.message = "Failed to save '${params.eai}'"
    
    redirect action:'index'
  }

  private def with(Closure closure) {
    def resource = Resource.get(params['id'])
    if(resource) closure(resource)
    else {
      flash.message = "Resource '${params.id}' not found"
      redirect action:'index'
    }
  }
}
