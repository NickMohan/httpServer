//First gotta get JSON from php
//This function automatically runs with JQuery
$(function() { 


	var dataJSON;
	$.getJSON('JSONConverter.php', data, function(jsonData) {
	  	dataJSON = jsonData; 
	});       

	var temp = JSON.parse(dataJSON);


	document.getElementById("test").innerHTML = temp.items[1].path;











	var text = '{ "employees" : [' +
		'{ "firstName":"John" , "lastName":"Doe" },' +
		'{ "firstName":"Anna" , "lastName":"Smith" },' +
		'{ "firstName":"Peter" , "lastName":"Jones" } ]}';
	//None of this works fix it
	var obj = JSON.parse(text);

	


	//$.get('JSONConverter.php', function(data) {

	//var response = [data]
	/*var dataJSON;
	$.getJSON('JSONConverter.php', data, function(jsonData) {
	  	dataJSON = jsonData; 
	});       

	var temp = JSON.parse(dataJSON);
	*/
	document.getElementById("test").innerHTML = //temp.items[1].path;
	obj.employees[1].firstName + " " + obj.employees[1].lastName;
});

