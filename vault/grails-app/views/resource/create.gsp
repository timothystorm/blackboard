<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Create Resource</title>
</head>

<body>
<a href="#create-resource" class="skip" tabindex="-1">Skip to content&hellip;</a>

<div class="nav" role="navigation">
  <ul>
    <li><g:link class="list" action="index">Resource List</g:link></li>
  </ul>
</div>

<div id="create-resource" class="content scaffold-create" role="main">
  <h1>Create Resource</h1>
  <g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
  </g:if>
  <g:hasErrors bean="${resource}">
    <ul class="errors" role="alert">
    <g:eachError bean="${resource}" var="error">
      <li<g:if
        test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
        error="${error}"/></li>
    </g:eachError>
    </ul>
  </g:hasErrors>
  <g:form action="save">
    <fieldset class="form">
      <div class="fieldcontain required">
        <label for="eai"><g:message code="vault.label.eai"/></label><g:field type="number"
                                                                             name="eai"
                                                                             value="${resource?.eai}"
                                                                             required="true"
                                                                             min="0"/>
      </div>

      <div class="fieldcontain required">
        <label for="name">Name</label><g:field type="text"
                                               name="name"
                                               value="${resource?.name}"
                                               required="true"/>
      </div>

      <div class="fieldcontain">
        <label for="desc">Description</label><g:textArea name="desc"
                                                         value="${resource?.desc}"
                                                         rows="3"/>
      </div>

      <g:if test="${assets}">
        <div class="fieldcontain">
          <label for="assets"><g:message code="vault.label.assets"/></label><g:select name="assets"
                                                                                      from="${assets}"
                                                                                      optionKey="eai"
                                                                                      optionValue="${{
                                                                                        it.eai + ' - ' + it.name
                                                                                      }}"
                                                                                      multiple="true"
                                                                                      size="4"/>
        </div>
      </g:if>

      <g:if test="${contacts}">
        <div class="fieldcontain">
          <label for="contacts">
            <g:link action="create" controller="contact" title="Create New Contact">Contacts</g:link>
          </label><g:select name="contacts"
                            from="${contacts}"
                            optionKey="ldap"
                            optionValue="${{ it.ldap + ' - ' + it.name?.family + ', ' + it.name?.given }}"
                            multiple="true"
                            size="4"/>

        </div>
      </g:if>
      <g:else>
        <div class="fieldcontain">
          <g:link action="create" controller="contact" title="Create New Contact">Create Contact</g:link>
        </div>
      </g:else>

      <g:if test="${components}">
        <div class="fieldcontain">
          <label for="components">
            <g:link action="create" controller="component" title="Create New Component"><g:message
                code="vault.label.components"/></g:link>
          </label><g:select name="components"
                            from="${components}"
                            optionKey="id"
                            optionValue="${{ '[' + it.type + '] ' + it.name }}"
                            multiple="true"
                            size="4"/>
        </div>
      </g:if>
      <g:else>
        <div class="fieldcontain">
          <g:link action="create" controller="component" title="Create New Component">Create Component</g:link>
        </div>
      </g:else>
    </fieldset>
    <fieldset class="buttons">
      <g:submitButton name="create"
                      class="save"
                      value="${message(code: 'default.button.create.label', default: 'Create')}"/>
    </fieldset>
  </g:form>
</div>
</body>
</html>
