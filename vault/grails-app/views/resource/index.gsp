<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'resource.label', default: 'Resource')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#list-resource" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="list-resource" class="content scaffold-list" role="main">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
            
            <table>
                <thead>
                    <tr>
                        <g:sortableColumn property="eai" title="EAI"/>
                        <g:sortableColumn property="name" title="Name"/>
                        <g:sortableColumn property="detail.disposition" title="Disposition"/>
                        <th>Assets</th>
                        <th>Asset Of</th>
                    </tr>
                </thead>
                <tbody>
                    <g:each in="${resources}" var="resource">
                        <tr>
                            <td><g:link action="show" id="${resource.eai}">${resource?.eai}</g:link></td>
                            <td><g:link action="show" id="${resource.eai}">${resource?.name}</g:link></td>
                            <td>${resource?.detail?.disposition}</td>
                            <td>
                            <g:each in="${resource.assets}" var="a">
                                <div><g:link action="show" id="${a.asset.eai}">${a.asset.eai+' - '+a.asset.name}</g:link></div>
                            </g:each>
                            </td>
                            <td>
                            <g:each in="${resource.assetOf}" var="a">
                                <div><g:link action="show" id="${a.root.eai}">${a.root.eai+' - '+a.root.name}</g:link></div>
                            </g:each>
                            </td>
                        </tr>
                    </g:each>                   
                </tbody>
            </table>
        </div>
    </body>
</html>