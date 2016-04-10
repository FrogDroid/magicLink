(function() {
    'use strict';
    angular
        .module('magicLinkApp')
        .factory('Color', Color);

    Color.$inject = ['$resource'];

    function Color ($resource) {
        var resourceUrl =  'api/colors/:id';

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
