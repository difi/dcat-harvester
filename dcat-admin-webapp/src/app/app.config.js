var config = {
	backendUrl: function backend () {
		if (document.location.href.indexOf("localhost") > -1) {
			return "http://localhost:8090/";
		} else {
			return "http://demo.difi.no/dcat-harvester-app/"
		}
	}
};

export default config;