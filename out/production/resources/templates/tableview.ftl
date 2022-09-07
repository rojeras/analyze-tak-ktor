<#ftl auto_esc=false>
<#import "_layout.ftl" as layout />
<@layout.header>
    <style>
        table, td, th {
            border: 1px solid black;
            text-align: left;
        }

    </style>
    <div>
        <h3>
            ${heading}
        </h3>

        <table>
            <#list tableHeadings as ths>
                <th>${ths}</th>
            </#list>

            <#list viewData as viewLine>
                <tr>
                    <#list viewLine as tds>
                        <td>${tds}</td>
                    </#list>
                </tr>

            </#list>
        </table>
        <hr>
        <p>
        </p>
    </div>
</@layout.header>