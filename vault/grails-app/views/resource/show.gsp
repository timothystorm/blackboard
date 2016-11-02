<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'resource.label', default: 'Resource')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#show-resource" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="show-resource" class="content scaffold-show" role="main">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
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
                <div class="property-value">${resource?.name}</div>
              </li>
              <li class="fieldcontain">
                <span class="property-label">Assets</span>
                <div class="property-value">
                  <g:each in="${resource?.assets}" var="a">
                    <div><g:link action="show" id="${a.asset.eai}">${a.asset.eai+' - '+a.asset.name}</g:link></div>
                  </g:each>
                </div>
              </li>
              <li class="fieldcontain">
                <span class="property-label">Asset Of</span>
                <div class="property-value">
                  <g:each in="${resource?.assetOf}" var="a">
                    <div><g:link action="show" id="${a.root.eai}">${a.root.eai+' - '+a.root.name}</g:link></div>
                  </g:each>
                </div>
              </li>
            </ol>
            <g:form resource="${resource}" method="DELETE" id="${resource?.eai}">
                <fieldset class="buttons">
                    <g:link class="edit" action="edit" resource="${resource}" id="${resource.eai}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                    <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
