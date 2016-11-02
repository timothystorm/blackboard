<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'resource.label', default: 'Resource')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#create-resource" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="create-resource" class="content scaffold-create" role="main">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${resource}">
            <ul class="errors" role="alert">
                <g:eachError bean="${resource}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
            </g:hasErrors>
            <g:form action="save">
                <fieldset class="form">
                  <div class="fieldcontain required">
                    <label for="eai">EAI</label><g:field type="number" name="eai" value="${resource?.eai}" required="true" />
                  </div>
                  <div class="fieldcontain required">
                    <label for="name">Name</label><g:field type="text" name="name" value="${resource?.name}" required="true"/>
                  </div>
                  <div class="fieldcontain required">
                    <label for="detail.disposition">Disposition</label><g:select name="detail.disposition" from="${org.storm.vault.Disposition.values()}" value="${resource?.detail?.disposition}"/>
                  </div>
                  <g:if test="${assets}">
                      <div class="fieldcontain">
                        <label for="assets">Assets</label><g:select name="assets" from="${assets}" optionKey="eai" optionValue="${{it.eai+' - '+it.name}}" multiple="true" size="${Math.max(Math.min(3, assets?.size()), 10)}" />
                      </div>
                  </g:if>
                  <div class="fieldcontain">
                    <label for="detai.desc">Description</label><g:textArea name="detail.desc" value="${resource?.detail?.desc}" rows="5" cols="40"/>
                  </div>
                </fieldset>
                <fieldset class="buttons">
                    <g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
