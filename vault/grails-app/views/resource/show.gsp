<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>${resource.eai}</title>
</head>

<body>
<a href="#show-resource" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                               default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
  <ul>
    <li><g:link class="list" action="index">List Resources</g:link></li>
    <li><g:link class="create" action="create">New Resource</g:link></li>
  </ul>
</div>

<div id="show-resource" class="content scaffold-show" role="main">
  <h1><g:message code="default.show.label" args="[entityName]"/></h1>
  <g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
  </g:if>
  <ol class="property-list resource">
    <li class="fieldcontain">
      <span class="property-label">EAI</span>

      <div class="property-value">${resource.eai}</div>
    </li>
    <li class="fieldcontain">
      <span class="property-label">Name</span>

      <div class="property-value">${resource.name}</div>
    </li>

    <li class="fieldcontain">
      <span class="property-label">Description</span>

      <div class="property-value multi-line">
        <g:textArea name="resource.desc" value="${resource.desc}" disabled="true"/>
      </div>
    </li>

    <li class="fieldcontain">
      <span class="property-label">Assets</span>

      <div class="property-value">
        <g:select name="assets"
                  from="${resource.assets*.asset}"
                  optionValue="${{ it.eai + ' - ' + it.name }}"
                  multiple="true"
                  size="4"
                  disabled="true"/>
      </div>
    </li>

    <li class="fieldcontain">
      <span class="property-label">Contacts</span>

      <div class="property-value">
        <g:select name="contacts"
                  from="${resource.contacts}"
                  optionValue="${{ it.ldap + ' - ' + it.name?.family + ', ' + it.name?.given }}"
                  multiple="true"
                  size="4"
                  disabled="true"/>
      </div>
    </li>

    <li class="fieldcontain">
      <span class="property-label">Asset Of</span>

      <div class="property-value">
        <g:select name="assets"
                  from="${resource.assetOf*.root}"
                  optionValue="${{ it.eai + ' - ' + it.name }}"
                  multiple="true"
                  size="4"
                  disabled="true"/>
      </div>
    </li>

    <li class="fieldcontain">
      <span class="property-label">Components</span>

      <div class="property-value">
        <g:select name="components"
                  from="${resource.components}"
                  optionValue="${{ '[' + it.type + '] ' + it.name }}"
                  multiple="true"
                  size="4"
                  disabled="true"/>
      </div>
    </li>
  </ol>
  <g:form resource="${resource}" method="DELETE" id="${resource?.eai}">
    <fieldset class="buttons">
      <g:link class="edit" action="edit" resource="${resource}" id="${resource.eai}"><g:message
          code="default.button.edit.label" default="Edit"/></g:link>
      <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}"/>
    </fieldset>
  </g:form>
</div>
</body>
</html>
