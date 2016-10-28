<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'asset.label', default: 'Asset')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#edit-asset" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="edit-asset" class="content scaffold-edit" role="main">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${this.asset}">
            <ul class="errors" role="alert">
                <g:eachError bean="${this.asset}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
            </g:hasErrors>
            <g:form resource="${this.asset}" method="PUT">
                <g:hiddenField name="version" value="${this.asset?.version}" />
                <fieldset class="form">
                    <div class="fieldcontain required">
                        <label for"eai">EAI</label><g:field type="number" name="eai" value="${asset?.eai}" required="true"/>
                    </div>
                    <div class="fieldcontain required">
                        <label for="name">Name</label><g:field type="text" name="name" value="${asset?.name}" required="true"/>
                    </div>
                    <div class="fieldcontain">
                        <label for="desc">Description</label><g:textArea name="desc" value="${asset?.desc}"/>
                    </div>
                    <div class="fieldcontain">
                        <label for="assets">Assets</label><g:select name="assets" from="${assets}" value="${asset?.assets*.id}" optionKey="id" optionValue="${{'('+it.eai+') '+it.name}}" multiple="true"/>
                    </div>
                    <div class="fieldcontain">
                        <label for="assetOf">Asset Of</label><g:select name="assetOf" from="${assetOf}" value="${asset?.assetOf*.id}" optionKey="id" optionValue="${{'('+it.eai+') '+it.name}}" multiple="true"/>
                    </div>
                </fieldset>
                <fieldset class="buttons">
                    <input class="save" type="submit" value="${message(code: 'default.button.update.label', default: 'Update')}" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
