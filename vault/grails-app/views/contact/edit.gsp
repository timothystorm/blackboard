<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <g:set var="entityName" value="${message(code: 'contact.label', default: 'Contact')}"/>
  <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body>
<a href="#edit-contact" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                              default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
  <ul>
    <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
    <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
  </ul>
</div>

<div id="edit-contact" class="content" role="main">
  <h1><g:message code="default.edit.label" args="[entityName]"/></h1>
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
  <g:form resource="${contact}" method="PUT" id="${contact?.ldap}">
    <g:hiddenField name="version" value="${contact?.version}"/>
    <fieldset class="form">
      <div class="fieldcontain required">
        <label for="ldap">LDAP</label><g:field type="number" name="ldap" value="${contact.ldap}" required="true"/>
      </div>

      <div class="fieldcontain required">
        <label for="name.given">Name</label><g:field type="text" name="name.given" value="${contact.name?.given}"
                                                     required="true"/>
      </div>

      <div class="fieldcontain">
        <label for="name.family"></label><g:field type="text" name="name.family" value="${contact.name?.family}"
                                                  required="true"/>
      </div>

      <div class="fieldcontain">
        <label for="email">Email</label><g:field type="email" name="email" value="${contact.email}" required="true"/>
      </div>
    </fieldset>
    <fieldset class="buttons">
      <input class="save" type="submit" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
    </fieldset>
  </g:form>
</div>
</body>
</html>
