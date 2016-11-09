<%@ page import="org.springframework.validation.FieldError" %>
<%@ page import="org.storm.vault.Component.Type" %>

<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Edit Component</title>
</head>

<body>
<a href="#edit-component" class="skip" tabindex="-1">Skip to content&hellip;</a>

<div class="nav" role="navigation">
  <ul>
    <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
    <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
  </ul>
</div>

<div id="edit-component" class="content scaffold-edit" role="main">
  <h1><g:message code="default.edit.label" args="[entityName]"/></h1>
  <g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
  </g:if>
  <g:hasErrors bean="${component}">
    <ul class="errors" role="alert">
    <g:eachError bean="${component}" var="error">
      <li<g:if
        test="${error in FieldError}">data-field-id="${error.field}"</g:if>><g:message
        error="${error}"/></li>
    </g:eachError>
    </ul>
  </g:hasErrors>
  <g:form resource="${component}" method="PUT">
    <g:hiddenField name="version" value="${component?.version}"/>
    <fieldset class="form">
      <div class="fieldcontain required">
        <label for="type">Type</label><g:select from="${Type}"
                                                name="type"
                                                value="${component.type}"/>
      </div>

      <div class="fieldcontain required">
        <label for="name">Name</label><g:field type="text"
                                               name="name"
                                               value="${component.name}"
                                               required="true"/>
      </div>

      <div class="fieldcontain">
        <label for="detail">Detail</label><g:textArea name="detail"
                                                      value="${component?.detail}"
                                                      rows="3"/>
      </div>
    </fieldset>
    <fieldset class="buttons">
      <input class="save" type="submit" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
    </fieldset>
  </g:form>
</div>
</body>
</html>
