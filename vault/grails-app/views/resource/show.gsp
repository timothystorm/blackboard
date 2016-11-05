<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <g:set var="entityName" value="${message(code: 'resource.label', default: 'Resource')}"/>
  <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-resource" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                               default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
  <ul>
    <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
    <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
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

      <div class="property-value">${resource?.eai}</div>
    </li>
    <li class="fieldcontain">
      <span class="property-label">Name</span>

      <div class="property-value">${resource.name}</div>
    </li>
    <g:if test="${resource?.contacts}">
      <li class="fieldcontain">
        <span class="property-label">Contacts</span>

        <div class="property-value"><g:select name="contacts" from="${resource.contacts}" optionValue="${{
          it.ldap + ' - ' + it.name?.family + ', ' + it.name?.given
        }}" multiple="true" size="${Math.max(Math.min(3, resource.contacts?.size()), 10)}" optionKey="ldap"
                                              disabled="true"/></div>
      </li>
    </g:if>
    <g:if test="${resource.assets}">
      <li class="fieldcontain">
        <span class="property-label">Assets</span>

        <div class="property-value">
          <g:select name="assets" from="${resource?.assets*.asset}" optionKey="eai"
                    optionValue="${{ it.eai + ' - ' + it.name }}" multiple="true"
                    size="${Math.max(Math.min(3, resource.assets?.size()), 10)}" disabled="true"/>
        </div>
      </li>
    </g:if>
    <g:if test="${resource.assetOf}">
      <li class="fieldcontain">
        <span class="property-label">Asset Of</span>

        <div class="property-value">
          <g:select name="assets" from="${resource?.assetOf*.root}" optionKey="eai"
                    optionValue="${{ it.eai + ' - ' + it.name }}" multiple="true"
                    size="${Math.max(Math.min(3, resource.assetOf?.size()), 10)}" disabled="true"/>
        </div>
      </li>
    </g:if>
    <g:if test="${resource?.desc}">
      <li class="fieldcontain">
        <span class="property-label">Description</span>

        <div class="property-value">${resource.desc}</div>
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
