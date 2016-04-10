(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('SubTypeDetailController', SubTypeDetailController);

    SubTypeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'SubType', 'Card'];

    function SubTypeDetailController($scope, $rootScope, $stateParams, entity, SubType, Card) {
        var vm = this;
        vm.subType = entity;
        vm.load = function (id) {
            SubType.get({id: id}, function(result) {
                vm.subType = result;
            });
        };
        var unsubscribe = $rootScope.$on('magicLinkApp:subTypeUpdate', function(event, result) {
            vm.subType = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
