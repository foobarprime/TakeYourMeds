// var pretty = require('js-object-pretty-print').pretty;
/****

{
 "name": "flare",
 "children": [
  {
   "name": "analytics",
   "children": [
    {
     "name": "cluster",
     "children": [
      {"name": "AgglomerativeCluster", "size": 3938},
      {"name": "CommunityStructure", "size": 3812},
      {"name": "HierarchicalCluster", "size": 6714},
      {"name": "MergeEdge", "size": 743}
     ]
    },

****/

var myTree = function(){

	var treeNodes = ["Root"];

	var childrenSet = [[]];

	this.printTree = function(){

		var MotherNode = {
			'name': "MotherShip",
			"children" : []
		};

		this.printNode("Root", MotherNode);

		return MotherNode;

	};


	this.printNode = function(name, parentNode){

		var thisNode = this.getNodefromName(name);
		
		var myNodePrint = {
			"name" : name,
			"children" : []
		};

		parentNode.children.push(myNodePrint);
		
		var myChildNodes = this.getChildNodes(name);

		for(var i = 0; i < myChildNodes.length; i++){
			this.printNode(myChildNodes[i], myNodePrint);
		}

	};

	this.getNodefromName = function(name){
		return treeNodes[this.addNode(name)];
	};

	this.getChildNodes = function(name){
		var myindex = this.addNode(name);
		var myChildIndices = childrenSet[myindex];

		var myChildNodes = myChildIndices.map(function(elem){
			return treeNodes[elem];
		});

		return myChildNodes;
	};

	this.addNode = function(node){
		if(treeNodes.indexOf(node) == -1){
			treeNodes.push(node);
			childrenSet.push([]);
			this.addChild("Root", node);
			return treeNodes.length - 1;
		}
		else{
			return treeNodes.indexOf(node);
		}
	};

	this.removeChild = function(parent, child){
		var parentindex = this.addNode(parent);
		var childindex = this.addNode(child);

		var mychildren = childrenSet[parentindex];
		var myindex = mychildren.indexOf(childindex);

		if(myindex != -1){
			mychildren.splice(myindex,1);
		}
		else{
			return 0;
		}
	};

	this.addChild = function(parent, child){
		var parentindex = this.addNode(parent);
		var childindex = this.addNode(child);

		var mychildren = childrenSet[parentindex];
		// console.log('mychildren');
		// console.log(mychildren);

		var myindex = mychildren.indexOf(childindex);

		if(parent != "Root"){
			this.removeChild("Root", child);
		}

		if(myindex != -1){
			return myindex;
		}
		else{
			mychildren.push(childindex);
		}
	};

	this.hasParent = function(child){
		var childindex = this.addNode(child);
		var found = false;

		for(var i = 0; i < childrenSet; i++){
			var inhere = childrenSet.indexOf(child);
			if(inhere != -1){
				return true;
			}
			if(i == childrenSet.length - 1){
				return false;
			}
		}
	};

	// this.addRootChildren = function(){
	// 	for(var i = 0; i < treeNodes; i++){
	// 		var thisNode = treeNodes[i];
	// 		if(!this.hasParent(thisNode)){
	// 			this.addChild("Root", thisNode);
	// 		}
	// 		else{
	// 			this.removeChild("Root", thisNode);
	// 		}
	// 	}
	// };

};

// var thisTree = new myTree();
// thisTree.addChild('hello', 'how');
// thisTree.addChild('how', 'are');
// thisTree.addChild('are', 'you');
// thisTree.addChild('you', '?');
// var motherTree = thisTree.printTree();
// console.log(pretty(motherTree));