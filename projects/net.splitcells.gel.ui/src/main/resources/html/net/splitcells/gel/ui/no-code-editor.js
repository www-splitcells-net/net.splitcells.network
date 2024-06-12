/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
 var net_splitcells_gel_ui_editor_no_code_last_node_id = -1;
let net_splitcells_gel_ui_editor_form_solution = null;
function net_splitcells_gel_ui_calculate_solution_form_solution_download_as_csv() {
    net_splitcells_gel_ui_editor_form_solution.download("csv", "solution.csv", {delimiter:","});
}
function net_splitcells_gel_ui_editor_form_submit() {
    var config = net_splitcells_webserver_form_submit_config();
    config['form-id'] = 'net-splitcells-gel-ui-editor-form'
    config['submit-button-id'] = 'net-splitcells-gel-ui-calculate-solution-form-submit-1';
    config['on-submission-completion'] = () => {
        // TODO Use paging for table.
        // TODO Inject Tabulator in order to avoid direct dependency.
        net_splitcells_gel_ui_editor_form_solution = new Tabulator("#net-splitcells-gel-ui-editor-form-solution-as-csv-output"
            , {data: document.getElementById('net-splitcells-gel-ui-editor-form-solution').value
            , importFormat: 'csv'
            , autoColumns: true });
    };
    net_splitcells_webserver_form_submit(config);
}
function readHtmlFromTextArea(from) {
    return from.innerHTML.replaceAll('&lt;', '<').replaceAll('&gt;', '>')
}
function enhanceNoCodeEditors() {
	let noCodeEditors = document.querySelectorAll(".net-splitcells-webserver-form-no-code-editor");
	for (var i = 0; i < noCodeEditors.length; i++) {
	    let editor = noCodeEditors[i];
	    let syncTargetId = editor.getAttribute('net-splitcells-syncs-to');
	    let syncTarget = document.getElementById(syncTargetId);
	    editor.innerHTML = readHtmlFromTextArea(syncTarget);
	    let syncTargetObserver = new MutationObserver(function(mutations) {
            if (editor.innerHTML != readHtmlFromTextArea(syncTarget)) {
                editor.innerHTML = readHtmlFromTextArea(syncTarget);
            }
        });
        syncTargetObserver.observe(syncTarget, { attributes: true, childList: true, subtree: true,characterData: true});
        net_splitcells_gel_ui_editor_no_code_variable_definition_names_enhance();
        net_splitcells_gel_ui_editor_no_code_function_calls_enhance();
        net_splitcells_gel_ui_editor_no_code_variable_references_enhance();
        net_splitcells_gel_ui_editor_no_code_variable_access_enhance();
	}
}
function net_splitcells_gel_ui_editor_no_code_generic_enhance(astElement, config) {
    astElement.onclick = function() {
        for (var j = 0; j < astElement.parentNode.childNodes.length; j++) {
            if (astElement.parentNode.childNodes[j].className == "net-splitcells-dem-lang-perspective-no-code-action-menu") {
                astElement.parentNode.removeChild(astElement.parentNode.childNodes[j]);
                return;
            }
        }
        net_splitcells_gel_ui_editor_no_code_action_menu_close();
        actionMenu = document.createElement("div");
        actionMenu.className = "net-splitcells-dem-lang-perspective-no-code-action-menu"
        actionMenu.innerHTML = '<h1>'
            + config.title
            + '</h1><ol>'
            + '<li onclick="net_splitcells_gel_ui_editor_no_code_action_menu_close();">Close</li>' // Close is placed on top, so that a action pop-up can also be closed via a closure of the action menu.
            + config.actionList
            + '</ol>'
        astElement.parentNode.insertBefore(actionMenu, astElement.nextSibling);
    };
}
function net_splitcells_gel_ui_editor_no_code_variable_references_enhance() {
    var arguments = document.getElementsByClassName("net-splitcells-dem-lang-perspective-no-code-variable-reference");
    for (var i = 0; i < arguments.length; i++) {
        net_splitcells_gel_ui_editor_no_code_generic_enhance(arguments[i], {
            title : 'Variable Reference Actions'
            , actionList : '<li onclick="net_splitcells_gel_ui_editor_no_code_variable_references_enhance_help_show(this);">Help</li>'
                + '<li onclick="net_splitcells_gel_ui_editor_no_code_variable_references_set_pop_up(this);">Set reference</li>'
        });
    }
}
function net_splitcells_gel_ui_editor_no_code_variable_access_enhance() {
    var arguments = document.getElementsByClassName("net-splitcells-dem-lang-perspective-no-code-variable-name");
    for (var i = 0; i < arguments.length; i++) {
        net_splitcells_gel_ui_editor_no_code_generic_enhance(arguments[i], {
            title : 'Variable Access Actions'
            , actionList : '<li onclick="net_splitcells_gel_ui_editor_no_code_variable_access_help_show(this);">Help</li>'
        });
    }
}
function net_splitcells_gel_ui_editor_no_code_function_calls_enhance() {
    var functionCalls = document.getElementsByClassName("net-splitcells-dem-lang-perspective-no-code-function-call-name");
    for (var i = 0; i < functionCalls.length; i++) {
        net_splitcells_gel_ui_editor_no_code_generic_enhance(functionCalls[i], {
            title : 'Function Actions'
            , actionList : '<li onclick="net_splitcells_gel_ui_editor_no_code_function_call_name_help_show(this);">Help</li>'
        });
    }
    var literals = document.getElementsByClassName("net-splitcells-dem-lang-perspective-no-code-literal");
    for (var i = 0; i < literals.length; i++) {
        net_splitcells_gel_ui_editor_no_code_generic_enhance(literals[i], {
            title : 'Literal Actions'
            , actionList : '<li onclick="net_splitcells_gel_ui_editor_no_code_literal_help_show(this);">Help</li>'
                + '<li onclick="net_splitcells_gel_ui_editor_no_code_literal_set_pop_up(this);">Set Value</li>'
        });
    }
}
function net_splitcells_gel_ui_editor_no_code_variable_definition_names_enhance() {
    var variableDefinitions = document.getElementsByClassName('net-splitcells-dem-lang-perspective-no-code-variable-name');
    for (var i = 0; i < variableDefinitions.length; i++) {
        net_splitcells_gel_ui_editor_no_code_generic_enhance(variableDefinitions[i], {
            title : 'Variable Actions'
            , actionList : '<li onclick="net_splitcells_gel_ui_editor_no_code_variable_definition_help_show(this);">Help</li>'
                + '<li onclick="net_splitcells_gel_ui_editor_no_code_variable_definition_rename_pop_up(this);">Rename variable</li>'
        });
    }
}
function net_splitcells_gel_ui_editor_no_code_action_menu_close() {
    net_splitcells_gel_ui_editor_no_code_pop_ups_close();
    var actionMenus = document.getElementsByClassName("net-splitcells-dem-lang-perspective-no-code-action-menu");
    for (var i = 0; i < actionMenus.length; i++) {
        actionMenus[i].parentNode.removeChild(actionMenus[i]);
    }
}
function net_splitcells_gel_ui_editor_no_code_variable_definition_rename_pop_up(renameAction) {
    net_splitcells_gel_ui_editor_no_code_pop_ups_close();
    let renameWindow = document.createElement("div");
    let variableDefinition = renameAction.parentNode.parentNode.parentNode.parentNode;
    let variableName = variableDefinition.getElementsByClassName("net-splitcells-dem-lang-perspective-no-code-variable-name")[0];
    renameWindow.innerHTML = '<h1>Rename variable</h1><div class="net-splitcells-button net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_pop_ups_close();">Close</div>';
    let renameInput = document.createElement("input");
    renameInput.type = 'text';
    renameInput.value = variableName.innerHTML;
    renameWindow.appendChild(renameInput);
    renameWindow.className = 'net-splitcells-gel-ui-editor-no-code-pop-up';
    let renameSubmit = document.createElement("div");
    renameSubmit.className = 'net-splitcells-button net-splitcells-action-button';
    renameSubmit.onclick = function() {
        net_splitcells_gel_ui_editor_no_code_variable_definition_rename(variableName, renameInput);
    };
    renameSubmit.innerHTML = 'Rename';
    renameWindow.appendChild(renameSubmit);
    renameAction.parentNode.insertBefore(renameWindow, renameAction.nextSibling);
}
function net_splitcells_gel_ui_editor_no_code_variable_definition_rename(variableName, renameInput) {
    variableName.innerHTML = renameInput.value;
}
function net_splitcells_gel_ui_editor_no_code_variable_references_set_pop_up(setAction) {
    net_splitcells_gel_ui_editor_no_code_pop_ups_close();
    let menu = setAction.parentNode.parentNode.parentNode;
    let literalHolder = menu.parentNode;
    let literalElement = literalHolder.children[Array.from(literalHolder.children).indexOf(menu) - 1];
    let setWindow = document.createElement("div");
    setWindow.innerHTML = '<h1>Set literal value</h1><div class="net-splitcells-button net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_pop_ups_close();">Close</div>';
    setWindow.className = 'net-splitcells-gel-ui-editor-no-code-pop-up';

    let variableDefinitions = document.getElementsByClassName("net-splitcells-dem-lang-perspective-no-code-variable-definition");
    for (var i = 0; i < variableDefinitions.length; i++) {
        let variableName = variableDefinitions[i].getElementsByClassName("net-splitcells-dem-lang-perspective-no-code-variable-name")[0];
        let setSubmit = document.createElement("div");
        setSubmit.className = 'net-splitcells-button net-splitcells-action-button';
        setSubmit.onclick = function() {
            literalElement.innerHTML = variableName.innerHTML;
        };
        setSubmit.innerHTML = variableName.innerHTML;
        setWindow.appendChild(setSubmit);
    }
    setAction.parentNode.insertBefore(setWindow, setAction.nextSibling);
}
function net_splitcells_gel_ui_editor_no_code_literal_set_pop_up(setAction) {
    net_splitcells_gel_ui_editor_no_code_pop_ups_close();
    let menu = setAction.parentNode.parentNode;
    let literalHolder = menu.parentNode;
    let literalElement = literalHolder.children[Array.from(literalHolder.children).indexOf(menu) - 1];
    let setWindow = document.createElement("div");
    setWindow.innerHTML = '<h1>Set literal value</h1><div class="net-splitcells-button net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_pop_ups_close();">Close</div>';
    let setInput = document.createElement("input");
    setInput.type = 'text';
    setInput.value = literalElement.innerHTML;
    setWindow.appendChild(setInput);
    setWindow.className = 'net-splitcells-gel-ui-editor-no-code-pop-up';
    let setSubmit = document.createElement("div");
    setSubmit.className = 'net-splitcells-button net-splitcells-action-button';
    setSubmit.onclick = function() {
        literalElement.innerHTML = setInput.value;
    };
    setSubmit.innerHTML = 'Set value';
    setWindow.appendChild(setSubmit);
    setAction.parentNode.insertBefore(setWindow, setAction.nextSibling);
}
function net_splitcells_gel_ui_editor_no_code_variable_definition_help_show(helpAction) {
    net_splitcells_gel_ui_editor_no_code_help_via_dynamic_name(helpAction, {
        helpSubject: 'variable-definition'
        , helpType: 'general'
    });
}
function net_splitcells_gel_ui_editor_no_code_pop_ups_close() {
    let existingPopUps = document.getElementsByClassName("net-splitcells-gel-ui-editor-no-code-pop-up");
    for (var i = 0; i < existingPopUps.length; i++) {
        existingPopUps[i].parentNode.removeChild(existingPopUps[i]);
    }
}
function net_splitcells_gel_ui_editor_no_code_help_via_dynamic_name(helpAction, config) {
    net_splitcells_gel_ui_editor_no_code_pop_ups_close();
    let helpWindow = document.createElement("div");

    let helpSubject;
    if (config.helpSubject !== undefined) {
        helpSubject = config.helpSubject;
    } else {
        let astElement = helpAction.parentNode.parentNode.parentNode.parentNode;
        let nameHolder = astElement.getElementsByClassName(config.nameHolderCss)[0];
        helpSubject = nameHolder.innerHTML;
    }
    /* Using iframes, makes the help navigable by allowing to read the user related help texts via this pop-up without
     * requiring a new visual navigation element.
     */
    helpWindow.innerHTML = '<h1>Help description</h1><div class="net-splitcells-button net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_pop_ups_close();">Close</div>'
    + '<iframe class="net-splitcells-gel-ui-editor-no-code-pop-up-iframe" src="help/'
    + config.helpType
    + '/'
    + helpSubject
    + '.html"></iframe> ';
    helpWindow.className = 'net-splitcells-gel-ui-editor-no-code-pop-up';
    helpAction.parentNode.insertBefore(helpWindow, helpAction.nextSibling);
}
function net_splitcells_gel_ui_editor_no_code_function_call_name_help_show(helpAction) {
    net_splitcells_gel_ui_editor_no_code_help_via_dynamic_name(helpAction, {
        nameHolderCss: 'net-splitcells-dem-lang-perspective-no-code-function-call-name'
        , helpType: 'function-call'
    });
}
function net_splitcells_gel_ui_editor_no_code_variable_references_enhance_help_show(helpAction) {
    net_splitcells_gel_ui_editor_no_code_help_via_dynamic_name(helpAction, {
        helpSubject: 'variable-reference'
        , helpType: 'general'
    });
}
function net_splitcells_gel_ui_editor_no_code_variable_access_help_show(helpAction) {
    net_splitcells_gel_ui_editor_no_code_help_via_dynamic_name(helpAction, {
        helpSubject: 'variable-access'
        , helpType: 'general'
    });
}
function net_splitcells_gel_ui_editor_no_code_literal_help_show(helpAction) {
    net_splitcells_gel_ui_editor_no_code_help_via_dynamic_name(helpAction, {
        helpSubject: 'literal'
        , helpType: 'general'
    });
}