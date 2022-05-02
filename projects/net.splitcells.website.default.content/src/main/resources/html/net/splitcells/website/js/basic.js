/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
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
 	child=node.childNodes[getRandomInt(node.childNodes.length)];
 	child.style.display = 'inherit';
 	child.style.visibility = 'inherit';
 	}
 function unshowAllChildren(node) {
  	for (var i = 0; i < node.childNodes.length; i++) {
    	unshowElement(node.childNodes[i]);
    	}
  	}
function unshowByCssClass(cssClass) {
    hide(cssClass);
    undisplay(cssClass);
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
	element.style.display = 'inherit';
  	element.style.visibility = 'inherit';
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
		elementId_elements[i].style.visibility = 'inherit';
		}
	}
function display(elementId /*str*/) {
	var elementId_elements = document.getElementsByClassName(elementId);
	for (var i = 0; i < elementId_elements.length; i++) {
		elementId_elements[i].style.display = "inherit";
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
