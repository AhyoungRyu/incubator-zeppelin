/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var data = [];
_.forEach($z.result.columnNames, function(col, series) {
  if (series == 0) return;
  var values = _.map($z.result.rows, function(row) {
    return {
      label: row[0],
      value : parseFloat(row[series])
    }
  });
  
  data.push({
    key : col.name,
    values : values
  })
});

jQuery.when(
  jQuery.getScript('https://code.highcharts.com/highcharts.js'),
  jQuery.getScript('https://code.highcharts.com/modules/exporting.js'),
  jQuery.getScript('https://cdn.rawgit.com/sdecima/javascript-detect-element-resize/master/jquery.resize.js'),
  jQuery('#highcharts_' + $z.id + ' svg').ready,
  jQuery.Deferred(function( deferred ){
    jQuery( deferred.resolve );
  })
).done(function() {
   jQuery('#highcharts_' + $z.id)
   .highcharts({
    title: {
      text: 'Monthly Average Temperature',
      x: -20 //center
    },
    subtitle: {
      text: 'Source: WorldClimate.com',
      x: -20
    },
    xAxis: {
      categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
    },
    yAxis: {
      title: {
      text: 'Temperature (°C)'
    },
    plotLines: [{
      value: 0,
      width: 1,
      color: '#808080'
      }]
    },
    tooltip: {
      valueSuffix: '°C'
    },
    legend: {
      layout: 'vertical',
      align: 'right',
      verticalAlign: 'middle',
      borderWidth: 0
    },
    series: [{
      name: 'Tokyo',
      data: data
    }]
  });
});
