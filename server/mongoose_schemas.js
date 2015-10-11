var mongoose = require("mongoose");
var bcrypt   = require('bcrypt-nodejs');
var Schema = mongoose.Schema;
var db = mongoose.connection;
var ObjectId = require('mongoose').Types.ObjectId; 
mongoose.connect('mongodb://localhost:27017/curadb');

var viewPaneSchema = new Schema({

	collection_id: Schema.Types.ObjectId,
	DEFAULTCOLWIDTH : Number,
	DEFAULTROWHEIGHT : Number,
	VIEWCOLMAX : Number,
	VIEWROWMAX : Number,

	view_col_dimensions : [],
	view_row_dimensions : [],
	//view_col_labels : [],
	//view_row_labels : [],
	//view_col_indices : [],
	//view_row_indices : [],
	
	//THESE ARE THE EXACT COLUMN AND ROW INDICES OF THE TOP-LEFT
	//CELL IN THE VIEW PANE
	START_VIEW_COL_INDEX : Number,
	START_VIEW_ROW_INDEX : Number,

	//THESE ARE THE EXACT COLUMN AND ROW INDICES OF THE 
	//SELECTED/HIGHLIGHTED CELL IN THE VIEW PANE
	SELECTED_ROW_INDEX : Number,
	SELECTED_COL_INDEX : Number,

	//THESE ARE THE "VIEW" COLUMN AND ROW INDICES OF THE 
	//SELECTED/HIGHLIGHTED CELL IN THE VIEW PANE
	SELECTED_VIEW_ROW_INDEX : Number,
	SELECTED_VIEW_COL_INDEX : Number

});
module.exports.viewPaneModel = mongoose.model('viewPane', viewPaneSchema);

var collectionDimensionSchema = new Schema({
	collection_id: Schema.Types.ObjectId,
	dimensionindex:  Number,
	row_indices: []
});
collectionDimensionSchema.index({ collection_id: 1, dimensionindex: 1 }, {unique: true}); // schema level
module.exports.collectionDimensionModel = mongoose.model('collectionDimension', collectionDimensionSchema);

var attributeSchema = new Schema({
	name: String,
	index: Number
});
module.exports.attributeModel = mongoose.model('Attribute', attributeSchema);

var dimensionSchema = new Schema({
	name: String,
	attributes: [attributeSchema],
	bitmap_database: [],
	index: Number
});
module.exports.dimensionModel = mongoose.model('Dimension', dimensionSchema);

var cubeSchema = new Schema({
	dimensions: [dimensionSchema],
	cellindices: [],
	bitslicedmap: [],
	model_id: Schema.Types.ObjectId
});
module.exports.cubeModel = mongoose.model('Cube', cubeSchema);

var cellSchema = new Schema({
	index: Number,
	formula: String,
	value: 0,
	model_id: Schema.Types.ObjectId,
	collection_id: Schema.Types.ObjectId,
	v_cellindex: Number
});
cellSchema.index({ index: 1, model_id: 1 }, {unique: true}); // schema level
module.exports.cellModel = mongoose.model('Cell', cellSchema);

var cellCollectionSchema = new Schema({
	name: "",
	model_id: Schema.Types.ObjectId,
	column_labels: [],
	row_labels: [],
	column_indices: [],
	row_indices: [],
	column_dimensions: [],
	row_dimensions: [],
	COLMAX : Number,
	ROWMAX : Number
});
module.exports.cellCollectionModel = mongoose.model('CellCollection', cellCollectionSchema);

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