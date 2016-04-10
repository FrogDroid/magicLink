(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('ColorController', ColorController);

    ColorController.$inject = ['$scope', '$state', 'Color', 'ColorSearch'];

    function ColorController ($scope, $state, Color, ColorSearch) {
        var vm = this;
        vm.colors = [];
        vm.loadAll = function() {
            Color.query(function(result) {
                vm.colors = result;
            });
        };

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ColorSearch.query({query: vm.searchQuery}, function(result) {
                vm.colors = result;
            });
        };
        vm.loadAll();
        
    }
})();
