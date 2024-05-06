/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
// general html/css/javascript functions
/* RENAME elementId -> css_class
 * RENAME elementId_elements
 */
 function checkAvailibility(cssClass) {
	var elementsOfCssClass = document.getElementsByClassName(cssClass);
	for (var i = 0; i < elementsOfCssClass.length; i++) {
		var linksOfElement = elementsOfCssClass[i].getElementsByTagName('a');
		for (var j = 0; j < linksOfElement.length; j++) {
		    if (doesUrlExist(linksOfElement[j].href)) {
                showElement(linksOfElement[j]);
		    } else {
                unshowElement(linksOfElement[j]);
		    }
		}
    }
}
 function doesUrlExist(url) {
    var http = new XMLHttpRequest();
    http.open('HEAD', url, false);
    http.send();
    return http.status>=200 && http.status<300;
    }
 function getRandomInt(max) {
   return Math.floor(Math.random() * Math.floor(max));
    }
 function showOneOfChildren(node) {
 	let child = node.childNodes[getRandomInt(node.childNodes.length)];
 	let i = 0;
 	while (!child.hasOwnProperty('style') && i < 10) {
 	    ++i;
 	    child=node.childNodes[getRandomInt(node.childNodes.length)];
 	    }
 	if (!child.hasOwnProperty('style')) {
 	    return;
 	    }
 	showElement(child);
 	}
 function unshowAllChildren(node) {
     for (var i = 0; i < node.childNodes.length; i++) {
         if (node.childNodes[i].hasOwnProperty('style')) {
             unshowElement(node.childNodes[i]);
             }
         }
     }
function unshowById(elementId) {
    unshowElement(document.getElementById(elementId));
    }
function showById(elementId) {
    showElement(document.getElementById(elementId));
    }
function unshowByCssClass(cssClass) {
    hide(cssClass);
    undisplay(cssClass);
    }
function unshowByTagName(elementName) {
	var elements = document.getElementsByTagName(elementName);
	for (var i = 0; i < elements.length; i++) {
		elements[i].style.display = "none";
		elements[i].style.visibility = "hidden";
		}
	}
function showByCssClass(cssClass) {
    unhide(cssClass);
    display(cssClass);
    }
function unshowElement(element) {
	element.style.display = 'none';
  	element.style.visibility = 'hidden';
	}
function showElement(element) {
    if (element.style === undefined) {
    } else {
        /* The value 'inherit' is not used, as this can cause problems,
        * when i.e. the element in question is a div containing centered text.
        */
	    element.style.display = null;
      	element.style.visibility = null;
      	}
	}
function hasClass(element, cls) {
	return (' ' + element.className + ' ').indexOf(' ' + cls + ' ') > -1;
	}
function apply_to_elements_of(css_class, process) {
	var elements = document.getElementsByClassName(css_class);
	for (var i = 0; i < elements.length; i++) {
		process(elements[i])
		}
	}
function hide(elementId /*str*/ ) {
	var elementId_elements = document.getElementsByClassName(elementId);
	for (var i = 0; i < elementId_elements.length; i++) {
		elementId_elements[i].style.visibility = 'hidden';
		}
	}
function hide_important(elementId /*str*/ ) {
	var elementId_elements = document.getElementsByClassName(elementId);
	for (var i = 0; i < elementId_elements.length; i++) {
		elementId_elements[i].style.setProperty('visibility', 'hidden', 'important');
		}
	}
function element_visibility_default(css_class) {
	apply_to_elements_of(css_class, function(element) { element.style.visibility='inherit';});
	}
function unhide(elementId /*str*/ ) {
	var elementId_elements = document.getElementsByClassName(elementId);
	for (var i = 0; i < elementId_elements.length; i++) {
		elementId_elements[i].style.visibility = null;
		}
	}
function display(elementId /*str*/) {
	var elementId_elements = document.getElementsByClassName(elementId);
	for (var i = 0; i < elementId_elements.length; i++) {
		elementId_elements[i].style.display = null;
		}
	}
function undisplay(elementId /*str*/) {
	var elementId_elements = document.getElementsByClassName(elementId);
	for (var i = 0; i < elementId_elements.length; i++) {
		elementId_elements[i].style.display = "none";
		}
	}
function displayBlock(elementId /*str*/) {
	var elementId_elements = document.getElementsByClassName(elementId);
	for (var i = 0; i < elementId_elements.length; i++) {
		elementId_elements[i].style.display = "block";
		}
	}
// splitcells.net specific functions
function invert_colors() {
	var topElement_elements = document.getElementsByClassName('topElement');
	for (var i = 0; i < elementId_elements.length; i++) {
		if (topElement_elements[i].style.filter == "invert(100%)") {
			topElement_elements[i].style.filter = "invert(0%)";
		} else {
			topElement_elements[i].style.filter = "invert(100%)";
			}
		}
	}
function net_splitcells_webserver_form_submit_config() {
    var config = {
        'form-id' : null,
        'submit-button-id' : null,
        'on-submission-completion' : () => {}
    };
    return config;
}
function net_splitcells_webserver_form_submit(config) {
    var submitButton = document.getElementById(config['submit-button-id']);
    var preSubmitButtonText = submitButton.innerHTML;
    var onClickCode = submitButton.onclick;
    submitButton.onclick = null;
    submitButton.innerHTML = "Executing...";
    submitButton.classList.add("net-splitcells-button-activity");
    submitButton.classList.remove("net-splitcells-action-button");
    var form = document.getElementById(config['form-id']);
    var request  = new XMLHttpRequest();
    var data = new FormData(form);
    request.onload = function() {
        console.log('Response to "' + config['form-id'] + '":' + this.responseText);
        var responseObject = JSON.parse(this.responseText);
        if ('net-splitcells-websiter-server-form-update' in responseObject) {
            for (const [key, value] of Object.entries(responseObject['net-splitcells-websiter-server-form-update'])) {
                document.getElementById(key).value = value; // value is used by form elements like textarea.
                document.getElementById(key).innerHTML = value; // innerHTML is used by div elements inside forms.
            }
        }
        config['on-submission-completion']();
        submitButton.classList.remove("net-splitcells-button-activity");
        submitButton.classList.add("net-splitcells-action-button");
        submitButton.innerHTML = preSubmitButtonText;
        submitButton.onclick = onClickCode;
    }
    request.open("post", form.action);
    request.send(data);
}
