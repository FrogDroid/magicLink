(function() {
    'use strict';
    angular
        .module('magicLinkApp')
        .factory('SuperType', SuperType);

    SuperType.$inject = ['$resource'];

    function SuperType ($resource) {
        var resourceUrl =  'api/super-types/:id';

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
