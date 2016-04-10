(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('TypeDeleteController',TypeDeleteController);

    TypeDeleteController.$inject = ['$uibModalInstance', 'entity', 'Type'];

    function TypeDeleteController($uibModalInstance, entity, Type) {
        var vm = this;
        vm.type = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Type.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
