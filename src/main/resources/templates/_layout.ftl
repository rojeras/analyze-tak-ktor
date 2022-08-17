<#-- @ftlvariable name="plattforms" type="kotlin.collections.List<com.example.models.ConnectionPoint>" -->
<#macro header>
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <title>TAK-information</title>
    </head>
    <body style="text-align: center; font-family: sans-serif">
    <!-- <img src="/static/ktor_logo.png"> -->
    <h1>Utforska en tjänsteadresseringskatalog (TAK)</h1>
    <hr>

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

    <#nested>

    </body>
    </html>
</#macro>