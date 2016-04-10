(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .factory('SubTypeSearch', SubTypeSearch);

    SubTypeSearch.$inject = ['$resource'];

    function SubTypeSearch($resource) {
        var resourceUrl =  'api/_search/sub-types/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
