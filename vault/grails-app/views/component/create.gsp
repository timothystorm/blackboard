<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Create Component</title>
</head>

<body>
<a href="#create-component" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                  default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
  <ul>
    <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    <li><g:link class="list" action="index">Component List</g:link></li>
  </ul>
</div>

<div id="create-component" class="content scaffold-create" role="main">
  <h1>Create Component</h1>
  <g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
  </g:if>
  <g:hasErrors bean="${component}">
    <ul class="errors" role="alert">
    <g:eachError bean="${component}" var="error">
      <li<g:if
        test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
        error="${error}"/></li>
    </g:eachError>
    </ul>
  </g:hasErrors>
  <g:form action="save">
    <fieldset class="form">
      <div class="fieldcontain required">
        <label for="type">Type</label><g:select from="${org.storm.vault.Component.Type}" name="type"/>
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
      <g:submitButton name="create" class="save"
                      value="${message(code: 'default.button.create.label', default: 'Create')}"/>
    </fieldset>
  </g:form>
</div>
</body>
</html>
