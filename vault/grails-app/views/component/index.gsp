<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Components</title>
</head>

<body>
<a href="#list-component" class="skip" tabindex="-1">Skip to content&hellip;</a>

<div class="nav" role="navigation">
  <ul>
    <li><g:link class="create" action="create"><g:message code="vault.title.createcomponent"/></g:link></li>
  </ul>
</div>

<div id="list-component" class="content scaffold-list" role="main">
  <g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
  </g:if>

  <g:if test="${components}">
    <table>
      <thead>
      <tr>
        <g:sortableColumn property="type" titleKey="vault.label.type"/>
        <g:sortableColumn property="name" titleKey="vault.label.name"/>
        <g:sortableColumn property="detail" titleKey="vault.label.detail"/>
        <th><g:link action="index" controller="resource"><g:message code="vault.label.componentof"/></g:link></th>
      </tr>
      </thead>
      <tbody>
      <g:each in="${components}" var="component">
        <tr>
          <td><g:link action="show" id="${component.id}">${component.type}</g:link></td>
          <td><g:link action="show" id="${component.id}">${component.name}</g:link></td>
          <td>${component.detail}</td>
          <td>
            <g:each in="${component.getComponentOf()}" var="resource">
              <div><g:link action="show" controller="resource"
                           id="${resource.eai}">${resource.eai} - ${resource.name}</g:link></div>
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