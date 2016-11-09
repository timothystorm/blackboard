<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Show Component</title>
</head>

<body>
<a href="#show-component" class="skip" tabindex="-1">Skip to content&hellip;</a>

<div class="nav" role="navigation">
  <ul>
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

    <g:if test="${component.componentOf}">
      <li class="fieldcontain">
        <span class="property-label">Component Of</span>

        <div class="property-value scrollable">
          <g:each in="${component.componentOf}">
            <div><g:link action="show" controller="resource" id="${it.eai}">${it.eai} - ${it.name}</g:link></div>
          </g:each>
        </div>
      </li>
    </g:if>
  </ol>
  <g:form resource="${this.component}" method="DELETE">
    <fieldset class="buttons">
      <g:link class="edit" action="edit" resource="${this.component}"><g:message code="default.button.edit.label"
                                                                                 default="Edit"/></g:link>
      <g:if test="${!component.componentOf}">
        <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}"/>
      </g:if>
    </fieldset>
  </g:form>
</div>
</body>
</html>
