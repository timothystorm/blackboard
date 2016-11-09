<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <g:set var="entityName" value="${message(code: 'component.label', default: 'Component')}"/>
  <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-component" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
  <ul>
    <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
    <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
  </ul>
</div>

<div id="show-component" class="content scaffold-show" role="main">
  <h1><g:message code="default.show.label" args="[entityName]"/></h1>
  <g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
  </g:if>
  <ol class="property-list component">
    <li class="fieldcontain">
      <span class="property-label">Type</span>

      <div class="property-value">${component?.type}</div>
    </li>
    <li class="fieldcontain">
      <span class="property-label">Name</span>

      <div class="property-value">${component?.name}</div>
    </li>
    <li class="fieldcontain">
      <span class="property-label">Detail</span>

      <div class="property-value">${component?.detail}</div>
    </li>
    <li class="fieldcontain">
      <span class="property-label">Component</span>

      <div class="property-value">
        <g:select name="resources"
                  from="${component?.componentOf()}"
                  optionValue="${{ it.eai + ' - ' + it.name }}"
                  multiple="true"
                  size="4"
                  disabled="true"/>
      </div>
    </li>
  </ol>
  <g:form resource="${this.component}" method="DELETE">
    <fieldset class="buttons">
      <g:link class="edit" action="edit" resource="${this.component}"><g:message code="default.button.edit.label"
                                                                                 default="Edit"/></g:link>
      <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}"
             onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
    </fieldset>
  </g:form>
</div>
</body>
</html>
