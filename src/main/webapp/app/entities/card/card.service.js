(function() {
    'use strict';
    angular
        .module('magicLinkApp')
        .factory('Card', Card);

    Card.$inject = ['$resource'];

    function Card ($resource) {
        var resourceUrl =  'api/cards/:id';

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
