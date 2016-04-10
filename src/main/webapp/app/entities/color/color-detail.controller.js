(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('ColorDetailController', ColorDetailController);

    ColorDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Color', 'Card'];

    function ColorDetailController($scope, $rootScope, $stateParams, entity, Color, Card) {
        var vm = this;
        vm.color = entity;
        vm.load = function (id) {
            Color.get({id: id}, function(result) {
                vm.color = result;
            });
        };
        var unsubscribe = $rootScope.$on('magicLinkApp:colorUpdate', function(event, result) {
            vm.color = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
