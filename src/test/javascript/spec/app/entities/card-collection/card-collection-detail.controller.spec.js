'use strict';

describe('Controller Tests', function() {

    describe('CardCollection Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockCardCollection, MockUser, MockCard;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockCardCollection = jasmine.createSpy('MockCardCollection');
            MockUser = jasmine.createSpy('MockUser');
            MockCard = jasmine.createSpy('MockCard');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'CardCollection': MockCardCollection,
                'User': MockUser,
                'Card': MockCard
            };
            createController = function() {
                $injector.get('$controller')("CardCollectionDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'magicLinkApp:cardCollectionUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
