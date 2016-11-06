<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Components</title>
</head>

<body>
<a href="#list-component" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
  <ul>
    <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    <li><g:link class="create" action="create">Create Component</g:link></li>
  </ul>
</div>

<div id="list-component" class="content scaffold-list" role="main">
  <h1>Component List</h1>
  <g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
  </g:if>

  <g:if test="${components}">
    <table>
      <thead>
      <tr>
        <g:sortableColumn property="type" title="Type"/>
        <g:sortableColumn property="name" title="Name"/>
        <g:sortableColumn property="detail" title="Detail"/>
        <th><g:link action="index" controller="resource">Component Of</g:link></th>
      </tr>
      </thead>
      <tbody>
      <g:each in="${components}" var="component">
        <tr>
          <td><g:link action="show" id="${component.id}">${component.type}</g:link></td>
          <td><g:link action="show" id="${component.id}">${component.name}</g:link></td>
          <td>${component.detail}</td>
          <td>
            <g:each in="${component.componentOf()}" var="resource">
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