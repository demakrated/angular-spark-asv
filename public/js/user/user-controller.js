'use strict';

angular.module('prueba')
  .controller('UserController', ['$scope', '$modal', 'resolvedUser', 'User',
    function ($scope, $modal, resolvedUser, User) {

      $scope.users = resolvedUser;

      $scope.create = function () {
        $scope.clear();
        $scope.open();
      };

      $scope.update = function (name) {
        $scope.user = User.get({name: name});
        $scope.open(name);
      };

      $scope.delete = function (name) {
        User.delete({name: name},
          function () {
            $scope.users = User.query();
          });
      };

      $scope.save = function (name) {
        if (name) {
          User.update({id: name}, $scope.user,
            function () {
              $scope.users = User.query();
              $scope.clear();
            });
        } else {
          User.save($scope.user,
            function () {
              $scope.users = User.query();
              $scope.clear();
            });
        }
      };

      $scope.clear = function () {
        $scope.user = {
          
          "name": "",
          
          "pass": ""
        };
      };

      $scope.open = function (id) {
        var userSave = $modal.open({
          templateUrl: 'user-save.html',
          controller: 'UserSaveController',
          resolve: {
            user: function () {
              return $scope.user;
            }
          }
        });

        userSave.result.then(function (entity) {
          $scope.user = entity;
          $scope.save(id);
        });
      };
    }])
  .controller('UserSaveController', ['$scope', '$modalInstance', 'user',
    function ($scope, $modalInstance, user) {
      $scope.user = user;

      

      $scope.ok = function () {
        $modalInstance.close($scope.user);
      };

      $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
      };
    }]);
