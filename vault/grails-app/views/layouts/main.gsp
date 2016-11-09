<!doctype html>
<html lang="en" class="no-js">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
  <title>
    <g:layoutTitle default="Grails"/>
  </title>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>

  <asset:stylesheet src="application.css"/>

  <g:layoutHead/>
</head>

<body>

<div class="navbar navbar-default navbar-static-top" role="navigation">
  <div class="container">
    <div class="navbar-header">
      <a class="navbar-brand" href="/resource/">
        <g:message code="vault.title"/>
      </a>
    </div>
  </div>
</div>

<ul class="topnav">
  <li><g:link action="index" controller="resource"><g:message code="vault.label.resources"/></g:link></li>
  <li><g:link action="index" controller="contact"><g:message code="vault.label.contacts"/></g:link></li>
  <li><g:link action="index" controller="component"><g:message code="vault.title.components"/></g:link></li>
</ul>

<g:layoutBody/>

<div class="footer" role="contentinfo"></div>

<div id="spinner" class="spinner" style="display:none;">
  <g:message code="spinner.alt" default="Loading&hellip;"/>
</div>

<asset:javascript src="application.js"/>

</body>
</html>
