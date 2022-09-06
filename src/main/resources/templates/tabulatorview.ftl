<!DOCTYPE html>
<html lang="en">
<head>
    <link href="/static/tabulator/css/tabulator.min.css" rel="stylesheet">
</head>
<body>
<div id="example-table"></div>

<script src="https://cdn.jsdelivr.net/npm/luxon/build/global/luxon.min.js"></script>

<script type="text/javascript" src="/static/tabulator/js/tabulator.min.js"></script>
<script type="text/javascript">

    <h3>
        ${heading}
    </h3>

    var table = new Tabulator("#example-table", {
            ajaxURL: "${ajaxUrl},
            // height: 200, // set height of table to enable virtual DOM
            layout: "fitColumns", //fit columns to width of table (optional)
            columns:
                [ //Define Table Columns
                    {title: "Id", field: "id", sorter: "number", width: 20},
                    {title: "HsaId", field: "hsaId", sorter: "string", hozAlign: "left"},
                    {title: "Beskrivning", field: "description", sorter: "string", headerSort: false}
                ],
        })
    ;

    //trigger an alert message when the row is clicked
    table.on("rowClick", function (e, row) {
        alert("Row " + row.getIndex() + " Clicked!!!!");
    });
</script>
</body>
</html>