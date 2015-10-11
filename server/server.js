console.log('\n\n');

var mongoose = require('mongoose');
//mongoose.set('debug', true);
var myschemas = require('./mongoose_schemas.js');
var ObjectId = require('mongoose').Types.ObjectId; 

var express = require('express');
var cors = require('cors');
var bodyParser = require('body-parser');
var flash = require('connect-flash');
var app = express();
app.use( bodyParser.json() );       // to support JSON-encoded bodies
app.use( bodyParser.urlencoded({extended: true}) );
app.use(cors());

app.use("/json/",express.static(__dirname + '/json/'));
app.use("/images/",express.static(__dirname + '/images/'));
app.use("/stylesheets/",express.static(__dirname + '/stylesheets/'));
app.use("/js/",express.static(__dirname + '/js/'));
app.use("/",express.static(__dirname + '/html/'));

var cookieParser = require('cookie-parser');
app.use(cookieParser());

var passport = require('passport');
var expressSession = require('express-session');

require('./passport')(passport); 

app.use(expressSession({secret: 'mySecretKey' , resave: false }));
app.use(passport.initialize());
app.use(passport.session());
var LocalStrategy = require('passport-local').Strategy;
app.use(flash());
app.set('view engine', 'jade');


var myroutes = require("./routes.js")(app,passport);
app.listen(3002);
console.log('SERVER STARTED....LISTENING ON 3002');

