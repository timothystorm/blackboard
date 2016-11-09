<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>${resource.eai} - ${resource.name}</title>
</head>

<body>
<a href="#show-resource" class="skip" tabindex="-1">Skip to content&hellip;</a>

<div class="nav" role="navigation">
  <ul>
    <li><g:link class="list" action="index">List Resources</g:link></li>
    <li><g:link class="create" action="create">New Resource</g:link></li>
  </ul>
</div>

<div id="show-resource" class="content scaffold-show" role="main">
  <h1>${resource.eai} - ${resource.name}</h1>
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

    <g:if test="${resource.desc}">
      <li class="fieldcontain">
        <span class="property-label">Description</span>

        <div class="property-value multi-line">
          <g:textArea name="resource.desc" value="${resource.desc}" disabled="true"/>
        </div>
      </li>
    </g:if>

    <g:if test="${resource.assets}">
      <li class="fieldcontain">
        <span class="property-label">Assets</span>

        <div class="property-value scrollable">
          <g:each in="${resource.assets*.asset}">
            <div><g:link action="show" controller="resource" id="${it.eai}">${it.eai} - ${it.name}</g:link></div>
          </g:each>
        </div>
      </li>
    </g:if>

    <g:if test="${resource.contacts}">
      <li class="fieldcontain">
        <span class="property-label">Contacts</span>

        <div class="property-value scrollable">
          <g:each in="${resource.contacts}">
            <div><g:link action="show" controller="contact"
                         id="${it.id}">${it.ldap} - ${it.name?.family}, ${it.name?.given}</g:link></div>
          </g:each>
        </div>
      </li>
    </g:if>

    <g:if test="${resource.assetOf}">
      <li class="fieldcontain">
        <span class="property-label">Asset Of</span>

        <div class="property-value scrollable">
          <g:each in="${resource.assets*.root}">
            <div><g:link action="show" controller="resource" id="${it.eai}">${it.eai} - ${it.name}</g:link></div>
          </g:each>
        </div>
      </li>
    </g:if>

    <g:if test="${resource.components}">
      <li class="fieldcontain">
        <span class="property-label">Components</span>

        <div class="property-value scrollable">
          <g:each in="${resource.components}">
            <div><g:link action="show" controller="component" id="${it.id}">[${it.type}] ${it.name}</g:link></div>
          </g:each>
        </div>
      </li>
    </g:if>
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
