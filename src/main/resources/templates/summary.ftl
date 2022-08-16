<#-- @ftlvariable name="numOfContracts" type="Int" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <div>
        <h3>
            Summanställning
        </h3>
        <p>
            Antal tjänstekonsumenter: ${numOfConsumers}<br>
            Antal tjänsteproducenter: ${numOfProducers}<br>
            Antal tjänstekontrakt: ${numOfContracts}<br>
            Antal logiska adresser: ${numOfLogicalAddresses}
        </p>
        <hr>
        <p>
        </p>
    </div>
</@layout.header>