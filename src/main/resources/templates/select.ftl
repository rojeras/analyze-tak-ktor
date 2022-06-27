<#-- @ftlvariable name="plattforms" type="kotlin.collections.List<com.example.models.Plattform>" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <div>
        <h3>Välj TAK (tjänsteplattform)</h3>
        <#list plattforms as plattform>
            <div>
                <h3>
                    <a href="/articles/${plattform.id}">${plattform.platform}-${plattform.environment}</a>
                </h3>
                <p>
                    ${plattform.snapshotTime}
                </p>
            </div>
        </#list>
    </div>
</@layout.header>