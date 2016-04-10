(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('CardDialogController', CardDialogController);

    CardDialogController.$inject = ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Card', 'SuperType', 'Type', 'SubType', 'Color', 'CardCollection'];

    function CardDialogController ($scope, $stateParams, $uibModalInstance, entity, Card, SuperType, Type, SubType, Color, CardCollection) {
        var vm = this;
        vm.card = entity;
        vm.supertypes = SuperType.query();
        vm.types = Type.query();
        vm.subtypes = SubType.query();
        vm.colors = Color.query();
        vm.cardcollections = CardCollection.query();
        vm.load = function(id) {
            Card.get({id : id}, function(result) {
                vm.card = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('magicLinkApp:cardUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.card.id !== null) {
                Card.update(vm.card, onSaveSuccess, onSaveError);
            } else {
                Card.save(vm.card, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
