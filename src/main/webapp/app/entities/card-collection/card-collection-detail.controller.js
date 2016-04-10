(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('CardCollectionDetailController', CardCollectionDetailController);

    CardCollectionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'CardCollection', 'User', 'Card'];

    function CardCollectionDetailController($scope, $rootScope, $stateParams, entity, CardCollection, User, Card) {
        var vm = this;
        vm.cardCollection = entity;
        vm.load = function (id) {
            CardCollection.get({id: id}, function(result) {
                vm.cardCollection = result;
            });
        };
        var unsubscribe = $rootScope.$on('magicLinkApp:cardCollectionUpdate', function(event, result) {
            vm.cardCollection = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
