console.log('BEGIN LOADING FILE');

var myscope = null;
var myurl = "http://www.realmarketiq.com";

function extend(o,p){
	for(prop in p){
		o[prop] = p[prop];
	}
	return o;
}

function post(params, object, callback){
	//console.log(myurl+params);
	$.post(myurl+params, {}, function(data){}, "jsonp");
}

function post_to_server_json_clean(params, dataobject, callback) {
	console.log('**** POST REQUEST TO SERVER ****');
	console.log(myurl+params);
	console.log(dataobject);
	
	//console.log(JSON.stringify({ myobjects: dataobject }));
	//console.log(JSON.parse(JSON.stringify({ myobjects: dataobject })));

	var jsonobjs = JSON.stringify({myobjects: dataobject});
	console.log('CHANGES MADE');
	var sent_time = performance.now();
	
	$.ajax({ 
		type: "POST",
		url: myurl+params,
		dataType: "json",
		data: jsonobjs,
		contentType: 'application/json',
		success: function(jsonobject){
			var rec_time = performance.now();
			console.oldlog('received answer to: ' + params);
			console.oldlog('time taken to receive answer:');
			console.oldlog(rec_time - sent_time);

			console.oldlog('success');
			console.oldlog(jsonobject);
			callback(jsonobject);
		},
		error: function (jqXHR, textStatus, errorThrown){
			console.log('failure');
			console.log(jqXHR);
			console.log(textStatus);
			console.log(errorThrown);
			return errorThrown;
		}

	});
}


function post_to_server(params, dataobject, callback) {
	//console.log('**** POST REQUEST TO SERVER ****');
	//console.log(myurl+params);
	//console.log(dataobject);
	
	$.ajax({ 
	   type: "POST",
	   dataType: "json",
	   url: myurl+params,
	   data: dataobject,
	   success: function(jsonobject){
			//console.log('success');
			//console.log(jsonobject);
			callback(jsonobject);
	   },
	   error: function (jqXHR, textStatus, errorThrown){
			console.log('failure');
			console.log(jqXHR);
			console.log(textStatus);
			console.log(errorThrown);
			return errorThrown;
	   }

	});
}

function get_from_server(params, callback) {

	console.log('**** GET REQUEST TO SERVER ****');
	console.log('params');
	console.log(params);
	console.log(myurl+params);

	// console.log('jquery object');
	// console.log($.ajax);

	$.ajax({ 
	   type: "GET",
	   dataType: "jsonp",
   	   url: myurl+params,
	   success: function(jsonobject){
			console.log('success with:');
			console.log(myurl+params);
			console.log(jsonobject);
			callback(jsonobject);
	   },
	   error: function (jqXHR, textStatus, errorThrown){
			console.log('failure with:');
			console.log(myurl+params);
			console.log(jqXHR);
			console.log(textStatus);
			console.log(errorThrown);
			return errorThrown;
	   }
	})
	// .done(function(jsonobject) {
	// 	console.log( "success" );
	// 	console.log(myurl+params);
	// 	callback(jsonobject);
	// })
	// .fail(function() {
	// 	console.log( "error" );
	// 	console.log(myurl+params);
	// })
	// .always(function() {
	// 	console.log( "complete" );
	// 	console.log(myurl+params);
	// });
}

function unfocus(el,hiddenel) {
	el.focusout();
	$('#formula_editor').blur();
	$('#formula_editor').focusout();
	$('#hidden_output').focus(); //trigger('dblclick');
	if (typeof window.getSelection != "undefined"
			&& typeof document.createRange != "undefined") {
		//console.log('createrange = defined');
		var sel = window.getSelection();
		sel.removeAllRanges();
		//sel.addRange(range);
	} else if (typeof document.body.createTextRange != "undefined") {
		//console.log('createtextrange = defined');
		var textRange = document.body.createTextRange();
		textRange.moveToElementText(hiddenel);
		textRange.collapse(false);
		textRange.select();
	}
}

function placeCaretAtEnd(el) {
	el.focus();
	//console.log(el);
	if (typeof window.getSelection != "undefined"
			&& typeof document.createRange != "undefined") {
		//console.log('createrange = defined');
		var range = document.createRange();
		range.selectNodeContents(el);
		range.collapse(false);
		var sel = window.getSelection();
		sel.removeAllRanges();
		sel.addRange(range);
	} else if (typeof document.body.createTextRange != "undefined") {
		//console.log('createtextrange = defined');
		var textRange = document.body.createTextRange();
		textRange.moveToElementText(el);
		textRange.collapse(false);
		textRange.select();
	}
}

function getKeyChar(evt){
	var charCode = (evt.which) ? evt.which : event.keyCode
	var returnCode = String.fromCharCode(charCode);
	if (charCode == 8) returnCode = "backspace"; //  backspace
	if (charCode == 9) returnCode = "tab"; //  tab
	if (charCode == 13) returnCode = "enter"; //  enter
	if (charCode == 16) returnCode = "shift"; //  shift
	if (charCode == 17) returnCode = "ctrl"; //  ctrl
	if (charCode == 18) returnCode = "alt"; //  alt
	if (charCode == 19) returnCode = "pause/break"; //  pause/break
	if (charCode == 20) returnCode = "caps lock"; //  caps lock
	if (charCode == 27) returnCode = "escape"; //  escape
	if (charCode == 33) returnCode = "page up"; // page up, to avoid displaying alternate character and confusing people             
	if (charCode == 34) returnCode = "page down"; // page down
	if (charCode == 35) returnCode = "end"; // end
	if (charCode == 36) returnCode = "home"; // home
	if (charCode == 37) returnCode = "left arrow"; // left arrow
	if (charCode == 38) returnCode = "up arrow"; // up arrow
	if (charCode == 39) returnCode = "right arrow"; // right arrow
	if (charCode == 40) returnCode = "down arrow"; // down arrow
	if (charCode == 45) returnCode = "insert"; // insert
	if (charCode == 46) returnCode = "delete"; // delete
	if (charCode == 91) returnCode = "left window"; // left window
	if (charCode == 92) returnCode = "right window"; // right window
	if (charCode == 93) returnCode = "select key"; // select key
	if (charCode == 96) returnCode = "numpad 0"; // numpad 0
	if (charCode == 97) returnCode = "numpad 1"; // numpad 1
	if (charCode == 98) returnCode = "numpad 2"; // numpad 2
	if (charCode == 99) returnCode = "numpad 3"; // numpad 3
	if (charCode == 100) returnCode = "numpad 4"; // numpad 4
	if (charCode == 101) returnCode = "numpad 5"; // numpad 5
	if (charCode == 102) returnCode = "numpad 6"; // numpad 6
	if (charCode == 103) returnCode = "numpad 7"; // numpad 7
	if (charCode == 104) returnCode = "numpad 8"; // numpad 8
	if (charCode == 105) returnCode = "numpad 9"; // numpad 9
	if (charCode == 106) returnCode = "multiply"; // multiply
	if (charCode == 107) returnCode = "add"; // add
	if (charCode == 109) returnCode = "subtract"; // subtract
	if (charCode == 110) returnCode = "decimal point"; // decimal point
	if (charCode == 111) returnCode = "divide"; // divide
	if (charCode == 112) returnCode = "F1"; // F1
	if (charCode == 113) returnCode = "F2"; // F2
	if (charCode == 114) returnCode = "F3"; // F3
	if (charCode == 115) returnCode = "F4"; // F4
	if (charCode == 116) returnCode = "F5"; // F5
	if (charCode == 117) returnCode = "F6"; // F6
	if (charCode == 118) returnCode = "F7"; // F7
	if (charCode == 119) returnCode = "F8"; // F8
	if (charCode == 120) returnCode = "F9"; // F9
	if (charCode == 121) returnCode = "F10"; // F10
	if (charCode == 122) returnCode = "F11"; // F11
	if (charCode == 123) returnCode = "F12"; // F12
	if (charCode == 144) returnCode = "num lock"; // num lock
	if (charCode == 145) returnCode = "scroll lock"; // scroll lock
	if (charCode == 186) returnCode = ";"; // semi-colon
	if (charCode == 187) returnCode = "="; // equal-sign
	if (charCode == 188) returnCode = ","; // comma
	if (charCode == 189) returnCode = "-"; // dash
	if (charCode == 190) returnCode = "."; // period
	if (charCode == 191) returnCode = "/"; // forward slash
	if (charCode == 192) returnCode = "`"; // grave accent
	if (charCode == 219) returnCode = "["; // open bracket
	if (charCode == 220) returnCode = "\\"; // back slash
	if (charCode == 221) returnCode = "]"; // close bracket
	if (charCode == 222) returnCode = "'"; // single quote

	return returnCode;
}

function clone_properties(origModel, newModel){
	for (var thisprop in origModel){
		if(thisprop in newModel){
			//console.log('THIS PROPERTY IS SET: ' + thisprop);
			newModel[thisprop] = origModel[thisprop];
		}
		else{
			//console.log('THIS PROPERTY IS NOT SET: ' + thisprop);
		}
	}
	//console.log('THIS IS THE MODEL AFTER UPDATING THE PROPERTIES')
	//console.log(newModel);
	//console.log('\n\n');
}

console.log('FINISH LOADING FILE');

	
function consoleprint(thisobj, objname){
	console.log('\n\n****PRINTING ' + objname + '******');
	console.log(thisobj);
	console.log(thisobj.length);
	console.log('****PRINTING ' + objname + '******\n\n');
}
