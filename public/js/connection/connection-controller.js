'use strict';

angular.module('prueba')
  .controller('ConnectionController', ['$scope', '$modal', 'resolvedConnection', 'Connection',
    function ($scope, $modal, resolvedConnection, Connection) {

      $scope.connections = resolvedConnection;

      $scope.create = function () {
        $scope.clear();
        $scope.open();
      };

      $scope.update = function (id) {
        $scope.connection = Connection.get({id: id});
        $scope.open(id);
      };

      $scope.delete = function (id, user_id) {
        Connection.delete({id: id, user_id: user_id},
          function () {
            $scope.connections = Connection.query();
          });
      };

      $scope.save = function (id) {
        if (id) {
          Connection.update({id: id}, $scope.connection,
            function () {
              $scope.connections = Connection.query();
              $scope.clear();
            });
        } else {
          Connection.save($scope.connection,
            function () {
              $scope.connections = Connection.query();
              $scope.clear();
            });
        }
      };

      $scope.clear = function () {
        $scope.connection = {
          
          "name": "",
          
          "server_id": "",
          
          "id": "",
            "user_id": ""
        };
      };

      $scope.open = function (id) {
        var connectionSave = $modal.open({
          templateUrl: 'connection-save.html',
          controller: 'ConnectionSaveController',
          resolve: {
            connection: function () {
              return $scope.connection;
            }
          }
        });

        connectionSave.result.then(function (entity) {
          $scope.connection = entity;
          $scope.save(id);
        });
      };
    }])
  .controller('ConnectionSaveController', ['$scope', '$modalInstance', 'connection',
    function ($scope, $modalInstance, connection) {
      $scope.connection = connection;

      

      $scope.ok = function () {
        $modalInstance.close($scope.connection);
      };

      $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
      };
    }]);
