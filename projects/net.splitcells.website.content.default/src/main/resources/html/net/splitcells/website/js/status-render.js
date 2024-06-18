/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
var httpRequest = new XMLHttpRequest();
httpRequest.open("GET", "/net/splitcells/network/status.csv", true);
httpRequest.onload = (e) => {
    var status = httpRequest.responseText;
    function setContent(node) {
        node.innerHTML = status
    };
    document
        .querySelectorAll("a[class$='net-splitcells-network-status']")
        .forEach(setContent);
};
httpRequest.send(null);