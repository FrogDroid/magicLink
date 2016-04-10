(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('TypeDetailController', TypeDetailController);

    TypeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Type', 'Card'];

    function TypeDetailController($scope, $rootScope, $stateParams, entity, Type, Card) {
        var vm = this;
        vm.type = entity;
        vm.load = function (id) {
            Type.get({id: id}, function(result) {
                vm.type = result;
            });
        };
        var unsubscribe = $rootScope.$on('magicLinkApp:typeUpdate', function(event, result) {
            vm.type = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
