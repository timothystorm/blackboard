package org.storm.vault

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

class ResourceController {
  def scaffold = Resource

  def create() {
    [resource:new Resource(), assets:Resource.list()]
  }

  def edit() {
    with { resource -> 
      def resources = Resource.list().findAll { it != resource }
      [resource:resource, assets:resources, assetOf:resources]
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
      upsert(resource.clear())
    }
  }

  def delete() {
    with { resource -> 
      resource.delete()
      flash.message = "Resource '${params.id}' removed"
      redirect action:'index'
    }
  }

  private def upsert(Resource resource) {
    // securely capture resource params
    resource.properties['eai', 'name'] = params

    // add assets
    Resource.getAll(params.assets).each{ resource.addToAssets(it) }

    // save
    if(resource.save()) redirect action:'show', id:resource.eai
    else {
      flash.message = "failed to save '${params.eai}'"
      redirect action:'index'
    }
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
