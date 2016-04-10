(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .factory('SuperTypeSearch', SuperTypeSearch);

    SuperTypeSearch.$inject = ['$resource'];

    function SuperTypeSearch($resource) {
        var resourceUrl =  'api/_search/super-types/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
