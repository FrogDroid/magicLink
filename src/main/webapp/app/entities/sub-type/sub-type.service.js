(function() {
    'use strict';
    angular
        .module('magicLinkApp')
        .factory('SubType', SubType);

    SubType.$inject = ['$resource'];

    function SubType ($resource) {
        var resourceUrl =  'api/sub-types/:id';

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
