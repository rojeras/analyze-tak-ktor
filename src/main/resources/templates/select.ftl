<#-- @ftlvariable name="plattforms" type="kotlin.collections.List<com.example.models.ConnectionPoint>" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <div>
        <h3>Välj TAK (tjänsteplattform)</h3>

        <form action="/tak" id="tpform" method="post">

        </form>

        <select id="tp" name="tpid" form="tpform" onchange="this.form.submit()">
        <option style="display: none" >Välj</option>

        <#list plattforms as plattform>

            <option value="${plattform.id}">${plattform.platform}-${plattform.environment}</option>

        </#list>
        </select>
    </div>

</@layout.header>