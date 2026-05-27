/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
// Detects whether there is a Javascript in the browser and makes an indicator visible.
window.onerror = function(message, source, lineno, colno, error) {
    var elementId_elements = document.getElementsByClassName('net-splitcells-error-status-indicator');
    for (var i = 0; i < elementId_elements.length; i++) {
        elementId_elements[i].style.display = "inherit";
        elementId_elements[i].style.visibility = 'inherit';
    }
    console.log(`Error:`);
    console.log(`message: ${message}`);
    console.log(`source: ${source}`);
    console.log(`lineno: ${lineno}`);
    console.log(`colno: ${colno}`);
    console.log(`error: ${error}`);
    console.log(document.readyState);
    // logError is used, in order to debug errors on browsers with hard to get debug info, like it is the case on iPhones.
    if (document.readyState == "complete") {
        logError(message, source, lineno, colno, error);
    } else {
        window.addEventListener("load", function(event) {
            logError(message, source, lineno, colno, error);
        }, false);
    }
    return true;
};
function logError(message, source, lineno, colno, error) {
    var errorLogs = document.getElementsByClassName('net-splitcells-error-log');
    for (var i = 0; i < errorLogs.length; i++) {
        errorLogs[i].style.display = 'inherit';
        errorLogs[i].style.visibility = 'visible';
    }
    var errorLogLists = document.getElementsByClassName('net-splitcells-error-log-list');
    console.log(errorLogLists);
    
    var message = 'Client side JavaScript error: message = `' + message
            + '` source = `' + source
            + '` lineno = `' + lineno
            + '` colno = `' + colno
            + '` error = `' + error
            + '`';
    for (var i = 0; i < errorLogLists.length; i++) {
        let item = document.createElement("li");
        item.innerHTML = message;
        errorLogLists[i].appendChild(item);
    }
}