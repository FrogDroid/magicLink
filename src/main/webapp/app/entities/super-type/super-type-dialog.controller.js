(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('SuperTypeDialogController', SuperTypeDialogController);

    SuperTypeDialogController.$inject = ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'SuperType', 'Card'];

    function SuperTypeDialogController ($scope, $stateParams, $uibModalInstance, entity, SuperType, Card) {
        var vm = this;
        vm.superType = entity;
        vm.cards = Card.query();
        vm.load = function(id) {
            SuperType.get({id : id}, function(result) {
                vm.superType = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('magicLinkApp:superTypeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.superType.id !== null) {
                SuperType.update(vm.superType, onSaveSuccess, onSaveError);
            } else {
                SuperType.save(vm.superType, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
