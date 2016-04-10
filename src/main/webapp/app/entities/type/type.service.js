(function() {
    'use strict';
    angular
        .module('magicLinkApp')
        .factory('Type', Type);

    Type.$inject = ['$resource'];

    function Type ($resource) {
        var resourceUrl =  'api/types/:id';

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
