(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('SubTypeDialogController', SubTypeDialogController);

    SubTypeDialogController.$inject = ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'SubType', 'Card'];

    function SubTypeDialogController ($scope, $stateParams, $uibModalInstance, entity, SubType, Card) {
        var vm = this;
        vm.subType = entity;
        vm.cards = Card.query();
        vm.load = function(id) {
            SubType.get({id : id}, function(result) {
                vm.subType = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('magicLinkApp:subTypeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.subType.id !== null) {
                SubType.update(vm.subType, onSaveSuccess, onSaveError);
            } else {
                SubType.save(vm.subType, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
