<#-- @ftlvariable name="numOfContracts" type="Int" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <div>
        <h3>
            Sammanställning för ${platformName}
        </h3>
        <h4>Information om TAK-databasen</h4>
        <p>
            Antal <a href="/tak/${platformName}/consumers">tjänstekonsumenter</a>: ${numOfConsumers}<br>
            Antal <a href="/tak/${platformName}/producers">tjänsteproducenter</a>: ${numOfProducers}<br>
            Antal <a href="/tak/${platformName}/contracts">tjänstekontrakt</a>: ${numOfContracts}<br>
            Antal <a href="/tak/${platformName}/logicaladdress">logiska adresser</a>: ${numOfLogicalAddresses}<br>
            <#--
            Antal <a href="/tak/${cpId}/authorizations">anropsbehörigheter</a>: ${numOfAuthorizations}<br>
            Antal <a href="/tak/${cpId}/routings">vägval</a>: ${numOfRoutings}<br>
        </p>
        <h4>Identifierade problem</h4>
        Antal <a href="/tak/${cpId}/tknotpartofauthorization">tjänstekontrakt som inte ingår i någon
            anropsbehörighet</a>: ${numOfTkNotPartOfAuthorization}<br>
        Antal <a href="/tak/${cpId}/tknotpartofrouting">tjänstekontrakt som inte ingår i något
            vägval</a>: ${numOfTkNotPartOfRouting}<br>
        Antal <a href="/tak/${cpId}/lanotpartofrouting">logiska adresser som inte ingår i något
            vägval</a>: ${numOfLaNotPartOfRouting} (0 indikerar bug i TAK-api)<br>
        Antal <a href="/tak/${cpId}/authorizationwithoutamatchingrouting">anropsbehörigheter som inte ingår i något
            vägval</a>: ${authorizationWithoutAMatchingRouting}<br>
        <hr>
        -->
        <p>
        </p>
    </div>
</@layout.header>