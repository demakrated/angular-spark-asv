'use strict';

angular.module('prueba')
  .controller('ServerController', ['$scope', '$modal', 'resolvedServer', 'Server',
    function ($scope, $modal, resolvedServer, Server) {

      $scope.servers = resolvedServer;

      $scope.create = function () {
        $scope.clear();
        $scope.open();
      };

      $scope.update = function (id) {
        $scope.server = Server.get({id: id});
        $scope.open(id);
      };

      $scope.delete = function (id) {
        Server.delete({id: id},
          function () {
            $scope.servers = Server.query();
          });
      };

      $scope.save = function (id) {
        if (id) {
          Server.update({id: id}, $scope.server,
            function () {
              $scope.servers = Server.query();
              $scope.clear();
            });
        } else {
          Server.save($scope.server,
            function () {
              $scope.servers = Server.query();
              $scope.clear();
            });
        }
      };

      $scope.clear = function () {
        $scope.server = {
          
          "name": "",
          
          "ip": "",
          
          "admin_id": "",
          
          "id": ""
        };
      };

      $scope.open = function (id) {
        var serverSave = $modal.open({
          templateUrl: 'server-save.html',
          controller: 'ServerSaveController',
          resolve: {
            server: function () {
              return $scope.server;
            }
          }
        });

        serverSave.result.then(function (entity) {
          $scope.server = entity;
          $scope.save(id);
        });
      };
    }])
  .controller('ServerSaveController', ['$scope', '$modalInstance', 'server',
    function ($scope, $modalInstance, server) {
      $scope.server = server;

      

      $scope.ok = function () {
        $modalInstance.close($scope.server);
      };

      $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
      };
    }]);
