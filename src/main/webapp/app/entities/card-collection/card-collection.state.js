(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('card-collection', {
            parent: 'entity',
            url: '/card-collection?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'CardCollections'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/card-collection/card-collections.html',
                    controller: 'CardCollectionController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
            }
        })
        .state('card-collection-detail', {
            parent: 'entity',
            url: '/card-collection/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'CardCollection'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/card-collection/card-collection-detail.html',
                    controller: 'CardCollectionDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'CardCollection', function($stateParams, CardCollection) {
                    return CardCollection.get({id : $stateParams.id});
                }]
            }
        })
        .state('card-collection.new', {
            parent: 'card-collection',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/card-collection/card-collection-dialog.html',
                    controller: 'CardCollectionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('card-collection', null, { reload: true });
                }, function() {
                    $state.go('card-collection');
                });
            }]
        })
        .state('card-collection.edit', {
            parent: 'card-collection',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/card-collection/card-collection-dialog.html',
                    controller: 'CardCollectionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['CardCollection', function(CardCollection) {
                            return CardCollection.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('card-collection', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('card-collection.delete', {
            parent: 'card-collection',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/card-collection/card-collection-delete-dialog.html',
                    controller: 'CardCollectionDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['CardCollection', function(CardCollection) {
                            return CardCollection.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('card-collection', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
