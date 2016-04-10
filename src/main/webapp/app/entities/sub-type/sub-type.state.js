(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('sub-type', {
            parent: 'entity',
            url: '/sub-type',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'SubTypes'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/sub-type/sub-types.html',
                    controller: 'SubTypeController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('sub-type-detail', {
            parent: 'entity',
            url: '/sub-type/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'SubType'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/sub-type/sub-type-detail.html',
                    controller: 'SubTypeDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'SubType', function($stateParams, SubType) {
                    return SubType.get({id : $stateParams.id});
                }]
            }
        })
        .state('sub-type.new', {
            parent: 'sub-type',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sub-type/sub-type-dialog.html',
                    controller: 'SubTypeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('sub-type', null, { reload: true });
                }, function() {
                    $state.go('sub-type');
                });
            }]
        })
        .state('sub-type.edit', {
            parent: 'sub-type',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sub-type/sub-type-dialog.html',
                    controller: 'SubTypeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['SubType', function(SubType) {
                            return SubType.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('sub-type', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('sub-type.delete', {
            parent: 'sub-type',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sub-type/sub-type-delete-dialog.html',
                    controller: 'SubTypeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['SubType', function(SubType) {
                            return SubType.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('sub-type', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
