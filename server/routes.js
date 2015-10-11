var mongoose = require('mongoose');
//mongoose.set('debug', true);
var myschemas = require('./mongoose_schemas.js');
var ObjectId = require('mongoose').Types.ObjectId; 

var fs = require('fs'),
    path = require('path'),
	serverfilename = "./logs/server.txt",
	pagefile = require('./modeler/js/tests/protractor.page.js'),
	myPage = pagefile.myPage,
	getDateStr = pagefile.getDateStr,
	getDate = pagefile.getDate,
	getTime = pagefile.getTime,
	errorCallback = pagefile.errorCallback,
	timestampToDate = pagefile.timestampToDate,
	mkdirParent = pagefile.mkdirParent;

var viewPaneModels = myschemas.viewPaneModel;
var dimensionModels = myschemas.dimensionModel;
var cellModels = myschemas.cellModel;
var cellCollectionModels = myschemas.cellCollectionModel;
var modelContainers = myschemas.modelContainer;
var collectionDimensionModels = myschemas.collectionDimensionModel;
var userModels = myschemas.userModel;

var serverLogFileName = "./logs/server.txt";
var serverLogFileName2 = "./logs/server2.txt";
var clientLogFileName = "./logs/client.txt";
var clientLogFileName2 = "./logs/client2.txt";

//require('./modeler/js/logger').consolelog;

var myroutes = function(app, passport){ 


	app.get('/tester', function(req, res) {
		console.log('GOT A REQUEST FOR /');
		res.sendFile('/roaring_bitmap/Tester.html', options);
	});

	app.get('/', function(req, res) {
		console.log('GOT A REQUEST FOR /');
		res.sendFile('/modeler/login.html', options);
	});

	app.post('/login', passport.authenticate('local-login', {
			successRedirect : '/modeler', // redirect to the secure profile section
			failureRedirect : '/login', // redirect back to the signup page if there is an error
			failureFlash : true // allow flash messages
		})
	);

	app.post('/signup', passport.authenticate('local-signup', {
		successRedirect : '/profile', // redirect to the secure profile section
		failureRedirect : '/signup', // redirect back to the signup page if there is an error
		failureFlash : true // allow flash messages
	}));

	app.get('/signup', function(req, res) {
		// render the page and pass in any flash data if it exists
		res.sendFile('/modeler/signup.html', options);
	});

	app.get('/map', function(req, res) {
		// render the page and pass in any flash data if it exists
		console.log('***** REQUEST TO MAP GRAPH *****');
		res.sendFile('/modeler/dep_graph.html', options);
	});

	app.get('/map2', function(req, res) {
		// render the page and pass in any flash data if it exists
		console.log('***** REQUEST TO MAP GRAPH *****');
		res.sendFile('/modeler/dep_graph_2.html', options);
	});

	app.get('/map3', function(req, res) {
		// render the page and pass in any flash data if it exists
		console.log('***** REQUEST TO MAP GRAPH *****');
		res.sendFile('/modeler/dep_graph_3.html', options);
	});

	app.get('/map4', function(req, res) {
		// render the page and pass in any flash data if it exists
		console.log('***** REQUEST TO MAP GRAPH *****');
		res.sendFile('/modeler/dep_graph_4.html', options);
	});

	app.get('/container', function(req, res) {
		// render the page and pass in any flash data if it exists
		res.sendFile('/modeler/container.html', options);
	});

	app.use("*", isLoggedIn);

	app.get('/modeler', function(req, res) {
		res.sendFile('/modeler/index.html', options);
	});

	app.post('/client/log/write/', function(req, res) {
		
		var log = req.body.myobjects;
		var len = log.length;
		console.log('***** RECEIVED CLIENT LOG *****');
		//console.log(req.body);
		
		for(var i = 0; i < len; i++){
			fs.appendFile(clientLogFileName, log[i]+'\n', function(err) {
				if(err) {
					console.log(err);
				} else {
					//console.log("The file was saved!");
				}
			}); 
		}
	});

	app.post('/client/log/flush/', function(req, res) {
		
		console.log(JSON.stringify(req.body));
		var reportDir = path.resolve(__dirname + req.body.myobjects.reportDir);
		var clienttestLogFileName = path.resolve(reportDir + '/client-' + req.body.myobjects.testName + '-log.txt');
		var servertestLogFileName = path.resolve(reportDir + '/server-' + req.body.myobjects.testName + '-log.txt');
		console.log('***** REQUEST TO FLUSH CLIENT LOG *****');
		
		if (!fs.existsSync(reportDir)) {
			mkdirParent(reportDir);
		}
		
		fs.renameSync(clientLogFileName, clientLogFileName2);
		fs.createReadStream(clientLogFileName2).pipe(fs.createWriteStream(clienttestLogFileName.split("\\").join("/")));

		fs.renameSync(serverLogFileName, serverLogFileName2);
		fs.createReadStream(serverLogFileName2).pipe(fs.createWriteStream(servertestLogFileName.split("\\").join("/")));

		//fs.appendFileSync(logFileName, msg + '\r\n', {encoding: 'utf8'}, function(){});
	});

	app.get('/template/get/:name', function(req, res) {
		//console.log('******OH REALLY!!!******');
		console.log('******GOT REQUEST FOR TEMPLATE******');
		console.log(req.url);
		console.log('/modeler/templates/'+req.params.name+'.html');
		res.sendFile('/modeler/templates/'+req.params.name+'.html', options);
	});

	app.post('/modelcontainer/new/', function(req, res) {
		//console.log("POST: ");
		//console.log(req.body);
		
		var modelcontainer = new modelContainers({
			name: req.body.name
		});
		
		modelcontainer.save(function (err) {
			if (!err) {
				return console.log("created");
			}
			else {
				return req + "\n\n" + err;
			}
		});

		res.jsonp(modelcontainer);
	});

	app.get('/settings/get/', function(req, res) {
		
		console.log('\n\n\n*******REQUESTED MY SETTINGS********\n\n\n')
		console.log(req.user);
		
		userModels.findOne({ 'local.email': req.user.local.email }).lean().exec(function (err, thisUser) {
			if (err) return console.error(err);
			if (!thisUser) return console.log('USER NOT FOUND');
			
			delete thisUser.local.password;
			res.jsonp(thisUser.local);
		});
		
	});

	app.get('/modelcontainer/get/id/:myid', function(req, res) {
		
		modelContainers.findOne({ 'id': req.params.myid }).lean().exec(function (err, modelContainer) {
			if (err) return console.error(err);
			res.jsonp(modelContainer);
			if(allmyModelContainers[modelContainer._id] == undefined){
				allmyModelContainers[modelContainer._id] = new ModelContainer();
			}
		});
		
	});

	app.get('/modelcontainer/get/name/:myname', function(req, res) {

		console.log('****** $$$ GOT REQUEST FOR MODEL $$$ ******');
		console.log('GOT A REQUEST FOR A MODEL CALLED ' + req.params.myname);
		modelContainers.findOne({ 'name': req.params.myname }).lean().exec(function (err, modelContainer) {
			if (err) return console.error(err);
			res.jsonp(modelContainer);
			if(allmyModelContainers[modelContainer._id] == undefined){
				allmyModelContainers[modelContainer._id] = new ModelContainer();
			}
		});
		
	});

	function update_properties(thisModel, properties){
		//console.log('PROPERTIES:');
		//console.log(properties);
		//console.log('\n\n');

		//console.log('THIS IS THE MODEL BEFORE UPDATING THE PROPERTIES:');
		//console.log(thisModel);
		//console.log('\n\n');

		for (var thisprop in properties){
			if(thisprop in thisModel){
				//console.log('THIS PROPERTY IS SET: ' + thisprop);
				thisModel[thisprop] = properties[thisprop];
				thisModel.save();
			}
			else{
				//console.log('THIS PROPERTY IS NOT SET: ' + thisprop);
			}
		}
		
		//console.log('THIS IS THE MODEL AFTER UPDATING THE PROPERTIES')
		//console.log(thisModel);
		//console.log('\n\n');
	}

	app.post('/collection/new/', function(req, res) {
		console.log("POST: ");
		//console.log(req.body);

		var cellcollection = new cellCollectionModels({
			name: req.body.name,
			model_id: req.body.model_id
		});
		
		cellcollection.save(function (err) {
			if (!err) {
				return console.log("created");
			}
			else {
				return console.log(err);
			}
		});

		res.jsonp(cellcollection);
	});

	app.post('/collection/update/name/:collection_name', function(req, res) {
		console.log("**** POST REQUEST TO UPDATE COLLECTION ****");
		//console.log("BODY: ")
		//console.log(req.body);
		console.log("URL: " + req.url);
		
		cellCollectionModels.findOne({ 'name': req.params.collection_name }).lean().exec(function (err, cellCollectionModel) {
			if (err) {
				return console.error(err);
			}
			else{
				update_properties(cellCollectionModel, req.body);
			}
		});
		
	});


	app.post('/viewpane/update', function(req, res) {
		console.log("**** POST REQUEST TO UPDATE VIEWPANE ****");
		//console.log("body: " + req.body);
		
		var thiscollection_id = new ObjectId(req.body.collection_id);
		var thisobject = req.body;
		
		viewPaneModels.findOne({ 'collection_id': thiscollection_id }).exec(function (err, viewPaneModel) {
			
			//console.log('DID WE FIND A VIEWPANE HERE?');
			//console.log(viewPaneModel);
		
			if (err || !viewPaneModel) {
				var thisViewPane = new viewPaneModels({
					collection_id: thisobject.thiscollection_id,
					DEFAULTCOLWIDTH : thisobject.DEFAULTCOLWIDTH,
					DEFAULTROWHEIGHT : thisobject.DEFAULTROWHEIGHT,
					VIEWCOLMAX : thisobject.VIEWCOLMAX,
					VIEWROWMAX : thisobject.VIEWROWMAX,

					view_col_dimensions : thisobject.view_col_dimensions,
					view_row_dimensions : thisobject.view_row_dimensions,

					//view_col_labels : thisobject.view_col_labels,
					//view_row_labels : thisobject.view_row_labels,
					//view_col_indices : thisobject.view_col_indices,
					//view_row_indices : thisobject.view_row_indices,

					START_VIEW_COL_INDEX : thisobject.START_VIEW_COL_INDEX,
					START_VIEW_ROW_INDEX : thisobject.START_VIEW_ROW_INDEX,

					SELECTED_ROW_INDEX : thisobject.SELECTED_ROW_INDEX,
					SELECTED_COL_INDEX : thisobject.SELECTED_COL_INDEX,

					SELECTED_VIEW_ROW_INDEX : thisobject.SELECTED_VIEW_ROW_INDEX,
					SELECTED_VIEW_COL_INDEX : thisobject.SELECTED_VIEW_COL_INDEX
				});
				
				thisViewPane.save(function (err) {
					if (!err) {
						//return console.log("created thisViewPane at id: " + thisViewPane.id);
					}
					else {
						return console.log(err);
					}
				});

				res.jsonp(thisViewPane);
			}
			else{
				//console.log('THIS IS THE MODEL WE FOUND: ');
				//console.log(viewPaneModel);
				req.body.collection_id = new ObjectId(req.body.collection_id);
				update_properties(viewPaneModel, req.body);
			}
		});	
	});

	app.post('/cell/update/', function(req, res) {
		
		var starttime = Date.now();
		console.log("**** POST REQUEST TO UPDATE A CELL ****");
		//console.log("BODY: ")
		//console.log(req.body);
		//console.log("URL: " + req.url);
		console.log("COLLECTION_ID: " + new ObjectId(req.body.collection_id));
		console.log("INDEX: " + req.body.index);
		
		//lean().
		cellModels.findOne({ 'index': parseInt(req.body.index), 'collection_id': new ObjectId(req.body.collection_id)}).exec(function (err, cellModel) {
			
			//console.log('DID WE FIND A CELLMODEL HERE?');
			//console.log(cellModel);
		
			if (err || !cellModel) {
				var thiscell = new cellModels({
					index: parseInt(req.body.index),
					collection_id: new ObjectId(req.body.collection_id),
					formula: req.body.formula,
					value: req.body.value
				});
				
				thiscell.save(function (err) {
					if (!err) {
						return console.log("created cell at index: " + thiscell.index);
					}
					else {
						return console.log(err);
					}
				});

				var endtime = Date.now();
				var proctime = (endtime - starttime);
				res.jsonp({thiscell: thiscell, proctime: proctime});
			}
			else{
				//console.log('THIS IS THE MODEL WE FOUND: ');
				//console.log(cellModel);
				req.body.collection_id = new ObjectId(req.body.collection_id);
				update_properties(cellModel, req.body);
			}
		});
		
	});

	function update_cell(myCell){
		console.log(myCell);
		
		myCell.index = parseInt(myCell.index);
		myCell.model_id =  new ObjectId(myCell.model_id);
		myCell.collection_id = new ObjectId(myCell.collection_id);
		var myformula = myCell.formula;
		var myvalue = myCell.value;
		var myv_cellindex = myCell.v_cellindex;
		//console.log('index: ' + myindex + ' , model_id: ' + mymodel_id + ' formula: ' + myformula + ' , value: ' + myvalue);
		
		var props_to_update = {};
		
		for(prop in myCell){
			props_to_update[prop] = myCell[prop];
		}
		
		console.log(props_to_update);
		//{'formula': myformula, 'value': myvalue, 'collection_id': mycollection_id, 'v_cellindex': myv_cellindex},
		
		cellModels.update(
			{ index: myCell.index, model_id: myCell.model_id}, 
			props_to_update,
			{upsert: true}, 
			function(err, myModel){
				if(err){
					console.log(err);
					return 0;
				}
				else{
					console.log("updated cell at index: " + myCell.index);
					update_dimension_for_cell(myCell.index, myCell.v_cellindex, myCell.collection_id, myCell.value);
					//myCube.update_measure(myindex, myvalue);
					console.log("updated dimensions for cell index: " + myCell.index);
					return 1;
				}
			}
		);

	}

	app.post('/cells/update/', function(req, res) {
		console.log("**** POST REQUEST TO UPDATE CELLS ****");

		var starttime = Date.now();
		console.log(req.body);
		var myobjects = req.body.myobjects;
		var arraylen = myobjects.length;
		console.log(arraylen);
		//var counter = 0;
		
		for(var i = 0; i < arraylen; i++){
			console.log(i);
			update_cell(myobjects[i]);
			if(i == (arraylen - 1)){
				var endtime = Date.now();
				var proctime = endtime - starttime;
				//res.jsonp({'counter' : arraylen, 'proctime' : proctime});
				res.jsonp({});
			}
		}
		
	});

	
	function graph_update(thisRelationship){
		var model_id =  thisRelationship.model_id;

		var thisModel = allmyModelContainers[model_id];
		thisModel.addChild(thisRelationship.parent, thisRelationship.child);
	}	


	app.post('/graph/update/', function(req, res) {
		console.log("**** POST REQUEST TO UPDATE GRAPH ****");

		console.log(req.body);
		var myobjects = req.body.myobjects;
		var arraylen = myobjects.length;
		console.log(arraylen);
		//var counter = 0;
		
		for(var i = 0; i < arraylen; i++){
			console.log(i);
			graph_update(myobjects[i]);
			if(i == (arraylen - 1)){
				res.jsonp({'counter' : arraylen});
			}
		}
		
	});

	function compute_cell(myCell){
		var cellindex = parseInt(myCell.index);
		var model_id =  myCell.model_id;
		var collection_id = myCell.collection_id;
		var formula = myCell.formula;
		var v_cellindex = myCell.v_cellindex;

		var thisModel = allmyModelContainers[model_id];
		thisModel.setup_cell_update(cellindex, formula, collection_id, v_cellindex);
		myCell.value = thisModel.evaluate_cell(cellindex, false);
		update_cell(myCell);
		//thisModel.table_values[cellindex] = this_value;
	}	


	app.post('/cells/compute/', function(req, res) {
		console.log("**** POST REQUEST TO COMPUTE CELLS ****");

		console.log(req.body);
		var myobjects = req.body.myobjects;
		var arraylen = myobjects.length;
		console.log(arraylen);
		//var counter = 0;
		
		for(var i = 0; i < arraylen; i++){
			console.log(i);
			compute_cell(myobjects[i]);
			if(i == (arraylen - 1)){
				res.jsonp({'counter' : arraylen});
			}
		}
		
	});

	function delete_cell(myCell){
		console.log('GOING TO DELETE CELL: ' + myCell.index);
		
		myCell.formula = "";
		myCell.value = 0;
		
		this.update_cell(myCell);
	}


	app.post('/cells/delete/', function(req, res) {
		console.log("**** POST REQUEST TO DELETE CELLS ****");

		//console.log(req.body);
		var myobjects = req.body.myobjects;
		var arraylen = myobjects.length;
		//console.log(arraylen);
		
		for(var i = 0; i < arraylen; i++){
			//console.log(i);
			delete_cell(myobjects[i]);
			if(i == (arraylen - 1)){
				res.jsonp({'counter' : arraylen});
			}
		}
		
	});

	app.get('/cells/get/modelcontainer/id/', function(req, res) {
		var returnstring = "";
		console.log('**** GOT A REQUEST FOR CELLS WITH MODEL ID *****');
		console.log(req.query.model_id);
		
		cellModels.find({ 'model_id': new ObjectId(req.query.model_id)}).sort('v_cellindex').lean().exec(function (err, cellModel) {
			if (err) return console.error(err);
			//console.log('CELLMODELS BEING RETURN:');
			//console.log(cellModel);
			res.jsonp(cellModel);
		});
		
	});

	app.get('/collection/get/name/', function(req, res) {
		var returnstring = "";
		
		cellCollectionModels.findOne({ 'name': req.query.name, 'model_id': req.query.model_id}).lean().exec(function (err, cellCollectionModel) {
			if (err) return console.error(err);
			res.jsonp(cellCollectionModel);
		});
		
	});

	app.get('/collection/get/id/', function(req, res) {
		var returnstring = "";
		console.log('GOT A REQUEST FOR A CELLCOLLECTION WITH ID: ' + req.query.id);
		//console.log(req);
		
		cellCollectionModels.findOne({ '_id': req.query.id}).lean().exec(function (err, cellCollectionModel) {
			if (err) return console.error(err);
			res.jsonp(cellCollectionModel);
		});
		
	});

	app.get('/dimensions/measure/get/', function(req, res) {
		console.log("**** GET REQUEST FOR EVALUATING CUBE EXPRESSION ****");
		console.log(req.query);
		var exp = req.query.expression;
		var returnobject = myCube.get_measure(exp)
		var measure = returnobject.measure;
		var parentcellindices = returnobject.parentcellindices;
		
		if(myCube.is_complete(exp)){
			var attr_indices = myCube.labels_2_indices(exp);
		}
		
		console.log('*** GOT MEASURE FOR THIS EXPRESSION ***');
		console.log(req.query);
		console.log('MEASURE IS : ' + measure);
		console.log('PARENT CELL INDICES ARE : ');
		console.log(parentcellindices);
		res.jsonp({
					'measure': measure,
					'indices': attr_indices,
					'parentcellindices': parentcellindices
				});
	});

	function update_dimension_string(dimension_string){
		console.log(dimension_string);
		var mydimensions = dimension_string.dimensions;
		var myindex = dimension_string.index;
		
		var dimension_array = mydimensions.split(".");
		if(dimension_array.length == myCube.dimensions.length){
			myCube.update_dimension(mydimensions, myindex);
		}
	}

	app.get('/dimensions/update', function(req, res) {
		console.log("**** POST REQUEST TO UPDATE DIMENSION STRING ****");

		console.log(req.body);
		var dimension_strings = req.body.dimension_strings;
		var arraylen = dimension_strings.length;
		console.log(arraylen);
		//var counter = 0;
		
		for(var i = 0; i < arraylen; i++){
			console.log(i);
			update_dimension_string(dimension_strings[i]);
			if(i == (arraylen - 1)){
				res.jsonp({'counter' : arraylen});
			}
		}
		
	});

	app.get('/dimensions/delete/all', function(req, res) {
		var returnstring = "";
		
		dimensionModels.remove();
		
	});	

	function update_dimension_for_cell(mcellindex, vcellindex, collection_id, myvalue){
	
		console.log('UPDATING DIMENSION FOR THIS CELL: ' + mcellindex + ' , vcellindex: ' + vcellindex);
		console.log('LOOKING FOR A CELLCOLLECTIONMODEL OF ID ' + collection_id);
	
		cellCollectionModels.findOne({ '_id': collection_id}).lean().exec(function (err, cellCollectionModel) {
			if (err) return console.error(err);
			else if(cellCollectionModel){
			
				//console.log('FOUND A CELLCOLLECTIONMODEL OF ID ' + collection_id);
				var thisrowmax = cellCollectionModel.ROWMAX;
				var rowindex = Math.floor(vcellindex / thisrowmax);

				collectionDimensionModels.find(
					{'collection_id': collection_id}, 
					function(err, allmyModels){	
						//console.log('FIRST FIND RESULT');
						//console.log(myModel);
						if(err){
							console.log('ERROR');
							return 0;
						}
						else if(allmyModels){
							//console.log('FOUND SEVERAL COLLECTIONDIMENSIONMODELS');
							//console.log(allmyModels);
							var len = allmyModels.length;
							for(i = 0; i < len; i++){
								var myModel = allmyModels[i];
								console.log('FOUND THIS MODEL');
								console.log(myModel);
								console.log('ROWINDEX: ' + rowindex);
								var attributeindex = myModel.row_indices[rowindex];
								var dimensionindex = myModel.dimensionindex;
								console.log('attributeindex: ' + attributeindex);
								console.log('dimensionindex: ' + dimensionindex);
								myCube.update_measure(mcellindex, myvalue);
								myCube.update_dimension(dimensionindex, attributeindex, mcellindex);
							}
						}
					}
				);
			}
		});
	}

	function set_dimension_for_cell(myRowObject, vcellindex){
		var collection_id = new ObjectId(myRowObject.collection_id);
		var dimensionindex =  parseInt(myRowObject.dimensionindex);
		var attributeindex = myRowObject.attributeindex;
		
		cellModels.findOne({ 'collection_id' : collection_id, 'v_cellindex': vcellindex}).lean().exec(function (err, cellModel) {
			if (err) return console.error(err);
			else if(cellModel){
				
				console.log('FOUND CELLMODEL with v_cellindex ' + vcellindex);
				console.log(cellModel);
				mcellindex = cellModel.index;
				console.log('UPDATING mcellindex: ' + mcellindex + ' WITH VALUE: ' + cellModel.value);
				myCube.update_measure(mcellindex, cellModel.value);
				myCube.update_dimension(dimensionindex, attributeindex, mcellindex);
			}
		});
	}

	function set_dimension_for_row(myRowObject){
		var collection_id = new ObjectId(myRowObject.collection_id);
		var dimensionindex =  parseInt(myRowObject.dimensionindex);
		var rowindex = parseInt(myRowObject.rowindex);
		var attributeindex = myRowObject.attributeindex;
		console.log('SETTING DIMENSION '+dimensionindex+' FOR THIS ROW ' + rowindex);
		
		cellCollectionModels.findOne({ '_id': collection_id}).lean().exec(function (err, cellCollectionModel) {
			if (err) return console.error(err);
			else if(cellCollectionModel){
			
				var thisrowmax = cellCollectionModel.ROWMAX;
				var startcellindex = (rowindex) * thisrowmax;
				var endcellindex = startcellindex + thisrowmax;
				console.log('FOUND CELLCOLLECTIONMODEL TO UPDATE ATTRIBUTE ' + attributeindex);
				console.log('startcellindex: ' + startcellindex + ' , endcellindex: ' + endcellindex);
				for(var vcellindex = startcellindex; vcellindex <= endcellindex; vcellindex++){
					console.log('vcellindex: ' + vcellindex + 'startcellindex: ' + startcellindex + ' , endcellindex: ' + endcellindex);
					set_dimension_for_cell(myRowObject, vcellindex);
				}

			}
		});
	}

	function update_dimension_indices(myRowObject){
		console.log(myRowObject);
		var collection_id = new ObjectId(myRowObject.collection_id);
		var dimensionindex =  parseInt(myRowObject.dimensionindex);
		var attributeindex = parseInt(myRowObject.attributeindex);
		var rowindex = parseInt(myRowObject.rowindex);
		//console.log(rowindex);

		collectionDimensionModels.findOne(
			{'collection_id': collection_id, 'dimensionindex': dimensionindex}, 
			function(err, myModel){	
				//console.log('FIRST FIND RESULT');
				//console.log(myModel);
				if(err){
					console.log('ERROR');
					return 0;
				}
				else if(!myModel){
					//console.log('COLLECTION DIMENSION MODEL COULD NOT BE FOUND');
					var thisModel = new collectionDimensionModels({
						'collection_id': collection_id,
						'dimensionindex': dimensionindex,
						row_indices: []
					});
				
					thisModel.row_indices.set(rowindex, attributeindex);
					thisModel.save(function (err, fluffy) {
						if(err){
							//console.log('DETECTED ERROR');	
							console.log(err);
							if(err.code == 11000){
								//console.log('**** DUPLICATE ERROR******');
								collectionDimensionModels.findOne(
									{'collection_id': collection_id, 'dimensionindex': dimensionindex}, 
									function(err,myModel){
										if(!err && myModel){
											//console.log(myModel);
											myModel.row_indices.set(rowindex, attributeindex);
											set_dimension_for_row(myRowObject);
											//myModel.markModified('row_indices');
											myModel.save(function (err, fluffy) {
												if(err){
													//console.log('DETECTED ERROR');	
													console.log(err);
												}
												else if(fluffy){
													console.log('COLLECTION DIMENSION MODEL SAVED AFTER DUPLICATE ERROR!');
													console.log('THIS IS MODEL');
													console.log(fluffy);
												}
												else{
													console.log('COULD NOT SAVE MODEL AFTER DUPLICATE ERROR');
												}
											});
											
										}
									}
								);

							}
						}
						else if(fluffy){
							console.log('COLLECTION DIMENSION MODEL SAVED!');
							set_dimension_for_row(myRowObject);
						}
					});
				}
				else{
					//console.log(myModel);
					//console.log(myModel.row_indices);
					//console.log(typeof(myModel.row_indices));
					myModel.row_indices.set(rowindex, attributeindex);
					set_dimension_for_row(myRowObject);
					myModel.save();
					console.log("updated rowindex at index: " + rowindex);
					console.log('attributeindex: ' + attributeindex);
					//console.log(myModel.row_indices);
					//console.log(myModel);
					return 1;
				}
			}
		);

	}

	app.post('/dimensions/indices/update/', function(req, res) {
		console.log("**** POST REQUEST FOR DIMENSIONS ****");

		console.log(req.body);
		var myobjects = req.body.myobjects;
		var arraylen = myobjects.length;
		//console.log(arraylen);
		//var counter = 0;
		
		for(var i = 0; i < arraylen; i++){
			//console.log(i);
			update_dimension_indices(myobjects[i]);
			if(i == (arraylen - 1)){
				res.jsonp({'counter' : arraylen});
			}
		}
	});


	app.get('/dimensions/indices/get/all/', function(req, res) {
		console.log("**** GET REQUEST FOR DIMENSIONS ****");
		//console.log('HAHAHAHAH');
		//console.log(req.body);
		var returnstring = "";
		console.log('**** GOT A REQUEST FOR DIMENSION INDICES FOR COLLECTION ID *****');
		console.log(req.query.collection_id);
		
		
		collectionDimensionModels.find({ 'collection_id': new ObjectId(req.query.collection_id)}).lean().exec(function (err, collectionDimensionModel) {
			if (err) return console.error(err);
			console.log('DIMENSION COLLECTION MODELS BEING RETURN:');
			console.log(collectionDimensionModel);
			res.jsonp(collectionDimensionModel);
		});
	});

	app.get('/dimensions/get/all', function(req, res) {
		
		dimensionModels.find().sort("position").lean().exec(function (err, dimensionModel) {
			if (err) return console.error(err);
			res.jsonp(dimensionModel);
		});
		
	});


	app.get('/profile', isLoggedIn, function(req, res) {
		res.sendFile('/modeler/profile.html', options); 
		//{
			//user : req.user // get the user out of session and pass to template
		//});
	});

	app.get('/logout', function(req, res) {
		console.log('GOT REQUEST TO LOG OUT');
		req.logout();
		res.sendFile('/modeler/login.html', options);
	});
	
	function isLoggedIn(req, res, next) {
		
		//console.log('CHECK WHETHER ONE IS console.logGED IN');
		//console.log(req.passport);
		//console.log('REQ.USER');
		//console.log(req.user);
		//console.log('next');
		//console.log(next.toString());
		
		if (req.isAuthenticated()){
			return next();
		}

		//res.redirect('/console.login');
		res.sendFile('/modeler/login.html', options);
	}

};



module.exports = myroutes;
