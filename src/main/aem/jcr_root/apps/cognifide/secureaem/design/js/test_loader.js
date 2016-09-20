(function($){
$(document).ready(function() {
	var tests = $(".test");
	$.fn.shift = [].shift;

	function setIcon($test, result) {
		var $icon = $test.find('.secureaem-test-icon');
		$icon.removeClass('icon-loading');
		if (result == 'disabled') {
			//$icon.addClass('icon-exception');
		} else {
			$icon.addClass('icon-' + result);
			$test.removeClass('disabled');
		}
	}
	
	function addMessages(title, $messages, messages, type) {
		var msg, $div;
		if (messages && messages.length > 0) {
			var $title = $('<li/>').append($('<strong/>').text(title));
			$messages.append($title);
			$.each(messages, function() {
				msg = this.replace(/\[([^\]]+)\]/ig, '<a href="$1">$1</a>');
				$div = $('<div>').append(msg).addClass('li-bullet').addClass('secureaem-' + type);
				$messages.append($('<li>').append($div));
			});
		}
	}

	function onSuccess($test, data) {
		var $messages = $test.find('ul');
		$messages.append($('<li>').append('Environments: ').append($('<strong>').append(data.environments.join(' / '))));
		setIcon($test, data.testResult);
		if (!$test.data('hidePassed')) {
			addMessages("Passed tests:", $messages, data.infoMessages, "info");
		}
		addMessages("Failed tests:", $messages, data.errorMessages, "error");
		
		loadNextTest();
	}
	
	function onError($test) {
		setIcon($test, 'exception');
		loadNextTest();
	}

	function loadNextTest() {
		if (tests.length == 0) {
			return;
		}

		var $test = $(tests.shift());
		$.ajax($test.data('url'), {
			success: function(data) {
				onSuccess($test, data);
			},
			error: function() {
				onError($test);
			},
			cache: false
		});
	}

	loadNextTest();
	

});}(jQuery.noConflict(true)));
