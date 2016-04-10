'use strict';

describe('Controller Tests', function() {

    describe('Card Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockCard, MockSuperType, MockType, MockSubType, MockColor, MockCardCollection;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockCard = jasmine.createSpy('MockCard');
            MockSuperType = jasmine.createSpy('MockSuperType');
            MockType = jasmine.createSpy('MockType');
            MockSubType = jasmine.createSpy('MockSubType');
            MockColor = jasmine.createSpy('MockColor');
            MockCardCollection = jasmine.createSpy('MockCardCollection');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Card': MockCard,
                'SuperType': MockSuperType,
                'Type': MockType,
                'SubType': MockSubType,
                'Color': MockColor,
                'CardCollection': MockCardCollection
            };
            createController = function() {
                $injector.get('$controller')("CardDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'magicLinkApp:cardUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
