/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
// general html/css/javascript functions
/* RENAME elementId -> css_class
 * RENAME elementId_elements
 */
 function readHtmlFromTextArea(from) {
     return from.innerHTML.replaceAll('&lt;', '<').replaceAll('&gt;', '>')
 }
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
function net_splitcells_webserver_form_tab_select(formId, inputName) {
    unshowByCssClass(formId); // Hide the currently visible tab content.
    var inputButtons = document.getElementsByClassName(formId + '-tab-button');
    for (var i = 0; i < inputButtons.length; i++) {
        inputButtons[i].classList.remove('net-splitcells-tab-button-selected');
    }
    showByCssClass(formId + '-' + inputName + '-tab-content');
    var inputTabButtons = document.getElementsByClassName(formId + '-' + inputName + '-tab-button');
    for (var i = 0; i < inputTabButtons.length; i++) {
        inputTabButtons[i].classList.add('net-splitcells-tab-button-selected');
    }
}
/* Submits a HTML form's action.
 * The request is sent as multipart/form-data and contains the form's inputs.
 * The response is a JSON dictionary containing the key `net-splitcells-websiter-server-form-update`.
 * The key is a dictionary, that contains the new values for every input.
 * A not existing field can be in the response.
 * In this case, that server requests the creation of a new form input.
 *
 * A forms input fields uses ids for working on them, as for every input form value there is only one defining element.
 * A forms buttons use CSS classes as ids for working on them,
 * as for every input field there can be multiple buttons with the same function.
 */
function net_splitcells_webserver_form_submit(config) {
    const submitButton = document.getElementById(config['submit-button-id']);
    const formId = config['form-id'];
    const preSubmitButtonText = submitButton.innerHTML;
    const onClickCode = submitButton.onclick;
    submitButton.onclick = null;
    submitButton.innerHTML = "Executing...";
    submitButton.classList.add("net-splitcells-button-activity");
    submitButton.classList.remove("net-splitcells-action-button");
    const form = document.getElementById(formId);
    const request  = new XMLHttpRequest();
    const data = new FormData(form);
    const tabBar = form.querySelector('.net-splitcells-website-form-editor-tab-bar');
    const tabHolder = form.querySelector('.net-splitcells-website-form-editor-tab-holder');
    request.onload = function() {
        console.log('Response to "' + formId + '":' + this.responseText);
        let responseObject = JSON.parse(this.responseText);
        if ('net-splitcells-websiter-server-form-update' in responseObject) {
            const formUpdate = responseObject['net-splitcells-websiter-server-form-update'];
            const dataValues = formUpdate['data-values'];
            const dataTypes = formUpdate['data-types'];
            for (const [key, value] of Object.entries(dataValues)) {
                if (document.querySelector('*[name="' + key + '"]') === null) {
                    console.log('Inserting new form field for update: ' + key);
                    {
                        const newTabButton = document.createElement("div");
                        newTabButton.innerHTML = key;
                        newTabButton.className = 'net-splitcells-button net-splitcells-action-button '
                            + key + '-tab-button '
                            + formId + '-tab-button';
                        newTabButton.onclick = function() {
                            net_splitcells_webserver_form_tab_select(formId, key);
                        };
                        tabBar.appendChild(newTabButton);
                    }
                    document.querySelectorAll('.net-splitcells-website-menu-dynamic').forEach( (menu) => {
                        const newTabButton = document.createElement("div");
                        newTabButton.innerHTML = key;
                        newTabButton.className = 'net-splitcells-button net-splitcells-action-button '
                            + key + '-tab-button '
                            + formId + '-tab-button';
                        newTabButton.onclick = function() {
                            net_splitcells_webserver_form_tab_select(formId, key);
                        };
                        menu.appendChild(newTabButton);
                    });

                    const newTabContent = document.createElement('div');
                    newTabContent.id = key + '-tab-content';
                    newTabContent.className = 'net-splitcells-website-form-editor-tab ' + formId;
                    newTabContent.style.display = 'none';
                    newTabContent.style.visibility = 'hidden';
                    tabHolder.appendChild(newTabContent);

                    if (dataTypes[key] === 'text/plain') {
                        const newTabInput = document.createElement('textarea');
                        newTabInput.id = key;
                        newTabInput.name = key;
                        newTabInput.className = 'net-splitcells-component-priority-0 net-splitcells-webserver-form-text-editor-backend';
                        newTabInput.value = value;
                        newTabInput.innerHTML = value;
                        newTabContent.appendChild(newTabInput);

                        const newTabEditor = document.createElement('div');
                        newTabEditor.className = 'net-splitcells-component-priority-0 net-splitcells-webserver-form-text-editor';
                        newTabEditor.setAttribute('net-splitcells-syncs-to', key);
                        newTabContent.appendChild(newTabEditor);
                    } else if (dataTypes[key] === 'text/csv') {
                        const newTabInput = document.createElement('textarea');
                        newTabInput.id = key;
                        newTabInput.name = key;
                        newTabInput.className = 'net-splitcells-component-priority-0 net-splitcells-webserver-form-text-editor-backend';
                        newTabInput.value = value;
                        newTabInput.innerHTML = value;
                        newTabContent.appendChild(newTabInput);

                        const newTabEditor = document.createElement('div');
                        newTabEditor.className = 'net-splitcells-component-priority-0';
                        newTabEditor.setAttribute('net-splitcells-syncs-to', key);
                        newTabContent.appendChild(newTabEditor);

                        const tabEditorBackend = new Tabulator(newTabEditor
                                , {data: prepareCsvForTabulator(newTabInput.value)
                                , importFormat: 'csv'
                                , autoColumns: true });
                        var observer = new MutationObserver(
                            function(mutations, observer) {
                                for (const m of mutations) {
                                    tabEditorBackend.setData(prepareCsvForTabulator(newTabInput.value));
                                 };
                            }
                        );
                        observer.observe(newTabInput, {
                            attributes: true,
                            characterData: true,
                            subtree: true,
                            childList: true
                        });
                    } else {
                        console.warn('Unknown data type ' + dataTypes[key] + ' for form field update ' + key + '.');
                    }

                    continue;
                }
                const formInput = document.querySelector('*[name="' + key + '"]');
                formInput.value = value; // value is used by form elements like textarea.
                formInput.innerHTML = value; // innerHTML is used by div elements inside forms.
                if (formInput.getAttribute('content-types') === undefined || formInput.getAttribute('content-types') === null) {
                } else {
                    if (formInput.getAttribute('content-types').includes(' error-output ')) {
                        if (value === null || value === undefined || value === '') {
                            let errorButtons = document.getElementsByClassName(key + '-tab-button');
                            for (let i = 0; i < errorButtons.length; i++) {
                                errorButtons[i].classList.remove('net-splitcells-action-button-state-has-error');
                            }
                            continue;
                        }
                        let errorButtons = document.getElementsByClassName(key + '-tab-button');
                        for (let = 0; i < errorButtons.length; i++) {
                            errorButtons[i].classList.add('net-splitcells-action-button-state-has-error');
                        }
                    }
                    if (formInput.getAttribute('content-types').includes(' result-output ')) {
                        if (value === null || value === undefined || value === '') {
                            let errorButtons = document.getElementsByClassName(key + '-tab-button');
                            for (let i = 0; i < errorButtons.length; i++) {
                                errorButtons[i].classList.remove('net-splitcells-action-button-state-has-update');
                            }
                            continue;
                        }
                        let errorButtons = document.getElementsByClassName(key + '-tab-button');
                        for (let i = 0; i < errorButtons.length; i++) {
                            errorButtons[i].classList.add('net-splitcells-action-button-state-has-update');
                        }
                    }
                }
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
function count(str, char) {
    let counter = 0;
    let i = 0;
    for( i; i < this.length; i++) {
        if (str[i] = char) {
            counter++;
        }
    }
    return counter;
};

/* If the CSV data only contains the header line,
 * an empty line at the end is required.
 * In other words, empty tables are represented by tables with one line containing only empty fields.
 */
function prepareCsvForTabulator(csv) {
    let newLines = count(csv, "\n");
    if (newLines = 0) {
        return csv + "\n" + "\n";
    } else if (newLines = 1) {
        return csv + "\n";
    }
    return csv;
}
function downloadStringAsFile(string, filename) {
    // The element does not have to be added to the document, in order to be used.
    let downloader = document.createElement('a');
    downloader.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(string));
    downloader.download = filename;

    downloader.style.display = 'none';
    downloader.style.visibility = 'hidden';
    downloader.click();
}
function uploadFileAsString(reactor) {
    uploader = document.createElement("input")
    uploader.type = 'file'
    uploader.style.display = 'none'
    uploader.style.visibility = 'hidden';
    uploader.onchange = function(uploadEvent) {
        var file = uploadEvent.target.files[0];
        var fileReader = new FileReader();
        fileReader.onload = function(fileEvent) {
            reactor(fileEvent.target.result);
        }
        fileReader.readAsText(file)
    }
    uploader.click();
}
