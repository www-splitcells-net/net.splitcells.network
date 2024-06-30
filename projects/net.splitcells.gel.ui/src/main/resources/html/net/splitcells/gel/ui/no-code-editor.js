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
/* Add menus the AST elements of a no-code editor.
 * Menus are nested the following way, whereby the start of each point signals, how such kind of functions are named:
 * 1. `*_enhance`: Enhance AST elements with top level action menus
 * 2. `*_pop_up`: Pop-ups are than dynamically added for each action in the menu.
 * 3. `*_pop_up_[n]`: n-level-pop-ups are used for further recursion, where n starts with 1.
 *
 * Any action on the AST elements by buttons,
 * work independent of the relative location between the AST element and the button.
 * Thereby, the same functionality has not to be implemented separately for every menu and pop-up type and
 * new menu structures are easily supported.
 * In order to achieve this, 2 css classes are placed, when one clicks on a thing, in order to change it,
 * and deleted afterwards.
 * The actions select the elements to be updated via these css classes:
 * 1. The clicked element gets the css class `net-splitcells-no-code-update-subject`.
 * 2. During the click a empty span with the css class `net-splitcells-no-code-insert-target` is placed,
 *    at an location, where a new element could be placed.
 */
function net_splitcells_gel_ui_editor_no_code_enhance() {
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
        net_splitcells_gel_ui_editor_no_code_ast_element_enhance();
	}
}
function net_splitcells_gel_ui_editor_no_code_ast_element_enhance() {
    net_splitcells_gel_ui_editor_no_code_undefined_enhance();
    net_splitcells_gel_ui_editor_no_code_variable_definition_names_enhance();
    net_splitcells_gel_ui_editor_no_code_function_calls_enhance();
    net_splitcells_gel_ui_editor_no_code_variable_references_enhance();
    net_splitcells_gel_ui_editor_no_code_var_arg_enhance();
}
function net_splitcells_gel_ui_editor_no_code_generic_enhance(astElement, config) {
    astElement.onclick = function() {
        for (var j = 0; j < astElement.parentNode.childNodes.length; j++) {
            if (astElement.parentNode.childNodes[j].className == "net-splitcells-dem-lang-perspective-no-code-action-menu") {
                astElement.parentNode.removeChild(astElement.parentNode.childNodes[j]);
                net_splitcells_gel_ui_editor_no_code_action_menu_close();
                return;
            }
        }
        net_splitcells_gel_ui_editor_no_code_action_menu_close();
        actionMenu = document.createElement("div");
        actionMenu.className = "net-splitcells-dem-lang-perspective-no-code-action-menu";
        actionMenu.innerHTML = '<div class="net-splitcells-no-code-action-menu-title"><span class="net-splitcells-no-code-action-menu-title-name">'
            + config.title
            + '</span><span class="net-splitcells-action-button net-splitcells-no-code-action-menu-close" onclick="net_splitcells_gel_ui_editor_no_code_action_menu_close();">X</span></div>'
            + config.actionList;
        astElement.parentNode.insertBefore(actionMenu, astElement.nextSibling);
        if (config['update-target-placement'] == undefined) {
            $('<span class="net-splitcells-no-code-insert-target"> </span>').insertAfter(astElement);
        } else {
            config['update-target-placement'](astElement);
        }
        astElement.className += ' net-splitcells-no-code-update-subject';
    };
}
function net_splitcells_gel_ui_editor_no_code_variable_references_enhance() {
    var arguments = document.getElementsByClassName("net-splitcells-dem-lang-perspective-no-code-variable-reference");
    for (var i = 0; i < arguments.length; i++) {
        net_splitcells_gel_ui_editor_no_code_generic_enhance(arguments[i], {
            title : 'Reference Actions'
            , actionList : '<div class="net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_variable_references_set_pop_up(this);">Set reference</div>'
                + '<div class="net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_variable_references_enhance_help_show(this);">Help</div>'
        });
    }
}
function net_splitcells_gel_ui_editor_no_code_function_calls_enhance() {
    var functionCalls = document.getElementsByClassName("net-splitcells-dem-lang-perspective-no-code-function-call-name");
    for (var i = 0; i < functionCalls.length; i++) {
        net_splitcells_gel_ui_editor_no_code_generic_enhance(functionCalls[i], {
            title : 'Function Actions'
            , actionList : '<div class="net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_function_call_delete(this);">Delete function call</div>'
                + '<div class="net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_function_call_set(this);">Set called function</div>'
                + '<div class="net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_function_call_add(this);">Append function call</div>'
                + '<div class="net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_function_call_name_help_show(this);">Help</div>'
            , 'update-target-placement' : (astElement) => $('<span class="net-splitcells-no-code-insert-target"> </span>').insertAfter(astElement.parentNode)
        });
    }
    var literals = document.getElementsByClassName("net-splitcells-dem-lang-perspective-no-code-literal");
    for (var i = 0; i < literals.length; i++) {
        net_splitcells_gel_ui_editor_no_code_generic_enhance(literals[i], {
            title : 'Literal Actions'
            , actionList : '<div class="net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_literal_set_pop_up(this);">Set Value</div>'
                + '<div class="net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_literal_help_show(this);">Help</div>'
        });
    }
}
function net_splitcells_gel_ui_editor_no_code_undefined_enhance() {
    $('.net-splitcells-dem-lang-perspective-no-code-undefined').each((index, element) => {
        net_splitcells_gel_ui_editor_no_code_generic_enhance(element, {
            title : 'Undefined Actions'
            , actionList : '<div class="net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_function_call_set(this);">Set as function call</div>'
                + '<div class="net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_literal_set_pop_up(this);">Set as literal</div>'
                + '<div class="net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_variable_references_set_pop_up(this);">Set as variable</div>'
                + '<div class="net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_undefined_help_show(this);">Help</div>'
        });
    });
}
function net_splitcells_gel_ui_editor_no_code_undefined_help_show(helpButton) {
    net_splitcells_gel_ui_editor_no_code_help_via_dynamic_name(helpButton, {
        helpSubject: 'undefined'
        , helpType: 'general'
    });
}
function net_splitcells_gel_ui_editor_no_code_variable_definition_names_enhance() {
    var variableDefinitions = document.getElementsByClassName('net-splitcells-dem-lang-perspective-no-code-variable-name');
    for (var i = 0; i < variableDefinitions.length; i++) {
        net_splitcells_gel_ui_editor_no_code_generic_enhance(variableDefinitions[i], {
            title : 'Variable Actions'
            , actionList : '<div class="net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_variable_definition_rename_pop_up(this);">Rename variable</div>'
                + '<div class="net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_variable_definition_delete(this);">Delete variable</div>'
                + '<div class="net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_variable_definition_help_show(this);">Help</div>'
        });
    }
}
function net_splitcells_gel_ui_editor_no_code_action_menu_close() {
    var actionSubjects = document.getElementsByClassName("net-splitcells-no-code-update-subject");
    for (var i = 0; i < actionSubjects.length; i++) {
        actionSubjects[i].classList.remove("net-splitcells-no-code-update-subject");
    }
    net_splitcells_gel_ui_editor_no_code_pop_ups_close();
    var actionMenus = document.getElementsByClassName("net-splitcells-dem-lang-perspective-no-code-action-menu");
    for (var i = 0; i < actionMenus.length; i++) {
        actionMenus[i].parentNode.removeChild(actionMenus[i]);
    }
}
function net_splitcells_gel_ui_editor_no_code_variable_definition_rename_pop_up(renameAction) {
    let renameWindow = document.createElement("div");
    let variableName = $('.net-splitcells-no-code-update-subject').first().get()[0];
    let originalName = variableName.innerHTML;
    let variableDefinition = variableName.parentNode;
    renameWindow.innerHTML = '<div class="net-splitcells-no-code-action-menu-title"><span class="net-splitcells-no-code-action-menu-title-name">Rename variable</span>'
    + '<span class="net-splitcells-action-button net-splitcells-no-code-action-menu-close" onclick="net_splitcells_gel_ui_editor_no_code_pop_ups_close();">X</span>'
    + '</div>';
    let renameInput = document.createElement("input");
    renameInput.type = 'text';
    renameInput.value = variableName.innerHTML;
    renameWindow.appendChild(renameInput);
    renameWindow.className = 'net-splitcells-gel-ui-editor-no-code-pop-up';
    let renameSubmit = document.createElement("div");
    renameSubmit.className = 'net-splitcells-button net-splitcells-action-button';
    renameSubmit.onclick = function() {
        variableName.innerHTML = renameInput.value;
        $('.net-splitcells-dem-lang-perspective-no-code-variable-reference')
            .filter(function(){ return $(this).text() === originalName;})
            .each((index, element) => {
                element.innerHTML = renameInput.value;
            });
        net_splitcells_gel_ui_editor_no_code_action_menu_close();
    };
    renameSubmit.innerHTML = 'Rename';
    renameWindow.appendChild(renameSubmit);
    renameAction.parentNode.insertBefore(renameWindow, renameAction.nextSibling);
}
function net_splitcells_gel_ui_editor_no_code_variable_references_set_pop_up(setAction) {
    let literalElement = $('.net-splitcells-no-code-update-subject').first().get()[0];
    let setWindow = document.createElement("div");

    setWindow.innerHTML = '<div class="net-splitcells-no-code-action-menu-title"><span class="net-splitcells-no-code-action-menu-title-name">Set reference</span>'
        + '<span class="net-splitcells-action-button net-splitcells-no-code-action-menu-close" onclick="net_splitcells_gel_ui_editor_no_code_pop_ups_close();">X</span>'
        + '</div>';
    setWindow.className = 'net-splitcells-gel-ui-editor-no-code-pop-up';

    let variableDefinitions = document.getElementsByClassName("net-splitcells-dem-lang-perspective-no-code-variable-definition");
    for (var i = 0; i < variableDefinitions.length; i++) {
        let variableName = variableDefinitions[i].getElementsByClassName("net-splitcells-dem-lang-perspective-no-code-variable-name")[0];
        let setSubmit = document.createElement("div");
        setSubmit.className = 'net-splitcells-button net-splitcells-action-button';
        setSubmit.onclick = function() {
            literalElement.innerHTML = variableName.innerHTML;
            net_splitcells_gel_ui_editor_no_code_action_menu_close();
        };
        setSubmit.innerHTML = variableName.innerHTML;
        setWindow.appendChild(setSubmit);
    }
    setAction.parentNode.insertBefore(setWindow, setAction.nextSibling);
}
function net_splitcells_gel_ui_editor_no_code_literal_set_pop_up(setAction) {
    let literalElement = $('.net-splitcells-no-code-update-subject').first().get()[0];
    let setWindow = document.createElement("div");
    setWindow.innerHTML = '<div class="net-splitcells-no-code-action-menu-title"><span class="net-splitcells-no-code-action-menu-title-name">Set literal value</span>'
            + '<span class="net-splitcells-action-button net-splitcells-no-code-action-menu-close" onclick="net_splitcells_gel_ui_editor_no_code_pop_ups_close();">X</span>'
            + '</div>';
    let setInput = document.createElement("input");
    setInput.type = 'text';
    setInput.value = literalElement.innerHTML;
    setWindow.appendChild(setInput);
    setWindow.className = 'net-splitcells-gel-ui-editor-no-code-pop-up';
    let setSubmit = document.createElement("div");
    setSubmit.className = 'net-splitcells-button net-splitcells-action-button';
    setSubmit.onclick = function() {
        literalElement.innerHTML = setInput.value;
        net_splitcells_gel_ui_editor_no_code_action_menu_close();
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
    $('.net-splitcells-no-code-insert-target').remove();
    $('.net-splitcells-no-code-update-subject').removeClass('net-splitcells-no-code-update-subject');
}
function net_splitcells_gel_ui_editor_no_code_help_via_dynamic_name(helpAction, config) {
    let helpWindow = document.createElement("div");

    let helpSubject;
    if (config.helpSubject !== undefined) {
        helpSubject = config.helpSubject;
    } else {
        let astElement = $('.net-splitcells-no-code-update-subject').first().get()[0];
        helpSubject = astElement.innerHTML;
    }
    /* Using iframes, makes the help navigable by allowing to read the user related help texts via this pop-up without
     * requiring a new visual navigation element.
     */
    helpWindow.innerHTML = '<div class="net-splitcells-no-code-help-title"><span class="net-splitcells-no-code-help-title-name">Help</span><span class="net-splitcells-action-button net-splitcells-no-code-help-close" onclick="net_splitcells_gel_ui_editor_no_code_pop_ups_close();">X</span></div>'
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
function net_splitcells_gel_ui_editor_no_code_function_call_delete(deleteButton) {
    deleteButton.parentNode.parentNode.parentNode.removeChild(deleteButton.parentNode.parentNode);
    $('.net-splitcells-no-code-update-subject').remove();
    net_splitcells_gel_ui_editor_no_code_pop_ups_close();
}
function net_splitcells_gel_ui_editor_no_code_variable_definition_delete(deleteButton) {
    deleteButton.parentNode.parentNode.parentNode.removeChild(deleteButton.parentNode.parentNode);
}
function net_splitcells_gel_ui_editor_no_code_function_call_set(setButton) {
    var httpRequest = new XMLHttpRequest();
    httpRequest.open("GET", "/net/splitcells/gel/ui/no/code/editor/top-level-functions.json", true);
    httpRequest.onload = (e) => {
        var topLevelFunctions = JSON.parse(httpRequest.responseText);
        net_splitcells_gel_ui_editor_no_code_function_call_set_pop_up(setButton, topLevelFunctions);
    };
    httpRequest.send(null);
}
// TODO Use net_splitcells_gel_ui_editor_no_code_function_call_add_pop_up instead.
function net_splitcells_gel_ui_editor_no_code_function_call_set_pop_up(setAction, topLevelFunctions) {
    let functionName = $('.net-splitcells-no-code-update-subject').first().get()[0];
    let setWindow = document.createElement("div");

    setWindow.innerHTML = '<div class="net-splitcells-no-code-action-menu-title"><span class="net-splitcells-no-code-action-menu-title-name">Set function call</span>'
        + '<span class="net-splitcells-action-button net-splitcells-no-code-action-menu-close" onclick="net_splitcells_gel_ui_editor_no_code_pop_ups_close();">X</span>'
        + '</div>';
    setWindow.className = 'net-splitcells-gel-ui-editor-no-code-pop-up';

    for (var i = 0; i < topLevelFunctions.length; i++) {
        let possibleName = topLevelFunctions[i];
        let setSubmit = document.createElement("div");
        setSubmit.className = 'net-splitcells-button net-splitcells-action-button';
        setSubmit.onclick = function() {
            functionName.innerHTML = possibleName;
            $(functionName.parentNode).children('.net-splitcells-dem-lang-perspective-no-code-function-call-argument').remove();
            net_splitcells_gel_ui_editor_no_code_function_call_add_arguments(functionName.parentNode, possibleName);
        };
        setSubmit.innerHTML = possibleName;
        setWindow.appendChild(setSubmit);
    }
    setAction.parentNode.insertBefore(setWindow, setAction.nextSibling);
}
function net_splitcells_gel_ui_editor_no_code_function_call_add(appendButton) {
    let functionCall = $('.net-splitcells-no-code-insert-target').first().get()[0].parentNode;
    var httpRequest = new XMLHttpRequest();
    httpRequest.open("GET", "/net/splitcells/gel/ui/no/code/editor/functions.json", true);
    httpRequest.onload = (e) => {
        var topLevelFunctions = JSON.parse(httpRequest.responseText);
        net_splitcells_gel_ui_editor_no_code_function_call_append_pop_up(appendButton, topLevelFunctions);
    };
    httpRequest.send(null);
}
/* TODO Use net_splitcells_gel_ui_editor_no_code_function_call_set_pop_up instead, in order to reduce code duplication.
 */
function net_splitcells_gel_ui_editor_no_code_function_call_append_pop_up(appendButton, allowedFunctionCalls) {
    let menu = appendButton.parentNode;
    let functionName = $('.net-splitcells-no-code-insert-target').first().get();
    let callName = functionName.innerHTML;
    let setWindow = document.createElement("div");

    setWindow.innerHTML = '<div class="net-splitcells-no-code-action-menu-title"><span class="net-splitcells-no-code-action-menu-title-name">Append function call</span>'
        + '<span class="net-splitcells-action-button net-splitcells-no-code-action-menu-close" onclick="net_splitcells_gel_ui_editor_no_code_pop_ups_close();">X</span>'
        + '</div>';
    setWindow.className = 'net-splitcells-gel-ui-editor-no-code-pop-up';
    for (var i = 0; i < allowedFunctionCalls.length; ++i) {
        let possibleName = allowedFunctionCalls[i];
        let setSubmit = document.createElement("div");
        setSubmit.className = 'net-splitcells-button net-splitcells-action-button';
        setSubmit.onclick = function() {
            let newFunctionCall = document.createElement('div');
            newFunctionCall.className = 'net-splitcells-dem-lang-perspective-no-code-function-call';
            newFunctionCall.innerHTML = '<span class="net-splitcells-dem-lang-perspective-no-code-function-call-name">' + possibleName + '</span>';
            $(newFunctionCall).replaceAll('.net-splitcells-no-code-insert-target');
            net_splitcells_gel_ui_editor_no_code_function_call_add_arguments(newFunctionCall, possibleName);
            net_splitcells_gel_ui_editor_no_code_pop_ups_close();
        };
        setSubmit.innerHTML = possibleName;
        setWindow.appendChild(setSubmit);
    }
    appendButton.parentNode.insertBefore(setWindow, appendButton.nextSibling);
}
function net_splitcells_gel_ui_editor_no_code_function_call_add_arguments(functionCall, functionName) {
    var requestFunctionMeta = new XMLHttpRequest();
    requestFunctionMeta.open("GET", "/net/splitcells/gel/ui/no/code/editor/function-meta.json", true);
    requestFunctionMeta.onload = (e) => {
        var functionMeta = JSON.parse(requestFunctionMeta.responseText);
        let numberOfArguments = functionMeta[functionName]['number-of-arguments'];
        if (numberOfArguments !== undefined) {
            for (var j = 0; j < numberOfArguments; ++j) {
                functionCall.innerHTML += '<span class="net-splitcells-dem-lang-perspective-no-code-function-call-argument"><span class="net-splitcells-dem-lang-perspective-no-code-undefined">?</span></span>';
                net_splitcells_gel_ui_editor_no_code_ast_element_enhance();
            }
        }
        if (functionMeta[functionName]['has-variable-arguments']) {
            functionCall.innerHTML += '<span class="net-splitcells-dem-lang-perspective-no-code-function-call-argument"><span class="net-splitcells-dem-lang-perspective-no-code-var-arg">...</span></span>';
        }
        net_splitcells_gel_ui_editor_no_code_action_menu_close();
        net_splitcells_gel_ui_editor_no_code_ast_element_enhance(); // TODO Could be speed up, by applying enhance only on the new function call.
    };
    requestFunctionMeta.send(null);
}
function net_splitcells_gel_ui_editor_no_code_var_arg_enhance() {
    $(".net-splitcells-dem-lang-perspective-no-code-var-arg").each((index, element) => {
        net_splitcells_gel_ui_editor_no_code_generic_enhance(element, {
            title : 'Var Arg Actions'
            , actionList : '<div class="net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_function_call_add(this);">Add function call</div>'
                + '<div class="net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_var_arg_add_literal(this);">Add literal</div>'
                + '<div class="net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_var_arg_add_variable_reference(this);">Add variable reference</div>'
                + '<div class="net-splitcells-action-button" onclick="net_splitcells_gel_ui_editor_no_code_var_arg_enhance_help_show(this);">Help</div>'
            , 'update-target-placement' : (astElement) => $('<span class="net-splitcells-no-code-insert-target"> </span>').insertBefore(astElement.parentNode)
        });
    });
}
function net_splitcells_gel_ui_editor_no_code_var_arg_enhance_help_show(helpButton) {
    net_splitcells_gel_ui_editor_no_code_help_via_dynamic_name(helpButton, {
            helpSubject: 'variable-arguments'
            , helpType: 'general'
        });
}
function net_splitcells_gel_ui_editor_no_code_var_arg_add_literal(addButton) {
    let setWindow = document.createElement("div");
    setWindow.innerHTML = '<div class="net-splitcells-no-code-action-menu-title"><span class="net-splitcells-no-code-action-menu-title-name">Set literal value</span>'
        + '<span class="net-splitcells-action-button net-splitcells-no-code-action-menu-close" onclick="net_splitcells_gel_ui_editor_no_code_pop_ups_close();">X</span>'
        + '</div>';
    setWindow.className = 'net-splitcells-gel-ui-editor-no-code-pop-up';
    let setInput = document.createElement("input");
    setInput.type = 'text';
    setWindow.appendChild(setInput);
    setWindow.className = 'net-splitcells-gel-ui-editor-no-code-pop-up';
    let setSubmit = document.createElement("div");
    setSubmit.className = 'net-splitcells-button net-splitcells-action-button';
    setSubmit.onclick = function() {
        $('<span class="net-splitcells-dem-lang-perspective-no-code-function-call-argument">'
                      + '<span class="net-splitcells-dem-lang-perspective-no-code-literal">'
                      + setInput.value
                      + '</span></span>')
            .replaceAll('.net-splitcells-no-code-insert-target');
        net_splitcells_gel_ui_editor_no_code_action_menu_close();
    };
    setSubmit.innerHTML = 'Set value';
    setWindow.appendChild(setSubmit);
    addButton.parentNode.insertBefore(setWindow, addButton.nextSibling);
}
function net_splitcells_gel_ui_editor_no_code_var_arg_add_variable_reference(addButton) {
    let setWindow = document.createElement("div");
    setWindow.innerHTML = '<div class="net-splitcells-no-code-action-menu-title"><span class="net-splitcells-no-code-action-menu-title-name">Set reference</span>'
        + '<span class="net-splitcells-action-button net-splitcells-no-code-action-menu-close" onclick="net_splitcells_gel_ui_editor_no_code_pop_ups_close();">X</span>'
        + '</div>';
    setWindow.className = 'net-splitcells-gel-ui-editor-no-code-pop-up';
    let variableDefinitions = document.getElementsByClassName("net-splitcells-dem-lang-perspective-no-code-variable-definition");
    for (var i = 0; i < variableDefinitions.length; i++) {
        let variableName = variableDefinitions[i].getElementsByClassName("net-splitcells-dem-lang-perspective-no-code-variable-name")[0];
        let setSubmit = document.createElement("div");
        setSubmit.className = 'net-splitcells-button net-splitcells-action-button';
        setSubmit.onclick = function() {
            $('<span class="net-splitcells-dem-lang-perspective-no-code-function-call-argument">'
                    + '<span class="net-splitcells-dem-lang-perspective-no-code-variable-reference">'
                    + variableName.innerHTML
                    + '</span></span>')
                .replaceAll('.net-splitcells-no-code-insert-target');
            net_splitcells_gel_ui_editor_no_code_action_menu_close();
        };
        setSubmit.innerHTML = variableName.innerHTML;
        setWindow.appendChild(setSubmit);
    }
    addButton.parentNode.insertBefore(setWindow, addButton.nextSibling);
}
function net_splitcells_gel_ui_editor_no_code_function_call_add_pop_up(functionCallTarget, popUpTarget, allowedFunctionCalls, config) {
    let setWindow = document.createElement("div");
    setWindow.innerHTML = '<div class="net-splitcells-no-code-action-menu-title"><span class="net-splitcells-no-code-action-menu-title-name">Set function call</span>'
        + '<span class="net-splitcells-action-button net-splitcells-no-code-action-menu-close" onclick="net_splitcells_gel_ui_editor_no_code_pop_ups_close();">X</span>'
        + '</div>';
    setWindow.className = 'net-splitcells-gel-ui-editor-no-code-pop-up';
    for (var i = 0; i < allowedFunctionCalls.length; ++i) {
        let possibleName = allowedFunctionCalls[i];
        let setSubmit = document.createElement("div");
        setSubmit.className = 'net-splitcells-button net-splitcells-action-button';
        setSubmit.onclick = function() {
            let newFunctionCall = document.createElement('div');
            newFunctionCall.className = 'net-splitcells-dem-lang-perspective-no-code-function-call';
            newFunctionCall.innerHTML = '<span class="net-splitcells-dem-lang-perspective-no-code-function-call-name">' + possibleName + '</span>';
            functionCallTarget.appendChild(newFunctionCall);
            net_splitcells_gel_ui_editor_no_code_function_call_add_arguments(newFunctionCall, possibleName);
            functionCallTarget.className = "net-splitcells-dem-lang-perspective-no-code-function-call-argument";
            if (config['update-function'] !== undefined) {
                config['update-function']();
            }
            net_splitcells_gel_ui_editor_no_code_action_menu_close();
        };
        setSubmit.innerHTML = possibleName;
        setWindow.appendChild(setSubmit);
    }
    popUpTarget.parentNode.replaceChild(setWindow, popUpTarget);
}