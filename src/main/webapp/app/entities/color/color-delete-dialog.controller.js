(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('ColorDeleteController',ColorDeleteController);

    ColorDeleteController.$inject = ['$uibModalInstance', 'entity', 'Color'];

    function ColorDeleteController($uibModalInstance, entity, Color) {
        var vm = this;
        vm.color = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Color.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
