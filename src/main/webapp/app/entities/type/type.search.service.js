(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .factory('TypeSearch', TypeSearch);

    TypeSearch.$inject = ['$resource'];

    function TypeSearch($resource) {
        var resourceUrl =  'api/_search/types/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
