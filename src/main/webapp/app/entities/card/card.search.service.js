(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .factory('CardSearch', CardSearch);

    CardSearch.$inject = ['$resource'];

    function CardSearch($resource) {
        var resourceUrl =  'api/_search/cards/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
