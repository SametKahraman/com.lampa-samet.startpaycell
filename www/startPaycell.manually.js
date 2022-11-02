cordova.define("com.lampa.startpaycell.startpaycell", function(require, exports, module) {
/**
	com.lampa.startpaycell, ver. 6.1.4
	https://github.com/lampaa/com.lampa.startpaycell
	
	Phonegap plugin for check or launch other application in android device (iOS support).
	bug tracker: https://github.com/lampaa/com.lampa.startpaycell/issues
*/

var exec = require('cordova/exec');

module.exports = {
	/** 
	 * Set application params
	 *
	 * @param {Mixed} params				params, view documentation https://github.com/lampaa/com.lampa.startpaycell
	 * @param {Mixed} extra   				Extra fields
	 * @param {Function} errorCallback		The callback that is called when an error occurred when the program starts.
	 *
	 */
	 
	set: function(params, extra) {
		var output = [params];
			
		if(extra != undefined) {
			output.push(extra);
		}
		else {
			output.push(null);
		}
		
		return {
			start: function(completeCallback, errorCallback, messageCallback) {
				completeCallback = completeCallback || function() {};
				errorCallback = errorCallback || function() {};
				messageCallback = messageCallback || function() {};
				
				exec(function(result) {
				    if(result === "OK") {
				        completeCallback(result);
				    }
				    else {
				        var requestCode = result["_ACTION_requestCode_"];
				        delete result["_ACTION_requestCode_"];

				        var resultCode = result["_ACTION_resultCode_"];
				        delete result["_ACTION_resultCode_"];

				        messageCallback(result, requestCode, resultCode);
				    }
				}, errorCallback, "startPaycell", "start", output);
			},
			check: function(completeCallback, errorCallback) {
				completeCallback = completeCallback || function() {};
				errorCallback = errorCallback || function() {};
				
				exec(completeCallback, errorCallback, "startPaycell", "check", output);
			},
			receiver: function(completeCallback, errorCallback, messageCallback) {
				completeCallback = completeCallback || function() {};
				errorCallback = errorCallback || function() {};
				messageCallback = messageCallback || function() {};

				exec(function(result) {
				    if(/\d+/.test(result)) {
				        completeCallback(result);
				    }
				    else {
				        var action = result["_ACTION_VALUE_FORMAT_"];
				        delete result["_ACTION_VALUE_FORMAT_"];

				        messageCallback(action, result);
				    }
				}, errorCallback, "startPaycell", "receiver", output);
			}
		}
	},
	/**
	 * extra values
	 */
	getExtras: function(completeCallback, errorCallback) {
		exec(completeCallback, errorCallback, "startPaycell", "getExtras", []);
	},
	getExtra: function(extraValue, completeCallback, errorCallback) {
		exec(completeCallback, errorCallback, "startPaycell", "getExtra", [extraValue]);
	},
	hasExtra: function(extraValue, completeCallback, errorCallback) {
		this.getExtra(extraValue, completeCallback, errorCallback);
	}
}

});
