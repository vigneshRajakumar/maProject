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
  $.ajax({
      type: "GET",
      url: "rest/rest/stocklist",
      
      success: function (data) {
        var list = $("#stocksList");

        for (var i = 0; i < data.length; i++) {
          var stock = data[i];
          list.append(
            "<tr><td>" + stock.symbol.replace(/"/g, "").replace(/ /g, "") +
            "</td></tr>"
          );
        }
        
        stocksTable = $("#stocks").DataTable({
          paging: false,
          lengthChange: false,
          info: false,
          "sScrollY": "72vh",
          "bScrollCollapse": true
        });
      }
  });
}

function getHistory () {
	$.ajax({
      type: "GET",
      url: "rest/rest/allTrades",
      
      success: function (data) {
        var list = $("#historyList");

        for (var i = 0; i < data.length; i++) {
          var record = data[i];
          list.append(
            "<tr><td>" + record.stock_ID +
            "</td><td>" + record.trade_Type +
            "</td><td>" + record.num_Shares +
            "</td><td>" + record.price_PerShare +
            "</td></tr>"
          );
        }
        
	    historyTable = $("#history").DataTable({
	      lengthChange: false,
	      info: false,
	      "dom": 'rt<"bottom"fp><"clear">'
	    });
      }
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
  var query = "select * from yahoo.finance.quotes where symbol in (\"^IXIC\",\"^GSPC\",\"^FTSE\")";
  var processedQuery = query.replace(/ /g, "%20");
  var address = "http://query.yahooapis.com/v1/public/yql?q=" + processedQuery + "&env=store://datatables.org/alltableswithkeys&format=json";

  $.ajax({
      type: "GET",
      url: address,
      
      success: function (data) {
        var result = data.query.results;
        var content = "";

        /*content = new Date().toJSON().slice(0,10);
        content += "   ";*/
        var m_names = new Array("Jan", "Feb", "Mar", 
        "Apr", "May", "Jun", "Jul", "Aug", "Sep", 
        "Oct", "Nov", "Dec");

        var d = new Date();
        var curr_date = d.getDate();
        var curr_month = d.getMonth();
        var curr_year = d.getFullYear();
        content = (curr_date + " " + m_names[curr_month] + ", " + curr_year + " ");

        var nasdaq = result.quote[0];
        var sp = result.quote[1];
        var ftse = result.quote[2];

        for (var i = 0; i < data.query.count; i++) {
          var quote = result.quote[i];

          var name = quote.Name;
          var details = quote.Change_PercentChange;
          var volume = quote.Volume;
          var change = quote.Change;

          if (volume == "0") {
            volume = "";
          }

          var spanClass = "";
          var symbol = "";

          if (change.charAt(0) == '-') {
            spanClass = "down";
            symbol = " ▼";
          } else {
            spanClass = "up";
            symbol = " ▲";
          }

          var processedDetails = details.replace(" - ", symbol)
          content += name + " " + volume + "<span class='" + spanClass + "'>  " + processedDetails + "  </span>";
        }

        $(".top_news").text("");
        $(".top_news").append(content);
      }
  });
}

