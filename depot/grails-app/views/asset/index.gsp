<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'asset.label', default: 'Asset')}" />
        <title>Assets</title>
    </head>
    <body>
        <a href="#list-asset" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="list-asset" class="content scaffold-list" role="main">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
            <table>
                <thead>
                <tr>
                    <g:sortableColumn property="eai" title="EAI"/>
                    <g:sortableColumn property="name" title="Name"/>
                    <th>Description</th> 
                    <th>Assets</th>
                    <th>Asset Of</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${assets}" var="asset">
                    <tr>
                        <td><g:link action="show" id="${asset.eai}">${asset.eai}</g:link></td>
                        <td><g:link action="show" id="${asset.eai}">${asset.name}</g:link></td>
                        <td>${asset.desc}</td>
                        <td>
                        <g:each in="${asset.assets}" var="a">
                            <div><g:link action="show" id="${a.eai}">${+a.eai+' - '+a.name}</g:link></div>
                        </g:each>
                        </td>
                        <td>
                        <g:each in="${asset.assetOf}" var="a">
                            <div><g:link action="show" id="${a.eai}">${+a.eai+' - '+a.name}</g:link></div>
                        </g:each>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </div>
    </body>
</html>
