(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('SuperTypeController', SuperTypeController);

    SuperTypeController.$inject = ['$scope', '$state', 'SuperType', 'SuperTypeSearch'];

    function SuperTypeController ($scope, $state, SuperType, SuperTypeSearch) {
        var vm = this;
        vm.superTypes = [];
        vm.loadAll = function() {
            SuperType.query(function(result) {
                vm.superTypes = result;
            });
        };

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            SuperTypeSearch.query({query: vm.searchQuery}, function(result) {
                vm.superTypes = result;
            });
        };
        vm.loadAll();
        
    }
})();
