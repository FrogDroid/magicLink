'use strict';

describe('Controller Tests', function() {

    describe('SubType Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockSubType, MockCard;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockSubType = jasmine.createSpy('MockSubType');
            MockCard = jasmine.createSpy('MockCard');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'SubType': MockSubType,
                'Card': MockCard
            };
            createController = function() {
                $injector.get('$controller')("SubTypeDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'magicLinkApp:subTypeUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
