import ko from 'knockout';
import adminTemplate from 'text!./admin.html';
import DcatSource from './dcat-source';

class AdminViewModel {
	
    constructor(route) {
    	this.dcatSources = ko.observableArray();
        this.loadDcatSources(this.dcatSources);
    }
    
	loadDcatSources(list) {
		$.ajax({
			url: "http://localhost:8090/api/admin/dcat-sources"
		}).done(function(data) {
			console.log("data", data);
			for (var i = 0; i < data.length; i++) {
				var dcatSource = new DcatSource();
				dcatSource.update(data[i]);
				list.push(dcatSource);
			}
		});
	};
	
	
}

export default { viewModel: AdminViewModel, template: adminTemplate };