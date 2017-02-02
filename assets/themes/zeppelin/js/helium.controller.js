angular.module("app", []).controller("HeliumPkgCtrl", function($scope, $window, $sce) {
  $scope.HeliumPkgs = zeppelinHeliumPackages
  $scope.npmWebLink = 'https://www.npmjs.com/package'
  $scope.latestPkgInfo = {}

  var pkgsInfo = $scope.HeliumPkgs
  var latestPkgInfo = []

  for (var idx in pkgsInfo) {
    var eachPkgInfo = pkgsInfo[idx]
    for (var key in eachPkgInfo) {
      // key: pkg's name
      var latestPkg = eachPkgInfo[key]
      for (var ver in latestPkg){
        if (ver == "latest") {
          latestPkgInfo.push(latestPkg[ver])
          latestPkg[ver].icon = $sce.trustAsHtml(latestPkg[ver].icon)

        }
      }
    }
  }
  $scope.latestPkgInfo = latestPkgInfo
  $scope.numberOfPkgs = latestPkgInfo.length
  
  $scope.showPkgsBasedOnType = function () {
    var vizTypePkgs = []
    var spellTypePkgs = []
    for (var idx in latestPkgInfo) {
      if (latestPkgInfo[idx].type == "VISUALIZATION") {
       vizTypePkgs.push(latestPkgInfo[idx])
      } else {
       spellTypePkgs.push(latestPkgInfo[idx])
      }
    }

    $scope.vizTypePkgs = vizTypePkgs
    $scope.spellTypePkgs = spellTypePkgs
  }

  $scope.showPkgsBasedOnType()
});