package ui.utils


expect object Logger {
    fun d(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}

fun getKtorLogger(): io.ktor.client.plugins.logging.Logger = object : io.ktor.client.plugins.logging.Logger {
    override fun log(message: String) {Logger.d("KtorClient", message)
    }
}


/**
 * old code in the App Script :
 */


//var spreadsheetFile = SpreadsheetApp.openByUrl("https://docs.google.com/spreadsheets/d/1SMrpeJC2isCTJotRYXBDNENNbDVzCcazonOOwUQ-Vf0/edit#gid=1335740402")
//var sheet = spreadsheetFile.getSheetByName("SOUTHPOINT STARS U15")
//
//function doPost(e) {
//    var action = e.parameter.action;
//    // var action = "edit";
//
//    if(action =="add"){
//        var spreadsheetName = e.parameter.spreadsheetName;
//
//        var sheet = spreadsheetFile.getSheetByName(spreadsheetName);
//        var playerFirstName = e.parameter.playerFirstName;
//        var playerSecondName = e.parameter.playerSecondName;
//        var age = e.parameter.age;
//        var isShoot = e.parameter.isShoot;
//        var position = e.parameter.position;
//        SpreadsheetApp.flush();
//        sheet.appendRow([playerFirstName,playerSecondName,age,position,isShoot]);
//    }else if(action=="edit"){
//        var spreadsheetName = e.parameter.spreadsheetName;
//        // var spreadsheetName = "SOUTHPOINT STARS U15";
//        var sheet = spreadsheetFile.getSheetByName(spreadsheetName);
//        var isShoot = e.parameter.isShoot;
//        //   var isShoot = "FALSE";
//        var playerFirstName = e.parameter.playerFirstName;
//        var playerSecondName = e.parameter.playerSecondName;
//
//        // var playerFirstName = "Stella";
//        //  var playerSecondName = "Bavetta";
//
//        var range = sheet.getDataRange();
//        var values = range.getValues();
//        for (var i = 0; i < values.length; i++) {
//            for (var j = 0; j < 1; j++) {
//            if (values[i][0] == playerFirstName && values[i][1] == playerSecondName) {
//                Logger.log(values[i][0]);
//                sheet.getRange(i + 1, [5]).setValue(isShoot);
//            }
//        }
//        }
//    }
//    return ContentService.createTextOutput("Success").setMimeType(ContentService.MimeType.TEXT);
//}