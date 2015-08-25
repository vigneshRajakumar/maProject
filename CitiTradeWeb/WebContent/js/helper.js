$(document).ready(function() {

	/*var url = window.location.href;
	if (url.indexOf("index.html") >= 0) {
		checkUserOnload();
	} else {
		checkUserStatus();
	}*/

    $("#userSettingsButton").on("click", function () {
        $("#userSettingsTabs li:nth-child(2) a").click();
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
	$.ajax({
		type: "GET",
	    url: "rest/rest/logout",
	    success: function () {
		  window.location.href = "index.html";
	  }
	});
});


function checkUserStatus () {
	$.ajax({
		  type: "GET",
		  url: "rest/rest/verification",
		  success: function (data) {
			  console.log("checkUserStatus: " + data);
			  if (data == "false") {
				  window.location.href = "index.html";
			  }
		  }
	});
}

function checkUserOnload () {
	$.ajax({
	      type: "GET",
	      url: "rest/rest/verification",
	      success: function (data) {
	    	  console.log("checkUserOnload: " + data);
	    	  if(data == "true") {
	    		  window.location.href = "main.html";
	    	  }
	      }
	});
}
