<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title><g:message code="vault.title.components"/></title>
</head>

<body>
<a href="#list-resource" class="skip" tabindex="-1">Skip to content&hellip;</a>

<div class="nav" role="navigation">
  <ul>
    <li><g:link class="create" action="create"><g:message code="vault.title.createresource"/></g:link></li>
  </ul>
</div>

<div id="list-resource" class="content scaffold-list" role="main">
  <g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
  </g:if>

  <g:if test="${resources}">
    <table>
      <thead>
      <tr>
        <g:sortableColumn property="eai" titleKey="vault.label.eai"/>
        <g:sortableColumn property="name" titleKey="vault.label.name"/>
        <th><g:message code="vault.label.assets"/></th>
        <th><g:message code="vault.label.assetof"/></th>
        <th><g:link action="index" controller="contact"><g:message code="vault.label.contacts"/></g:link></th>
        <th><g:link action="index" controller="component"><g:message code="vault.label.components"/></g:link></th>
      </tr>
      </thead>
      <tbody>
      <g:each in="${resources}" var="resource">
        <tr>
          <td><g:link action="show" id="${resource.eai}">${resource?.eai}</g:link></td>
          <td><g:link action="show" id="${resource.eai}">${resource?.name}</g:link></td>
          <td>
            <g:each in="${resource.assets}" var="asset">
              <div><g:link action="show"
                           id="${asset.asset.eai}">${asset.asset.eai + ' - ' + asset.asset?.name}</g:link></div>
            </g:each>
          </td>
          <td>
            <g:each in="${resource.assetOf}" var="asset">
              <div><g:link action="show"
                           id="${asset.root.eai}">${asset.root.eai + ' - ' + asset.root?.name}</g:link></div>
            </g:each>
          </td>
          <td>
            <g:each in="${resource.contacts}" var="contact">
              <div><g:link action="show" id="${contact.ldap}"
                           controller="contact">${contact.ldap} - ${contact.name?.family}, ${contact.name?.given}</g:link>
              </div>
            </g:each>
          </td>
          <td>
            <g:each in="${resource.components}" var="component">
              <div><g:link action="show" controller="component"
                           id="${component.id}">[${component.type}] ${component.name}</g:link></div>
            </g:each>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </g:if>
</div>
</body>
</html>