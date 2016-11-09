<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <g:set var="entityName" value="${message(code: 'contact.label', default: 'Contact')}"/>
  <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-contact" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                              default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
  <ul>
    <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
    <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
  </ul>
</div>

<div id="show-contact" class="content" role="main">
  <h1><g:message code="default.show.label" args="[entityName]"/></h1>
  <g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
  </g:if>
  <ol class="property-list contact">
    <li class="fieldcontain">
      <span class="property-label">LDAP</span>

      <div class="property-value"><a href="mailto:${contact.email}">${contact?.ldap}</a></div>
    </li>
    <li class="fieldcontain">
      <span class="property-label">Name</span>

      <div class="property-value">${contact.name?.family}, ${contact.name?.given}</div>
    </li>
    <li class="fieldcontain">
      <span class="property-label">Email</span>

      <div class="property-value"><a href="mailto:${contact.email}">${contact.email}</a></div>
    </li>
    <li class="fieldcontain">
      <span class="property-label">Resources</span>

      <div class="property-value scrollable">
        <g:each in="${contact.resources}">
          <div><g:link action="show" controller="resource" id="${it.eai}">${it.eai} - ${it.name}</g:link></div>
        </g:each>
      </div>
    </li>
  </ol>
  <g:form resource="${contact}" method="DELETE" id="${contact.ldap}">
    <fieldset class="buttons">
      <g:link class="edit" action="edit" resource="${contact}" id="${contact.ldap}"><g:message
          code="default.button.edit.label" default="Edit"/></g:link>

      <g:if test="${!contact.resources}">
        <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}"/>
      </g:if>
    </fieldset>
  </g:form>
</div>
</body>
</html>
