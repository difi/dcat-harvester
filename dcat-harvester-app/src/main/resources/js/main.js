var ko = require('knockout');
var $ = require('jquery');
var DcatSource = require('./dcat-source');

var DcatSourcesViewModel = function DcatSourcesViewModel () {

		var self = this;
	
		self.dcatSources = ko.observableArray();
		
		self.get = function get() {
			$.ajax({
				url: "http://localhost:8090/api/admin/dcat-sources"
			}).done(function(data) {
				console.log("data", data);
				for (var i = 0; i < data.length; i++) {
					var dcatSource = new DcatSource();
					dcatSource.update(data[i]);
					self.dcatSources.push(dcatSource);	
				}
			});
		};
};

var dcatSourcesViewModel= new DcatSourcesViewModel();
ko.applyBindings(dcatSourcesViewModel);

dcatSourcesViewModel.get();