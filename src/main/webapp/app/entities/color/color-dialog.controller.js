(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('ColorDialogController', ColorDialogController);

    ColorDialogController.$inject = ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Color', 'Card'];

    function ColorDialogController ($scope, $stateParams, $uibModalInstance, entity, Color, Card) {
        var vm = this;
        vm.color = entity;
        vm.cards = Card.query();
        vm.load = function(id) {
            Color.get({id : id}, function(result) {
                vm.color = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('magicLinkApp:colorUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.color.id !== null) {
                Color.update(vm.color, onSaveSuccess, onSaveError);
            } else {
                Color.save(vm.color, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
