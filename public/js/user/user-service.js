'use strict';

angular.module('prueba')
  .factory('User', ['$resource', function ($resource) {
    return $resource('prueba/users/:id', {}, {
      'query': { method: 'GET', isArray: true},
      'get': { method: 'GET'},
      'update': { method: 'PUT'}
    });
  }]);
