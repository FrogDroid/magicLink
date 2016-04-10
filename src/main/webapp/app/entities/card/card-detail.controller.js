(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('CardDetailController', CardDetailController);

    CardDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Card', 'SuperType', 'Type', 'SubType', 'Color', 'CardCollection'];

    function CardDetailController($scope, $rootScope, $stateParams, entity, Card, SuperType, Type, SubType, Color, CardCollection) {
        var vm = this;
        vm.card = entity;
        vm.load = function (id) {
            Card.get({id: id}, function(result) {
                vm.card = result;
            });
        };
        var unsubscribe = $rootScope.$on('magicLinkApp:cardUpdate', function(event, result) {
            vm.card = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
