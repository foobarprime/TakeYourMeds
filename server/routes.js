var mongoose = require('mongoose');
//mongoose.set('debug', true);
var myschemas = require('./mongoose_schemas.js');
var ObjectId = require('mongoose').Types.ObjectId; 

var fs = require('fs'),
    path = require('path');
	// serverfilename = "./logs/server.txt",
	// pagefile = require('./modeler/js/tests/protractor.page.js'),
	// myPage = pagefile.myPage,
	// getDateStr = pagefile.getDateStr,
	// getDate = pagefile.getDate,
	// getTime = pagefile.getTime,
	// errorCallback = pagefile.errorCallback,
	// timestampToDate = pagefile.timestampToDate,
	// mkdirParent = pagefile.mkdirParent;

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


	var options = {
		root: __dirname + '/',
		dotfiles: 'deny',
		headers: {
			'x-timestamp': Date.now(),
			'x-sent': true
		}
	};
	
	app.get('/', function(req, res) {
		console.log('GOT A REQUEST FOR /');
		res.sendFile('/login.html', options);
	});

	app.post('/login', passport.authenticate('local-login', {
			successRedirect : '/', // redirect to the secure profile section
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
		res.sendFile('/signup.html', options);
	});

	app.use("*", isLoggedIn);

	app.get('/template/get/:name', function(req, res) {
		//console.log('******OH REALLY!!!******');
		console.log('******GOT REQUEST FOR TEMPLATE******');
		console.log(req.url);
		console.log('/modeler/templates/'+req.params.name+'.html');
		res.sendFile('/modeler/templates/'+req.params.name+'.html', options);
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


	app.get('/collection/get/id/', function(req, res) {
		var returnstring = "";
		console.log('GOT A REQUEST FOR A CELLCOLLECTION WITH ID: ' + req.query.id);
		//console.log(req);
		
		cellCollectionModels.findOne({ '_id': req.query.id}).lean().exec(function (err, cellCollectionModel) {
			if (err) return console.error(err);
			res.jsonp(cellCollectionModel);
		});
		
	});


	app.get('/dimensions/delete/all', function(req, res) {
		var returnstring = "";
		
		dimensionModels.remove();
		
	});	


	app.get('/dimensions/get/all', function(req, res) {
		
		dimensionModels.find().sort("position").lean().exec(function (err, dimensionModel) {
			if (err) return console.error(err);
			res.jsonp(dimensionModel);
		});
		
	});


	app.get('/profile', isLoggedIn, function(req, res) {
		res.sendFile('/profile.html', options); 
		//{
			//user : req.user // get the user out of session and pass to template
		//});
	});

	app.get('/logout', function(req, res) {
		console.log('GOT REQUEST TO LOG OUT');
		req.logout();
		res.sendFile('/login.html', options);
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
		res.sendFile('/login.html', options);
	}

};



module.exports = myroutes;
