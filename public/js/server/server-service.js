'use strict';

angular.module('prueba')
  .factory('Server', ['$resource', function ($resource) {
    return $resource('prueba/servers/:id', {}, {
      'query': { method: 'GET', isArray: true},
      'get': { method: 'GET'},
      'update': { method: 'PUT'}
    });
  }]);
