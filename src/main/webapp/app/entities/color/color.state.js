(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('color', {
            parent: 'entity',
            url: '/color',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Colors'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/color/colors.html',
                    controller: 'ColorController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('color-detail', {
            parent: 'entity',
            url: '/color/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Color'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/color/color-detail.html',
                    controller: 'ColorDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Color', function($stateParams, Color) {
                    return Color.get({id : $stateParams.id});
                }]
            }
        })
        .state('color.new', {
            parent: 'color',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/color/color-dialog.html',
                    controller: 'ColorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                code: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('color', null, { reload: true });
                }, function() {
                    $state.go('color');
                });
            }]
        })
        .state('color.edit', {
            parent: 'color',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/color/color-dialog.html',
                    controller: 'ColorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Color', function(Color) {
                            return Color.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('color', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('color.delete', {
            parent: 'color',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/color/color-delete-dialog.html',
                    controller: 'ColorDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Color', function(Color) {
                            return Color.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('color', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
