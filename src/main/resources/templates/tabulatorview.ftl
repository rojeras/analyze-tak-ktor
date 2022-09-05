<!DOCTYPE html>
<html lang="en">
<head>
    <link href="https://unpkg.com/tabulator-tables@5.2.3/dist/css/tabulator.min.css" rel="stylesheet">
</head>
<body>
<div id="example-table"></div>

<script src="https://cdn.jsdelivr.net/npm/luxon/build/global/luxon.min.js"></script>
<script type="text/javascript" src="https://unpkg.com/tabulator-tables@5.2.3/dist/js/tabulator.min.js"></script>

<script type="text/javascript">
    //sample data
    var tabledata = [
        {id: 1, name: "Oli Bob", age: "12", col: "red", dob: "12/08/2017"},
        {id: 2, name: "Mary May", age: "1", col: "blue", dob: "14/05/1982"},
        {id: 3, name: "Christine Lobowski", age: "42", col: "green", dob: "22/05/1982"},
        {id: 4, name: "Brendon Philips", age: "125", col: "orange", dob: "01/08/1980"},
        {id: 5, name: "Margret Marmajuke", age: "16", col: "yellow", dob: "31/01/1999"},
    ];

    var table = new Tabulator("#example-table", {
        height: 200, // set height of table to enable virtual DOM
        data: tabledata, //load initial data into table
        layout: "fitColumns", //fit columns to width of table (optional)
        columns: [ //Define Table Columns
            {title: "Name", field: "name", sorter: "string", width: 150},
            {title: "Age", field: "age", sorter: "number", hozAlign: "left", formatter: "progress"},
            {title: "Favourite Color", field: "col", sorter: "string", headerSort: false},
            {title: "Date Of Birth", field: "dob", sorter: "date", hozAlign: "center"},
        ],
    });

    //trigger an alert message when the row is clicked
    table.on("rowClick", function (e, row) {
        alert("Row " + row.getIndex() + " Clicked!!!!");
    });
</script>
</body>
</html>