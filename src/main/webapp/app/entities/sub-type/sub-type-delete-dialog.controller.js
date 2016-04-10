(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('SubTypeDeleteController',SubTypeDeleteController);

    SubTypeDeleteController.$inject = ['$uibModalInstance', 'entity', 'SubType'];

    function SubTypeDeleteController($uibModalInstance, entity, SubType) {
        var vm = this;
        vm.subType = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            SubType.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
