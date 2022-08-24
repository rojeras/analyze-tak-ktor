<#-- @ftlvariable name="numOfContracts" type="Int" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <div>
        <h3>
            Sammanställning för ${plattform.platform}-${plattform.environment}
        </h3>
        <h4>Information om TAK-databasen</h4>
        <p>
            Antal tjänstekonsumenter: ${numOfConsumers}<br>
            Antal tjänsteproducenter: ${numOfProducers}<br>
            Antal tjänstekontrakt: ${numOfContracts}<br>
            Antal logiska adresser: ${numOfLogicalAddresses}<br>
            Antal anropsbehörigheter: ${numOfAuthorizations}<br>
            Antal vägval: ${numOfRoutings}<br>
        </p>
        <h4>Identifierade problem</h4>
        Antal tjänstekontrakt som inte ingår i någon anropsbehörighet: ${numOftkNotPartOfAuthorization}<br>
        Antal tjänstekontrakt som inte ingår i något vägval: ${numOftkNotPartOfRouting}<br>
        <hr>
        <p>
        </p>
    </div>
</@layout.header>