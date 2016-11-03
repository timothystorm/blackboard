package org.storm.vault

class ContactController {
  def scaffold = Contact

  def create() {
    [contact:new Contact()]
  }

  def delete() {
    with { contact ->
      contact.delete()
      flash.message = "Contact removed"
      redirect action:'index'
    }
  }

  def edit() {
    with { [contact:it] }
  }

  def index() {
    [contacts:Contact.list(params)] 
  }

  def save() {
    upsert(new Contact())
  }

  def update() {
    with { contact -> upsert(contact) }
  }

  private def with(Closure closure) {
    def contact = Contact.get(params['id'])
    if(contact) closure(contact)
    else {
      flash.message = "Resource '${params.id}' not found"
      redirect action:'index'
    }
  }

  private def upsert(Contact contact){
    contact.properties['ldap', 'name', 'email'] = params
    
    if(contact.save()) flash.message = 'Contact saved'
    else flash.message = 'Failed to save Contact'

    redirect action:'index'
  }
}