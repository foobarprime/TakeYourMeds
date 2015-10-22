var Entities = require('html-entities').XmlEntities;
entities = new Entities();
var pretty = require('js-object-pretty-print').pretty;
var mongoose = require('mongoose');
var myschemas = require('./mongoose_schemas.js');
var ObjectId = require('mongoose').Types.ObjectId; 

var userModels = myschemas.userModel;

var fs = require('fs');
var readline = require('readline');
var stream = require('stream');


var convert2ObjectId = function(string){
	return new ObjectId(string);
};


var convert2ObjectIds = function(arr){
	var len = arr.length;
	var newarr = [];
	for(i = 0; i < len; i++){
		newarr.push(convert2ObjectId(arr[i]._id));
	}
	return newarr;
};

module.exports.convert2ObjectId = convert2ObjectId;
module.exports.convert2ObjectIds = convert2ObjectIds;

module.exports.getAllArticles = function(callback){
	articleModels.find({}).lean().exec(function (err, foundArticles) {
		if (err) return console.error(err);
		callback(foundArticles);		
	});
};


var dateSortFunction = function(){
	return function(a, b) {
	    a = new Date(a.article_id.date_posted);
	    b = new Date(b.article_id.date_posted);
	    return a>b ? -1 : a<b ? 1 : 0;
	};
}

var registerUser = function(firstname, lastname, role, emailaddress, password, callback){
	userModels.findOne({ 'emailaddress': emailaddress }).exec(function (err, foundUser) {
		// console.log('SENT QUERY TO MONGO FOR ' + name + ' at ' + address);
		if (err) return console.error(err);
		if(!foundUser){

			var newUser = new userModels({
				firstname: firstname,
				lastname: lastname,
				role: role,
				emailaddress: emailaddress,
				password: password
			});

			newUser.save(function(err,data){
				if(err){
					console.log(err);
				}
				else{
					console.log('NEW USER PROFILE FOR ' + emailaddress + ' HAS BEEN CREATED');
					callback(data);
				}
			});


		}
		else{
			console.log('OLD USER PROFILE FOR ' + emailaddress + ' HAS BEEN FOUND');
			callback(foundUser);
		}
		
	});
};
module.exports.registerUser = registerUser;

var createEstablishment = function(name, address, reviewcount, rating, callback){
	establishmentModels.findOne({ 'name': name, 'address': address }).exec(function (err, foundEst) {
		// console.log('SENT QUERY TO MONGO FOR ' + name + ' at ' + address);
		if (err) return console.error(err);
		if(!foundEst){

			var parameters = {
				address: address
			};

			addressSearch(parameters, function (error, response) {
				if (error) throw error;

				if(response.results.length > 0){
					establishment_address = response.results[0].formatted_address;

				    var latitude = response.results[0].geometry.location.lat;
				    var longitude = response.results[0].geometry.location.lng;

					var newEstablishment = new establishmentModels({
						name: name,
						address: address,
						reviewcount: reviewcount,
						rating: rating,
						latitude: latitude,
						longitude: longitude
					});

					newEstablishment.save(function(err,data){
						if(err){
							console.log(err);
						}
						else{
							console.log('NEW ESTABLISHMENT FOR ' + name + ' HAS BEEN CREATED');
							callback(data);
						}
					});

				}
			});


		}
		else{
			console.log('OLD ESTABLISHMENT FOR ' + name + ' HAS BEEN FOUND');
			callback(foundEst);
		}
		
	});
};
module.exports.createEstablishment = createEstablishment;

var getEstablishments = function(criteria, callback){

	console.log('CRITERIA');
	console.log(criteria);

	establishmentModels.find(criteria).exec(function (err, foundEst) {
		console.log('SENT QUERY TO MONGO');


		if (err) return console.error(err);
		if(!foundEst){
		}
		else{
			console.log('OLD ESTABLISHMENTS HAS BEEN FOUND');
			console.log(foundEst);
			callback(foundEst);
		}
		
	});
};
module.exports.getEstablishments = getEstablishments;

var getCategory = function(category_name, callback){
	
	console.log('category_name: ' + category_name);

	categoryModels.findOne({category_value: category_name}).exec(function (err, foundCategory) {
		console.log('SENT QUERY TO MONGO');
		if (err) return console.error(err);
		if(!foundCategory){
		}
		else{
			console.log('CATEGORY HAS BEEN FOUND');
			console.log(foundCategory);
			callback(foundCategory);
		}
		
	});
};
module.exports.getCategory = getCategory;

var updateCategory = function(category, establishment_object, callback){
	var establishment_id = establishment_object._id
	// console.log('category: ' + category);
	// console.log('establishment_id: ' + establishment_id);
	// console.log('SENDING CATEGORY QUERY TO MONGO');

	categoryModels.findOneAndUpdate({'category_value' : label2param(category)}, {'category_label' : category, $addToSet : {'establishments' : convert2ObjectId(establishment_id)}}, {upsert: true}, function(err, foundCategory){
		console.log('err');
		console.log(err);
		console.log('foundCategory');
		console.log(foundCategory);
		// if(!foundCategory)
		// foundCategory.establishments.push(convert2ObjectId(establishment_id));
	});
};
module.exports.updateCategory = updateCategory;

var getZipcodes = function(state_abbr, callback){
	zipcodeModels.find({'state_abbr': state_abbr }).exec(function (err, foundGroup) {
		if (err) return console.error(err);
		// console.log('FOUND GROUP : ');
		// console.log(foundGroup);
		callback(foundGroup);
	});
};
module.exports.getZipcodes = getZipcodes;

var updateZipcodeData = function(data, dataset, callback){
	// console.log('SENDING CATEGORY QUERY TO MONGO');

	zipcodeModels.findOne({ 'zipcode': data.zipcode }).exec(function (err, foundGroup) {
		if (err) return console.error(err);
		if(!foundGroup){

			var newData = new zipcodeModels({	
				zipcode: data.zipcode
			});

			for(var key in data){
				newData[key] = data[key];
			}

			newData.save(function(err,data){
				if(err){
					console.log(err);
				}
				else{
					console.log('NEW zipcode: ' + data.zipcode + ' HAS BEEN CREATED');
					callback(data);
				}
			});
		}
		else{
			console.log('OLD zipcode FOR ' + data.zipcode  + ' HAS BEEN FOUND');


			for(var key in data){
				foundGroup[key] = data[key];
			}

			foundGroup.save(function(err,data){
				if(err){
					console.log(err);
				}
				else{
					console.log('OLD zipcode: ' + data.zipcode + ' HAS BEEN UPDATED');
					callback(data);
				}
			});
		}
	});
};
module.exports.updateZipcodeData = updateZipcodeData;

var updateCensusData = function(groupid, grouptype, data, dataset, callback){
	// console.log('SENDING CATEGORY QUERY TO MONGO');

	censusGroupModels.findOne({ 'group_id': groupid, 'group_type': grouptype }).exec(function (err, foundGroup) {
		if (err) return console.error(err);
		if(!foundGroup){

			var newData = new censusGroupModels({	
				group_id: groupid,
				group_type: grouptype,
				data_id: data._id,
				dataset: dataset
			});

			for(var key in data){
				newData[key] = data[key];
			}

			newData.save(function(err,data){
				if(err){
					console.log(err);
				}
				else{
					console.log('NEW GROUP FOR ' + groupid + ' of type: ' + grouptype + ' HAS BEEN CREATED');
					callback(data);
				}
			});
		}
		else{
			console.log('OLD GROUP FOR ' + groupid + ' HAS BEEN FOUND');


			for(var key in data){
				foundGroup[key] = data[key];
			}

			foundGroup.save(function(err,data){
				if(err){
					console.log(err);
				}
				else{
					console.log('NEW DATA FOR ' + groupid + ' of type: ' + grouptype + ' HAS BEEN CREATED');
					callback(data);
				}
			});
		}
	});
};
module.exports.updateCensusData = updateCensusData;

var getAllCensusData = function(grouptype, dataset, callback){
	// console.log('SENDING CATEGORY QUERY TO MONGO');

	censusGroupModels.find({'group_type': grouptype, 'dataset': dataset }).exec(function (err, foundGroup) {
		if (err) return console.error(err);
		// console.log('FOUND GROUP : ');
		// console.log(foundGroup);
		callback(foundGroup);
	});
};
module.exports.getAllCensusData = getAllCensusData;

var getCensusData = function(groupid, grouptype, dataset, callback){
	// console.log('SENDING CATEGORY QUERY TO MONGO');

	censusGroupModels.findOne({ 'group_id': groupid, 'group_type': grouptype, 'dataset': dataset }).exec(function (err, foundGroup) {
		if (err) return console.error(err);
		// console.log('FOUND GROUP : ');
		// console.log(foundGroup);
		callback(foundGroup);
	});
};
module.exports.getCensusData = getCensusData;

var getCensusDataFromArray = function(groupids, grouptype, dataset, callback){
	// console.log('SENDING CATEGORY QUERY TO MONGO');

	// console.log('GROUPIDS: ');
	// console.log(groupids);

	censusGroupModels.find({ 'group_id': {$in: groupids}, 'group_type': grouptype, 'dataset': dataset }).exec(function (err, foundGroup) {
		if (err) return console.error(err);
		// console.log('FOUND GROUP : ');
		// console.log(foundGroup);
		callback(foundGroup);
	});
};
module.exports.getCensusDataFromArray = getCensusDataFromArray;

var getCategories = function(callback){
	// console.log('SENDING CATEGORY QUERY TO MONGO');

	categoryModels.find().exec(function (err, foundModels) {
		if (err) return console.error(err);
		var categories = foundModels.map(function(elem){
			// console.log(elem);
			return {
				label: elem.category_label,
				value: elem.category_value
			};
		});
		// console.log('CATEGORIES');
		// console.log(categories);
		callback(categories);
	});
};
module.exports.getCategories = getCategories;

var getDataPoints = function(point){
	var datapoints = {
		male_under_5yrs: 'Male under 5 years',
		male_5to9yrs: 'Male - 5 to 9 years old',
		male_10to14yrs: 'Male - 10 to 14 years old',
		female_under_5yrs: 'Female under 5 years',
		female_5to9yrs: 'Female - 5 to 9 years old',
		female_10to14yrs: 'Female - 10 to 14 years old'
	};
	return datapoints;
};
module.exports.getDataPoints = getDataPoints;

var label2param = function(databaselabel){
	return databaselabel.toLowerCase().replace(' ', '_');
};
