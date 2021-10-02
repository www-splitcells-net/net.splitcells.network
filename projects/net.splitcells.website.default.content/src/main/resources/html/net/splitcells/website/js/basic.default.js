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
function change_menu_visibility() {
	var change_menu_content_elements = document.getElementsByClassName('change_menu_content');
	for (var i = 0; i < change_menu_content_elements.length; i++) {
		if (change_menu_content_elements[i].innerHTML === 'show menu') {
			show_contentCell();
			show_menu();
		} else {
			show_contentCell();
			hide_menu();
			}
		}
	}
// TODO Deprecated
function addapt_menu_visibility() {
	if (window.matchMedia("(max-width: 54em)").matches) {
		hide_menu();
	}
	else {
		show_menu();
	}
	show_contentCell();
	}
function hide_contentCell() {
	var contentCell_elements = document.getElementsByClassName('net-splitcells-content-main');
	for (var i = 0; i < contentCell_elements.length; i++) {
		contentCell_elements[i].style.setProperty('visibility', 'hidden', 'important');
		contentCell_elements[i].style.setProperty('display', 'none', 'important');
		}
	}
function hide_menu() {
	var menu_elements = document.getElementsByClassName('menu');
	for (var i = 0; i < menu_elements.length; i++) {
		menu_elements[i].style.setProperty('visibility', 'hidden', 'important');
		menu_elements[i].style.setProperty('display', 'none', 'important');
		}
	var change_menu_content_elements = document.getElementsByClassName('change_menu_content');
	for (var i = 0; i < change_menu_content_elements.length; i++) {
		change_menu_content_elements[i].innerHTML = 'show menu';
		}
	}
function show_contentCell() {
	var contentCell_elements = document.getElementsByClassName('net-splitcells-content-main');
	for (var i = 0; i < contentCell_elements.length; i++) {
		contentCell_elements[i].style.setProperty('visibility', 'visible', 'important');
		contentCell_elements[i].style.setProperty('display', 'flex', 'important');
		}
	}
function show_menu() {
	var contentCell_elements = document.getElementsByClassName('menu');
	for (var i = 0; i < contentCell_elements.length; i++) {
		contentCell_elements[i].style.setProperty('visibility', 'visible', 'important');
		contentCell_elements[i].style.setProperty('display', 'flex', 'important');
		}
	var change_menu_content_elements = document.getElementsByClassName('change_menu_content');
	for (var i = 0; i < change_menu_content_elements.length; i++) {
		change_menu_content_elements[i].innerHTML = 'hide menu';
		}
	}
var numberOfMessages = 1;
var containterIdMessages = 'messages';
function removeMessage(argId) {
	numberOfMessages = numberOfMessages - 1;
	if (numberOfMessages <= 0) {
		hide(containterIdMessages);
		undisplay(containterIdMessages);
		}
	undisplay(argId);
	hide(argId);
	}
removeMessage('noScriptMessage');