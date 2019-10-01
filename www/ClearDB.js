module.exports = {
    recreateFromAssets: function(name, successCallback, errorCallback){
        cordova.exec(successCallback, errorCallback, "ClearDB", "recreateFromAssets", [name]);
    }
};
