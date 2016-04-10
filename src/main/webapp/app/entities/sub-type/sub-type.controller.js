(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('SubTypeController', SubTypeController);

    SubTypeController.$inject = ['$scope', '$state', 'SubType', 'SubTypeSearch'];

    function SubTypeController ($scope, $state, SubType, SubTypeSearch) {
        var vm = this;
        vm.subTypes = [];
        vm.loadAll = function() {
            SubType.query(function(result) {
                vm.subTypes = result;
            });
        };

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            SubTypeSearch.query({query: vm.searchQuery}, function(result) {
                vm.subTypes = result;
            });
        };
        vm.loadAll();
        
    }
})();
