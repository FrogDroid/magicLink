(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('SuperTypeDetailController', SuperTypeDetailController);

    SuperTypeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'SuperType', 'Card'];

    function SuperTypeDetailController($scope, $rootScope, $stateParams, entity, SuperType, Card) {
        var vm = this;
        vm.superType = entity;
        vm.load = function (id) {
            SuperType.get({id: id}, function(result) {
                vm.superType = result;
            });
        };
        var unsubscribe = $rootScope.$on('magicLinkApp:superTypeUpdate', function(event, result) {
            vm.superType = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
