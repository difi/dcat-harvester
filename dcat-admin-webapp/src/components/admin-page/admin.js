import ko from 'knockout';
import adminTemplate from 'text!./admin.html';

class AdminViewModel {
    constructor(route) {
        this.message = ko.observable('Welcome to DCAT harvester admin!');
    }
    
    doSomething() {
        this.message('You invoked doSomething() on the viewmodel.');
    }
}

export default { viewModel: AdminViewModel, template: adminTemplate };