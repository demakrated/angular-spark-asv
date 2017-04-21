// Declare app level module which depends on filters, and services
angular.module('prueba', ['ngResource', 'ngRoute', 'ui.bootstrap', 'ui.date'])
  .config(['$routeProvider','$httpProvider', '$qProvider', function ($routeProvider, $httpProvider, $qProvider) {
    $routeProvider
      .when('/', {redirectTo: '/users'})
      .otherwise({redirectTo: '/users'});
      $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';
      $httpProvider.defaults.headers.put['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';
      $qProvider.errorOnUnhandledRejections(false);
  }]);
