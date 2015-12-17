var ko = require('knockout');

var DcatSource = function DcatSource() {
	
	var self = this;
	
	self.name = ko.observable();
	self.url = ko.observable();
	self.user = ko.observable();
	
	self.update = function (data) {
		self.name(data.name);
		self.url(data.url);
		self.user(data.user);
	};
};

module.exports = DcatSource;