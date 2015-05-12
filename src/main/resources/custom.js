$(document).ready(function(){

//	$("div").css("border", "3px solid red");

console.log( "juhu3");

console.log( "Bodysize: ", $("body").length);

console.log( "Body: ", $("body").rdf());




$('body').append( '<p id="info">This paper was written by <span rel="dc:creator" resource="#me"><span property="foaf:name">Ben Adida</span>.</span></p>');



$('#rdf').empty();
$('#rdf').append("<h1>Her er listen: </h1>");
$('#info')
  .rdf()
  .prefix('foaf', 'http://xmlns.com/foaf/0.1/')
  .where('?person a foaf:Person')
  .where('?person foaf:depiction ?picture')
  .optional('?person foaf:name ?name')
  .each(function () {
    console.log( "adding something ", this);
    var
      person = this.person.value,
      picture = this.picture.value,
      name = this.name === undefined ? '' : this.name.value;
    $('#rdf')
      .append('<li><a href="' + person + '"><img alt="' + name + '" src="' + picture + '" /></a></li>');
  });

$('#rdf').each( function(e) { console.log( "E: ", e)});

});
