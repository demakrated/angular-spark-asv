'use strict';

angular.module('prueba')
  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider
      .when('/connections', {
        templateUrl: 'views/connection/connections.html',
        controller: 'ConnectionController',
        resolve:{
          resolvedConnection: ['Connection', function (Connection) {
            return Connection.query();
          }]
        }
      })
    }]);
