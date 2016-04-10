(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('TypeController', TypeController);

    TypeController.$inject = ['$scope', '$state', 'Type', 'TypeSearch'];

    function TypeController ($scope, $state, Type, TypeSearch) {
        var vm = this;
        vm.types = [];
        vm.loadAll = function() {
            Type.query(function(result) {
                vm.types = result;
            });
        };

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            TypeSearch.query({query: vm.searchQuery}, function(result) {
                vm.types = result;
            });
        };
        vm.loadAll();
        
    }
})();
