import ko from 'knockout';
import config from '../../app/app.config';

class DcatSource {
	
	constructor() {
		this.name = ko.observable();
		this.url = ko.observable();
		this.user = ko.observable();
	}
	
	update(data) {
		this.name(data.name);
		this.url(data.url);
		this.user(data.user);
	}
	
	harvest() {
		$.ajax({url: config.backendUrl() + "api/admin/harvest?name=" + this.name()});
	}
}

export default DcatSource;