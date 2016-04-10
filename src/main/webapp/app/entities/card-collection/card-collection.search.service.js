(function() {
    'use strict';

    angular
        .module('magicLinkApp')
        .factory('CardCollectionSearch', CardCollectionSearch);

    CardCollectionSearch.$inject = ['$resource'];

    function CardCollectionSearch($resource) {
        var resourceUrl =  'api/_search/card-collections/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
