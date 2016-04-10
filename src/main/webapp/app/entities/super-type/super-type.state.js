(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('super-type', {
            parent: 'entity',
            url: '/super-type',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'SuperTypes'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/super-type/super-types.html',
                    controller: 'SuperTypeController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('super-type-detail', {
            parent: 'entity',
            url: '/super-type/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'SuperType'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/super-type/super-type-detail.html',
                    controller: 'SuperTypeDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'SuperType', function($stateParams, SuperType) {
                    return SuperType.get({id : $stateParams.id});
                }]
            }
        })
        .state('super-type.new', {
            parent: 'super-type',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/super-type/super-type-dialog.html',
                    controller: 'SuperTypeDialogController',
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
                    $state.go('super-type', null, { reload: true });
                }, function() {
                    $state.go('super-type');
                });
            }]
        })
        .state('super-type.edit', {
            parent: 'super-type',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/super-type/super-type-dialog.html',
                    controller: 'SuperTypeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['SuperType', function(SuperType) {
                            return SuperType.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('super-type', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('super-type.delete', {
            parent: 'super-type',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/super-type/super-type-delete-dialog.html',
                    controller: 'SuperTypeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['SuperType', function(SuperType) {
                            return SuperType.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('super-type', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
