'use strict';

angular.module('prueba')
  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider
      .when('/servers', {
        templateUrl: 'views/server/servers.html',
        controller: 'ServerController',
        resolve:{
          resolvedServer: ['Server', function (Server) {
            return Server.query();
          }]
        }
      })
    }]);
