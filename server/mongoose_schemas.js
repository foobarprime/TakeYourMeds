var mongoose = require("mongoose");
var bcrypt   = require('bcrypt-nodejs');
var Schema = mongoose.Schema;
var db = mongoose.connection;
var ObjectId = require('mongoose').Types.ObjectId; 
mongoose.connect('mongodb://localhost:27017/curadb');

var modelContainerSchema = new Schema({
	name: "",
	collections: [],
	domain: String
});
module.exports.modelContainer = mongoose.model('ModelContainer', modelContainerSchema);

var userSchema = new Schema({
	local : {
		email: String,
		password: String,
		firstname: String,
		lastname: String,
		role: String,
		domain: String
	}
});

userSchema.methods.generateHash = function(password) {
    return bcrypt.hashSync(password, bcrypt.genSaltSync(8), null);
};

userSchema.methods.validPassword = function(password) {
    return bcrypt.compareSync(password, this.local.password);
};

userSchema.methods.generateDomain = function(email) {
    var thisarray = new String(email).split("@");
	if(thisarray.length == 2){
		return thisarray[1];
	}
	else{
		return "";
	}
};

module.exports.userModel = mongoose.model('UserModel', userSchema);