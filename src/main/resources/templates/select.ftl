<#-- @ftlvariable name="plattforms" type="kotlin.collections.List<com.example.models.ConnectionPoint>" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <div>
        <h3>Välj TAK (tjänsteplattform)</h3>
        <!--
        <form action="/tak" id="tpform" method="get">

        </form>

        <select id="tp" name="tpid" form="tpform" onchange="this.form.submit()">
            <option style="display: none">Välj</option>

            <#list plattforms as plattform>
                <#if plattform.id == cpId>
                    <option selected="selected" value="${plattform.id}">${plattform.platform}-${plattform.environment}</option>
                <#else >
                    <option value="${plattform.id}">${plattform.platform}-${plattform.environment}</option>
                </#if>

            </#list>
        </select>
        -->
        <#list plattforms as plattform>

            <a href="tak/${plattform.id}">${plattform.platform}-${plattform.environment}</a>
            <br>

        </#list>
    </div>

    <hr>
</@layout.header>