(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .controller('CardCollectionDeleteController',CardCollectionDeleteController);

    CardCollectionDeleteController.$inject = ['$uibModalInstance', 'entity', 'CardCollection'];

    function CardCollectionDeleteController($uibModalInstance, entity, CardCollection) {
        var vm = this;
        vm.cardCollection = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            CardCollection.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
