'use strict';

angular.module('prueba')
  .factory('Connection', ['$resource', function ($resource) {
    return $resource('prueba/connections/:id', {}, {
      'query': { method: 'GET', isArray: true},
      'get': { method: 'GET'},
      'update': { method: 'PUT'}
    });
  }]);
