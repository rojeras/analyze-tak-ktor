<#-- @ftlvariable name="numOfContracts" type="Int" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <div>
        <h3>
            ${heading}
        </h3>

        <#list plattforms as plattform>
            <#if plattform.id == cpId>
                <option selected="selected" value="${plattform.id}">${plattform.platform}-${plattform.environment}</option>
            <#else >
                <option value="${plattform.id}">${plattform.platform}-${plattform.environment}</option>
            </#if>

        </#list>
        <hr>
        <p>
        </p>
    </div>
</@layout.header>