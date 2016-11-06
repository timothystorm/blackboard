<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Create Contact</title>
</head>

<body>
<a href="#create-contact" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
  <ul>
    <li><g:link class="list" action="index" controller="contact">Contact List</g:link></li>
  </ul>
</div>

<div id="create-contact" class="contact" role="main">
  <h1><g:message code="default.create.label" args="[entityName]"/></h1>
  <g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
  </g:if>
  <g:hasErrors bean="${contact}">
    <ul class="errors" role="alert">
      <g:eachError bean="${contact}" var="error">
        <li<g:if
               test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
            error="${error}"/></li>
      </g:eachError>
    </ul>
  </g:hasErrors>
  <g:form action="save">
    <fieldset class="form">
      <div class="fieldcontain required">
        <label for="eai">LDAP</label><g:field type="number" name="ldap" value="${contact.ldap}" required="true"/>
      </div>

      <div class="fieldcontain required">
        <label for="name.given">Name</label><g:field type="text" name="name.given" value="${contact.name?.given}"
                                                     required="true" placeholder="First Name"/>
      </div>

      <div class="fieldcontain required">
        <label for="name.family"></label><g:field type="text" name="name.family" value="${contact.name?.family}"
                                                  required="true" placeholder="Last Name"/>
      </div>

      <div class="fieldcontain required">
        <label for="email">Email</label><g:field type="email" name="email" value="${contact?.email}" required="true"/>
      </div>
    </fieldset>
    <fieldset class="buttons">
      <g:submitButton name="create" class="save"
                      value="${message(code: 'default.button.create.label', default: 'Create')}"/>
    </fieldset>
  </g:form>
</div>
</body>
</html>
