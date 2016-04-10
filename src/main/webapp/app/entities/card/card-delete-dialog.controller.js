(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('CardDeleteController',CardDeleteController);

    CardDeleteController.$inject = ['$uibModalInstance', 'entity', 'Card'];

    function CardDeleteController($uibModalInstance, entity, Card) {
        var vm = this;
        vm.card = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Card.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
