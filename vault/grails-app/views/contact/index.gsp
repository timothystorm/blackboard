<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'contact.label', default: 'Contact')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#list-contact" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="list-contact" class="content" role="main">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
            
            <table>
                <thead>
                    <tr>
                        <g:sortableColumn property="ldap" title="LDAP"/>
                        <g:sortableColumn property="name" title="Name"/>
                        <g:sortableColumn property="email" title="Email"/>
                        <th>Resources</th>
                    </tr>
                </thead>
                <tbody>
                    <g:each in="${contacts}" var="contact">
                        <tr>
                            <td><g:link action="show" id="${contact.ldap}">${contact.ldap}</g:link></td>
                            <td><g:link action="show" id="${contact.ldap}">${contact.name?.family}, ${contact.name?.given}</g:link></td>
                            <td><g:link action="show" id="${contact.ldap}">${contact.email}</g:link></td>
                            <td>
                            <g:each in="${contact.resources}" var="resource">
                                <div><g:link action="show" id="${resource.eai}" controller="resource">${resource.eai} - ${resource.name}</g:link></div>
                            </g:each>
                            </td>
                        </tr>
                    </g:each>                   
                </tbody>
            </table>
        </div>
    </body>
</html>