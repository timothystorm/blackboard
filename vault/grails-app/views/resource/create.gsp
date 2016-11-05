<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <g:set var="entityName" value="${message(code: 'resource.label', default: 'Resource')}"/>
  <title><g:message code="default.create.label" args="[entityName]"/></title>
</head>

<body>
<a href="#create-resource" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                 default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
  <ul>
    <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
  </ul>
</div>

<div id="create-resource" class="content scaffold-create" role="main">
  <h1><g:message code="default.create.label" args="[entityName]"/></h1>
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
        <label for="eai">EAI</label><g:field type="number" name="eai" value="${resource?.eai}" required="true"/>
      </div>

      <div class="fieldcontain required">
        <label for="name">Name</label><g:field type="text" name="name" value="${resource?.name}" required="true"/>
      </div>

      <div class="fieldcontain">
        <label for="detail.desc">Description</label><g:textArea name="desc" value="${resource?.desc}" rows="5"
                                                                cols="40"/>
      </div>

      <g:if test="${assets}">
        <div class="fieldcontain">
          <label for="assets">Assets</label><g:select name="assets" from="${assets}" optionKey="eai"
                                                      optionValue="${{ it.eai + ' - ' + it.name }}" multiple="true"
                                                      size="${Math.max(Math.min(3, assets?.size()), 10)}"/>
        </div>
      </g:if>

      <span class="components">
        <div class="fieldcontain">
          <label for="components"><g:link action="create" controller="component"
                                          title="Create New Component">Components</g:link></label>
          <g:select name="components" from="${components}" optionValue="${{
            '[' it.type + '] ' + it.name
          }}" multiple="true" size="${Math.max(Math.min(3, (components ? components.size() : 0)), 10)}" optionKey="id"/>
        </div>
      </span>

      <span class="contacts">
        <div class="fieldcontain">
          <label for="contacts"><g:link action="create" controller="contact"
                                        title="Create New Contact">Contacts</g:link></label>
          <g:select name="contacts" from="${contacts}" optionValue="${{
            it.ldap + ' - ' + it.name?.family + ', ' + it.name?.given
          }}" multiple="true" size="${Math.max(Math.min(3, (contacts ? contacts.size : 0)), 10)}" optionKey="ldap"/>

        </div>
      </span>
    </fieldset>
    <fieldset class="buttons">
      <g:submitButton name="create" class="save"
                      value="${message(code: 'default.button.create.label', default: 'Create')}"/>
    </fieldset>
  </g:form>
</div>
</body>
</html>