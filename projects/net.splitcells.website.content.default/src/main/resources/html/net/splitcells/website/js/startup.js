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
    return true;
};