var stocksTable;
var historyTable;
var portfolioTable;
var stockList = [];

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
    
    var symbol = $(this).find("td").text();
    var imageSource = "//chart.finance.yahoo.com/z?s=" + symbol + "&t=6m&q=l&l=on&z=s&p=m50,m200";
    
    /*$("#stockImage").attr("src", imageSource).load(function() {
    	if (!this.complete || typeof this.naturalWidth == "undefined" || this.naturalWidth == 0) {
            $("#stockImage").attr("src", "css/img/loading.gif");
        }
    });*/
    
    $("#stockImage").attr("src", "css/img/loading.gif");
    $.ajax({
        type: "GET",
        url: imageSource,
        cache: true,
        processData : false,
    }).always(function(){
	    $("#stockImage").attr("src", imageSource).fadeIn();
    });

    timer = setTimeout(function(){
      var windowHeight = window.height;
      var difference = mouseY - $(window).scrollTop();
      
      var startX = mouseX + 20;
      var startY = mouseY + 10 - (1.0 * difference/windowHeight) * 240;
      
      $('#stockGraph').css({'top':startY,'left':startX}).fadeIn('fast');
    }, 600);
  }).on("mouseleave", "tr", function () {
    $(this).removeClass("hover_stock");
    $('#stockGraph').fadeOut('fast');
    clearTimeout(timer);
  });

  $(".trade_button").on("click", function (event) {
    var stock = $("#stock").val();
    var result = $.grep(stockList, function(e){ return e.value == id; });

    if(result.length == 0) {
      event.preventDefault();
    }
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
            "<tr><td>" + stock.symbol +
            "</td><td>" + stock.SName +
            "</td></tr>"
          );
          
          var obj = {label: stock.symbol + "\t " + stock.SName, value: stock.symbol};
          stockList.push(obj);
        }
        
        stocksTable = $("#stocks").DataTable({
          paging: false,
          lengthChange: false,
          info: false,
          "sScrollY": "92vh",
          "bScrollCollapse": true,
          "columnDefs": [
              {
                  "targets": [ 1 ],
                  "visible": false
              }
          ],
          "fnDrawCallback": function ( oSettings ) {
        	    $(oSettings.nTHead).hide();
          }
        });
        
        $("#stock").autocomplete({
            source: stockList
        });
      }
  });
}

function getHistory () {
	$.ajax({
      type: "GET",
      url: "rest/rest/allTrades",
      
      success: function (data) {
    	var result = $.parseJSON(data);
        var list = $("#historyList");
        var algos = new Array("", "Bollinger Bands", "Price Breakout", "Two Moving Averages");

        for (var i = 0; i < result.length; i++) {
          var record = result[i].record;
          list.append(
            "<tr><td title='" + record.SName +
        	"'>" + record.SSymbol +
            "</td><td>" + record.trade_Type +
            "</td><td>" + algos[record.algo_ID] +
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
	$.ajax({
	      type: "GET",
	      url: "rest/rest/portfolio",	      
	      success: function (data) {
	    	var result = $.parseJSON(data);
	        var list = $("#portfolioList");
	        var algos = new Array("", "Bollinger Bands", "Price Breakout", "Two Moving Averages");
	        var statuses = new Array("Before Entering", "Entered", "Exit");
	        
	        for (var i = 0; i < result.length; i++) {
	          var order = result[i].order;
	          list.append(
	            "<tr><td>" + order.symbol +
	            "</td><td>" + statuses[order.status] +
	            "</td><td>" + order.initialAmt +
	            "</td><td>" + order.currentAmt +
	            "</td><td>" + order.percentChange +
	            "</td></tr>"
	          );
		    }
	        
	        portfolioTable = $("#portfolio").DataTable({
	            lengthChange: false,
	            info: false,
	            "dom": 'rt<"bottom"fp><"clear">'
	        });
	      }
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
          content += "<a href='' id='company" + i + "' target='_blank'>" + name + "</a> " + volume + "<span class='" + spanClass + "'>  " + processedDetails + "  </span>";
        }

        $(".top_news").text("");
        $(".top_news").append(content);

        $("#company0").attr("href", "http://www.nasdaq.com/");
        $("#company1").attr("href", "https://www.google.com/finance?cid=626307");
        $("#company2").attr("href", "http://finance.yahoo.com/q?s=%5EFTSE");
      }
  });
}

