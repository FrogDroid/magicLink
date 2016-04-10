(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('CardCollectionDialogController', CardCollectionDialogController);

    CardCollectionDialogController.$inject = ['$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'CardCollection', 'User', 'Card'];

    function CardCollectionDialogController ($scope, $stateParams, $uibModalInstance, $q, entity, CardCollection, User, Card) {
        var vm = this;
        vm.cardCollection = entity;
        vm.users = User.query();
        vm.cards = Card.query();
        vm.load = function(id) {
            CardCollection.get({id : id}, function(result) {
                vm.cardCollection = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('magicLinkApp:cardCollectionUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.cardCollection.id !== null) {
                CardCollection.update(vm.cardCollection, onSaveSuccess, onSaveError);
            } else {
                CardCollection.save(vm.cardCollection, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
