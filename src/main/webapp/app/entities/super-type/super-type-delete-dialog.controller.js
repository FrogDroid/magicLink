(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('SuperTypeDeleteController',SuperTypeDeleteController);

    SuperTypeDeleteController.$inject = ['$uibModalInstance', 'entity', 'SuperType'];

    function SuperTypeDeleteController($uibModalInstance, entity, SuperType) {
        var vm = this;
        vm.superType = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            SuperType.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
