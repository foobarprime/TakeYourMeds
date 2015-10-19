console.log('BEGIN LOADING FILE');

var browseroutputarray = [];
var app = angular.module('cura', ['ngAnimate', 'ngRoute'], function($locationProvider){
	$locationProvider.html5Mode(true); //.hashPrefix('!');
});

app.config(function($routeProvider,$locationProvider) {
	$routeProvider.
		when("/edit",
			{ 
				templateUrl: "partials/edit.html"
			}
		).
		when("/medications",
			{ 
				templateUrl: "partials/medications.html"
			}
		).
		when("/appointments",
			{ 
				templateUrl: "partials/appointments.html"
			}
		).
		when("/alerts",
			{ 
				templateUrl: "partials/alerts.html"
			}
		).
		when("/signup",
			{ 
				templateUrl: "partials/signup.html"
			}
		).
		when("/login", 
			{ 
				templateUrl: "partials/login.html" 
			}
		).
		when("/home", 
			{ 
				templateUrl: "home.html" 
			}
		).
		otherwise( 
			{ 
				templateUrl: "partials/login.html" 
			}
		);

    $locationProvider.html5Mode(true);
});

app.controller('ViewController', function($scope, $location) {

		$scope.User = {};
		$scope.medications = {};
		console.log('$location.path: ' + $location.path());
	
		$scope.User.signup = function signup(){
			post_to_server_json_clean('/signup', User, function Usersignup_callback(return_object){

			});
		};

		$scope.getMedications = function(){
			get_from_server('/medications/all', function (return_object){
				// console.log(return_object);
				var medications = return_object.medications;
				// console.log(medications.length);

				for(var i = 0; i < medications.length; i++){
					var med = medications[i];
					console.log(med);
					var recur = med.recurrence[0].substr('RRULE:'.length);
					var rule = RRule.fromString(recur);
					console.log(rule);
					med.recur_text = rule.toText();
					$scope.$apply();
				}


				$scope.medications = medications;

				$scope.$apply();
			});
		};

		$scope.getMedications();
	
		// $(".expandable_label, .contractable_label").live("mousedown",function(event){
		// 	console.log('CLICKED ON A CONTRACTABLE OR EXPANDABLE LABEL');
		// 	$(this).toggleClass("expandable_label").toggleClass("contractable_label");
		// });
});

console.log('FINISH LOADING FILE');
