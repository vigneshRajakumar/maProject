$(document).ready(function() {
	var url = window.location.href;
	var username = Cookies.get("username");
	if (url.indexOf("index.html") >= 0) {
		if (username != null) {
			 window.location.href = "main.html";
		}
	} else {
		if (username == null) {
			 window.location.href = "index.html";
		} /*else {
			var timer = setTimeout(function() {
				getMessage();
			  }, 10000);
		}*/
	}
	
	$("#userProfileButton").on("click", function () {
        $("#formSubmitButton").hide();
        getUserProfile();
    });

    $("#userSettingsButton").on("click", function () {
        $("#userSettingsTabs li:nth-child(2) a").click();
        $("#formSubmitButton").hide();
        getUserProfile();
    });
    
    /*$("input[type=number]").on("keydown", function (event) {
    	return event.keyCode >= 48 && event.keyCode <= 57;
    });*/
    $("#formSubmitButton").on("click", function () {
        $("#userSubmit").click();
    });

    $('#userProfileForm').on('keyup change', 'input, select, textarea', function(){
        $("#formSubmitButton").show();
    });

    $(".numbersOnly").on('keyup', function () { 
        var v = $(this).val().replace(/[^0-9\.]/g,'');
        $(this).val(v);
    });
});

//Loads the correct sidebar on window load,
//collapses the sidebar on window resize.
// Sets the min-height of #page-wrapper to window size
$(function() {
    $(window).bind("load resize", function() {
        topOffset = 50;
        width = (this.window.innerWidth > 0) ? this.window.innerWidth : this.screen.width;
        if (width < 768) {
            $('div.navbar-collapse').addClass('collapse');
            topOffset = 100; // 2-row-menu
        } else {
            $('div.navbar-collapse').removeClass('collapse');
        }

        height = ((this.window.innerHeight > 0) ? this.window.innerHeight : this.screen.height) - 1;
        height = height - topOffset;
        if (height < 1) height = 1;
        if (height > topOffset) {
            $("#page-wrapper").css("min-height", (height) + "px");
        }
    });

    var url = window.location;
    var element = $('ul.nav a').filter(function() {
        return this.href == url || url.href.indexOf(this.href) == 0;
    }).addClass('active').parent().parent().addClass('in').parent();
    if (element.is('li')) {
        element.addClass('active');
    }
});

//Notification fetch
$(function () {
    $(".badge-corner").text("1");
});

//Display/Hide login button
function toggleLogin () {
  var obj = $(".login");
  if (obj.is(":hidden")) {
    obj.show();
    obj.find("input[name=username]").focus();
  } else {
    obj.hide();
  }
}

//Display user profile in the modal
function getUserProfile () {
	$.ajax({
	      type: "GET",
	      url: "rest/rest/userinfo",
	      success: function (data) {
	    	  $("#userName").val(data.uname);
	    	  $("#userFirstName").val(data.firstname);
	    	  $("#userLastName").val(data.lastname);
	    	  $("#userEmail").val(data.email);
	    	  $("#userBalance").val(data.balance);
	    	  $("#profitLost").val(data.profit_Lost);
	      }
	});
}

function getMessage () {
	$.ajax({
	      type: "GET",
	      url: "rest/rest/message",	      
	      success: function (data) {
	    	  $(".dropdown-menu.dropdown-alerts").append(
	    			  "<li class='divider'></li><li><div><i class='fa fa-envelope fa-fw'></i>" +
	    			  data +
	    			  "</div></li>");
	      }
	});
}

//Actions with registration form
$(function () {
    $('.button-checkbox').each(function () {

        // Settings
        var $widget = $(this),
            $button = $widget.find('button'),
            $checkbox = $widget.find('input:checkbox'),
            color = $button.data('color'),
            settings = {
                on: {
                    icon: 'glyphicon glyphicon-check'
                },
                off: {
                    icon: 'glyphicon glyphicon-unchecked'
                }
            };

        // Event Handlers
        $button.on('click', function () {
            $checkbox.prop('checked', !$checkbox.is(':checked'));
            $checkbox.triggerHandler('change');
            updateDisplay();
        });
        $checkbox.on('change', function () {
            updateDisplay();
        });

        // Actions
        function updateDisplay() {
            var isChecked = $checkbox.is(':checked');

            // Set the button's state
            $button.data('state', (isChecked) ? "on" : "off");

            // Set the button's icon
            $button.find('.state-icon')
                .removeClass()
                .addClass('state-icon ' + settings[$button.data('state')].icon);

            // Update the button's color
            if (isChecked) {
                $button
                    .removeClass('btn-default')
                    .addClass('btn-' + color + ' active');
            }
            else {
                $button
                    .removeClass('btn-' + color + ' active')
                    .addClass('btn-default');
            }
        }

        // Initialization
        function init() {

            updateDisplay();

            // Inject the icon if applicable
            if ($button.find('.state-icon').length == 0) {
                $button.prepend('<i class="state-icon ' + settings[$button.data('state')].icon + '"></i>');
            }
        }
        init();
    });

    $(".register_button_show").on("click", function () {
        $(".register_button_hide").click();
    });
});

$("#logout").on("click", function () {
	Cookies.remove("username");
	window.location.href = "index.html";
});
