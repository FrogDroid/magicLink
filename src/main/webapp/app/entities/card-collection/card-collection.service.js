(function() {
    'use strict';
    angular
        .module('magicLinkApp')
        .factory('CardCollection', CardCollection);

    CardCollection.$inject = ['$resource'];

    function CardCollection ($resource) {
        var resourceUrl =  'api/card-collections/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
