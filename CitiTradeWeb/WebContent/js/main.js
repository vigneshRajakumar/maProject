var stocksTable;
var historyTable;
var portfolioTable;

$(document).ready(function() {

  getStocksList();
  getHistory();
  getTopNews();
  getPortfolio();

  var mouseX;
  var mouseY;
  $(document).mousemove( function(e) {
     mouseX = e.pageX; 
     mouseY = e.pageY;
  });

  var timer;
  $("#stocksList").on("mouseenter", "tr", function () {
    $(this).addClass("hover_stock");

    timer = setTimeout(function(){
      $('#stockGraph').css({'top':mouseY,'left':mouseX+20}).fadeIn('fast');
    }, 500);
  }).on("mouseleave", "tr", function () {
    $(this).removeClass("hover_stock");
    $('#stockGraph').fadeOut('fast');
    clearTimeout(timer);
  });
});

function getStocksList () {
  var query = "select * from yahoo.finance.historicaldata where symbol in (\"AAPL\") and endDate=\"2015-08-15\" and startDate=\"2015-01-01\"";
  var processedQuery = query.replace(/ /g, "%20");
  var address = "http://query.yahooapis.com/v1/public/yql?q=" + processedQuery + "&env=store://datatables.org/alltableswithkeys&format=json";

  $.ajax({
      type: "GET",
      url: address,
      
      success: function (data) {
        var list = $("#stocksList");
        var result = data.query.results;

        for (var i = 0; i < data.query.count; i++) {
          var quote = result.quote[i];
          list.append(
            "<tr><td>" + quote.Open +
            "</td><td>" + quote.Close +
            "</td></tr>"
          );
        }
        
        stocksTable = $("#stocks").DataTable({
          paging: false,
          lengthChange: false,
          info: false,
          "sScrollY": "72vh",
          "bScrollCollapse": true
          //"dom": '<"top"f><"datatable-scroll"rt><"bottom"><"clear">'
        });
      }
  });
}

function getHistory () {
  historyTable = $("#history").DataTable({
    lengthChange: false,
    info: false,
    "dom": 'rt<"bottom"fp><"clear">'
  });
}

function getPortfolio () {
  portfolioTable = $("#portfolio").DataTable({
    paging: false,
    lengthChange: false,
    searching: false,
    info: false
  });
}

function getTopNews () {
  
}

