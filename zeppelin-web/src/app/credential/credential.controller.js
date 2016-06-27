/* jshint loopfunc: true */
/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

angular.module('zeppelinWebApp').controller('CredentialCtrl', function($scope, $route, $routeParams, $location, $rootScope,
                                                                         $http, baseUrlSrv, ngToast) {
  $scope._ = _;

  $scope.credentialInfo = [];
  $scope.credentialEntity = '';
  $scope.credentialUsername = '';
  $scope.credentialPassword = '';

  $scope.showAddNewCredentialInfo = false;

  var getCredentialInfo = function() {
    $http.get(baseUrlSrv.getRestApiBase()+'/credential').
      success(function(data, status, headers, config) {
        $scope.credentialInfo = data.body;
        console.log('Success %o %o', status, data.message);
      }).
      error(function(data, status, headers, config) {
        console.log('Error %o %o', status, data.message);
      });
  };

  $scope.addNewCredentialInfo = function() {
    $http.put(baseUrlSrv.getRestApiBase() + '/credential',
    {
      'entity': $scope.credentialEntity,
      'username': $scope.credentialUsername,
      'password': $scope.credentialPassword
    }).
    success(function (data, status, headers, config) {
      BootstrapDialog.alert({
        closable: true,
        message: 'Successfully saved credentials.'
      });
      $scope.resetCredentialInfo();
      getCredentialInfo();
      $scope.showAddNewCredentialInfo = false;
      console.log('Success %o %o', status, data.message);
    }).
    error(function (data, status, headers, config) {
      BootstrapDialog.alert({
        closable: true,
        message: 'Error saving credentials'
      });
      console.log('Error %o %o', status, data.message);
    });
  };

  $scope.cancelCredentialInfo = function() {
    $scope.showAddNewCredentialInfo = false;
    $scope.resetCredentialInfo();
  };

  $scope.resetCredentialInfo = function() {
    $scope.credentialEntity = '';
    $scope.credentialUsername = '';
    $scope.credentialPassword = '';
  };

  $scope.copyOriginCredentialsInfo = function(entity, username, password) {
    angular.copy(entity);
    angular.copy(username);
    angular.copy(password);
  };

  $scope.updateCredentialInfo = function(form, entity, username, password) {
    BootstrapDialog.confirm({
      closable: false,
      closeByBackdrop: false,
      closeByKeyboard: false,
      title: '',
      message: 'Do you want to update this credentials with new information?',
      callback: function (result) {
        if (result) {
          var request = {
            entity: angular.copy(entity),
            username: angular.copy(username),
            password: angular.copy(password)
          };
          var index = _.findIndex($scope.credentialInfo, { 'entity': entity });

          $http.put(baseUrlSrv.getRestApiBase() + '/credential', request).
          success(function (data, status, headers, config) {
              $scope.credentialInfo[index] = data.body;
          }).
          error(function (data, status, headers, config) {
            console.log('Error %o %o', status, data.message);
            ngToast.danger({
              content: 'Fill the all credential information',
              verticalPosition: 'bottom',
              timeout: '3000'
            });
            form.$show();
          });
        } else {
          getCredentialInfo();
        }
      }
    });
  };

  $scope.removeCredentialInfo = function(entity) {
    BootstrapDialog.confirm({
      closable: false,
      closeByBackdrop: false,
      closeByKeyboard: false,
      title: '',
      message: 'Do you want to delete this credential information?',
      callback: function(result) {
        if (result) {
          $http.delete(baseUrlSrv.getRestApiBase() + '/credential/' + entity).
            success(function(data, status, headers, config) {
              var index = _.findIndex($scope.credentialInfo, { 'entity': entity });
              var arr = _.values($scope.credentialInfo);
              arr.splice(index, 1);
              getCredentialInfo();
              console.log('Success %o %o', status, data.message);
            }).
            error(function(data, status, headers, config) {
              console.log('Error %o %o', status, data.message);
            });
        }
      }
    });
  };

  var init = function() {
    $scope.resetCredentialInfo();
    getCredentialInfo();
  };

  init();
});
