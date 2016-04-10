'use strict';

describe('Controller Tests', function() {

    describe('SuperType Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockSuperType, MockCard;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockSuperType = jasmine.createSpy('MockSuperType');
            MockCard = jasmine.createSpy('MockCard');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'SuperType': MockSuperType,
                'Card': MockCard
            };
            createController = function() {
                $injector.get('$controller')("SuperTypeDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'magicLinkApp:superTypeUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
