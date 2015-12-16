var ko = require('knockout');
var $ = require('jquery');

var dcatSource = {
		
		get : function get() {
			$.ajax({
				url: "http://localhost:8090/api/admin/dcat-sources"
			}).done(function(data) {
				console.log("dcat sources", data);
			});
		}
};

dcatSource.get();