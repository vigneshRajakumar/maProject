var chartData1 = [];
var chartData2 = [];
var chartData3 = [];
var chartData4 = [];

// in order to set theme for a chart, all you need to include theme file
// located in amcharts/themes folder and set theme property for the chart.

var chart;

var stockList = [];
var stockName = "AAPL";

$(document).ready(function() {
  showGraph();
  getStocksList();
  getStockDetails();

  $(".trade_button").on("click", function (event) {
    var stock = $("#stock").val().toUpperCase();
    var result = $.grep(stockList, function(e){ return e.value == stock; });

    if(result.length == 0) {
      event.preventDefault();
    }
  });

  $("#stock").blur(function () {
    var stock = $("#stock").val().toUpperCase();
    /*stockName = stock;
    showGraph();
    getStockDetails();*/
    var result = $.grep(stockList, function(e){ return e.value == stock; });

    if(result.length != 0) {
      stockName = stock;
      showGraph();
      getStockDetails();
    }
  });
});

function getStockDetails () {
  var queryFront = "select * from yahoo.finance.quotes where symbol in (\"";
  var queryBack = "\")";

  var query = queryFront + stockName + queryBack;
  var processedQuery = query.replace(/ /g, "%20");
  var address = "http://query.yahooapis.com/v1/public/yql?q=" + processedQuery + "&env=store://datatables.org/alltableswithkeys&format=json";
  
  $.ajax({
        type: "GET",
        url: address,
        
        success: function (data) {
          var quote = data.query.results.quote;
          $("#tradeDetailsBody").empty();
          $("#tradeDetailsBody").append(
              "<div class='row'><div class='col-md-6 details_left'>Symbol:</div><div class='col-md-6 details_right'>" + quote.Symbol + 
              "</div><div class='col-md-6 details_left'>Name:</div><div class='col-md-6 details_right'>" + quote.Name +
              "</div><div class='col-md-6 details_left'>Ask:</div><div class='col-md-6 details_right'>" + quote.Ask +
              "</div><div class='col-md-6 details_left'>Bid:</div><div class='col-md-6 details_right'>" + quote.Bid +
              "</div><div class='col-md-6 details_left'>Book Value:</div><div class='col-md-6 details_right'>" + quote.BookValue +
              "</div><div class='col-md-6 details_left'>EBITDA:</div><div class='col-md-6 details_right'>" + quote.EBITDA +
              "</div><div class='col-md-6 details_left'>Day's Range:</div><div class='col-md-6 details_right'>" + quote.DaysRange +
              "</div><div class='col-md-6 details_left'>PercentChange:</div><div class='col-md-6 details_right'>" + quote.PercentChange +
              "</div></div>"
          );
        }
  });
}

function showGraph () {
  var queryFront = "select * from yahoo.finance.historicaldata where symbol in (\"";
  var queryMid = "\") and endDate=\"";
  var queryEnd = "\" and startDate=\"";
  var queryFinish = "\"";

  var d = new Date();
  var month = d.getMonth() + 1;
  var day = d.getDate();
  var dateStringEnd = d.getFullYear() + '-' + (month<10 ? '0' : '') + month + '-' + (day<10 ? '0' : '') + day;

  d.setDate(d.getDate() - 364);
  month = d.getMonth() + 1;
  day = d.getDate();
  var dateStringStart = d.getFullYear() + '-' + (month<10 ? '0' : '') + month + '-' + (day<10 ? '0' : '') + day;

  var query = queryFront + stockName + queryMid + dateStringEnd + queryEnd + dateStringStart + queryFinish;
  var processedQuery = query.replace(/ /g, "%20");
  var address = "http://query.yahooapis.com/v1/public/yql?q=" + processedQuery + "&env=store://datatables.org/alltableswithkeys&format=json";

  $.ajax({
        type: "GET",
        url: address,
        
        success: function (data) {
          generateChartData(data);
          makeChart('dark', '#282828');
        }
  });
}

function getStocksList () {
  $.ajax({
      type: "GET",
      url: "rest/rest/stocklist",
      
      success: function (data) {
        var list = $("#stocksList");

        for (var i = 0; i < data.length; i++) {
          var stock = data[i];
          
          var obj = {label: stock.symbol + "\t " + stock.SName, value: stock.symbol};
          stockList.push(obj);
        }
        
        $("#stock").autocomplete({
            source: stockList
        });
      }
  });
}

function generateChartData(data) {
  var result = data.query.results;

  for (var i = data.query.count - 1; i >= 0 ; i--) {
    var quote = result.quote[i];

    var ori = quote.Date;
    var d = new Date();
    d.setFullYear(parseInt(ori.substr(0,4)));
    d.setMonth(parseInt(ori.substr(5,2)) - 1);
    d.setDate(parseInt(ori.substr(8,2)));

    chartData1.push({
      date: d,
      value: quote.Close,
      volume: quote.Volume
    });
  }
}

function makeChart(theme, bgColor, bgImage) {

  if (chart) {
    chart.clear();
  }

  // background
  /*if ($("#chartdiv").length) {
    $("#chartdiv").css("backgroundColor" : bgColor);
    var url = "url(" + bgImage + ")";
    $("#chartdiv").css("backgroundImage" : url);
  }*/
  $("#chartContainer").css("backgroundColor",bgColor);

  AmCharts.makeChart("chartdiv", {
    type: "stock",
    theme: theme,

    dataSets: [{
        title: stockName,
        fieldMappings: [{
          fromField: "value",
          toField: "value"
        }, {
          fromField: "volume",
          toField: "volume"
        }],
        dataProvider: chartData1,
        categoryField: "date"
      }
    ],

    panels: [{

        showCategoryAxis: false,
        title: "Value",
        percentHeight: 70,

        stockGraphs: [{
          id: "g1",

          valueField: "value",
          comparable: true,
          compareField: "value",
          bullet: "round",
          balloonText: "[[title]]:<b>[[value]]</b>",
          compareGraphBalloonText: "[[title]]:<b>[[value]]</b>",
          compareGraphBullet: "round"
        }],

        stockLegend: {
          periodValueTextComparing: "[[percents.value.close]]%",
          periodValueTextRegular: "[[value.close]]"
        }
      },

      {
        title: "Volume",
        percentHeight: 30,
        stockGraphs: [{
          valueField: "volume",
          type: "column",
          showBalloon: false,
          fillAlphas: 1
        }],


        stockLegend: {
          periodValueTextRegular: "[[value.close]]"
        }
      }
    ],

    chartScrollbarSettings: {
      graph: "g1"
    },

    chartCursorSettings: {
      valueBalloonsEnabled: true
    },

    periodSelector: {
      position: "left",
      periods: [{
        period: "DD",
        count: 10,
        label: "10 days"
      }, {
        period: "MM",
        selected: true,
        count: 1,
        label: "1 month"
      }, {
        period: "YYYY",
        count: 1,
        label: "1 year"
      }, {
        period: "YTD",
        label: "YTD"
      }, {
        period: "MAX",
        label: "MAX"
      }]
    },

    dataSetSelector: {
      position: "left"
    }
  });
}