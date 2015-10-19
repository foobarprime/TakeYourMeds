var MongoMethods = require('./mongo_methods.js');
var GoogleCalendar = require('./google_calendar_api.js');

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
		res.sendFile('/index.html', options);
		// res.redirect('/login');
	});

	app.post('/login', passport.authenticate('local-login', {
			successRedirect : '/home', // redirect to the secure profile section
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
		res.sendFile('/index.html', options);
	});

	app.get('/home', function(req, res) {
		// render the page and pass in any flash data if it exists
		res.sendFile('/home.html', options);
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

	app.get('/medications/all', function(req, res) {
		console.log('GOT REQUEST FOR MEDICATIONS');

		GoogleCalendar.getMedications(function(medications){
			res.jsonp({'medications': medications});
		});
	});
	
	app.get('/appointments/all', function(req, res) {
		console.log('GOT REQUEST FOR MEDICATIONS');

		GoogleCalendar.getMedications(function(medications){
			res.jsonp({'appointments': medications});
		});
	});
	
	app.get('/alerts/all', function(req, res) {
		console.log('GOT REQUEST FOR MEDICATIONS');

		GoogleCalendar.getMedications(function(medications){
			res.jsonp({'alerts': medications});
		});
	});
	
	app.get('/logout', function(req, res) {
		console.log('GOT REQUEST TO LOG OUT');
		req.logout();
		res.sendFile('/login.html', options);
	});
	
	app.use("*", isLoggedIn);

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
		res.sendFile('/home.html', options);
	}

};



module.exports = myroutes;
