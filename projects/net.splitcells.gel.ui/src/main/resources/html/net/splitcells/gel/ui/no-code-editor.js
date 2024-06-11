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
        var variableDefinitions = document.getElementsByClassName("net-splitcells-dem-lang-perspective-no-code-variable-definition");
        for (var i = 0; i < variableDefinitions.length; i++) {
            enhanceVariableDefinitionName(variableDefinitions[i]);
        }
	}
}
function enhanceVariableDefinitionName(variableDefinition) {
    for (var i = 0; i < variableDefinition.childNodes.length; i++) {
        let child = variableDefinition.childNodes[i];
        if (child.className == undefined) {
            continue;
        }
        if (!hasClass(child, 'net-splitcells-dem-lang-perspective-no-code-variable-name')) {
            continue;
        }
        let actionButtonTrigger = document.createElement("span");
        actionButtonTrigger.innerHTML = "☰";
        actionButtonTrigger.onclick = function() {
            for (var j = 0; j < actionButton.childNodes.length; j++) {
                if (actionButton.childNodes[j].className == "net-splitcells-dem-lang-perspective-no-code-action-menu") {
                    actionButton.removeChild(actionButton.childNodes[j]);
                    return;
                }
            }
            var actionMenus = document.getElementsByClassName("net-splitcells-dem-lang-perspective-no-code-action-menu");
            for (var j = 0; j < actionMenus.length; j++) {
                actionMenus[j].parentNode.removeChild(actionMenus[j]);
            }
            actionMenu = document.createElement("div");
            actionMenu.className = "net-splitcells-dem-lang-perspective-no-code-action-menu"
            actionMenu.innerHTML = 'Variable Actions <ol><li onclick="alert(1);">Rename variable</li><li onclick="net_splitcells_gel_ui_editor_no_code_variable_definition_help_show(this);">Help</li></ol>';

            actionButton.appendChild(actionMenu);
        };

        let actionButton = document.createElement("span");
        actionButton.className = "net-splitcells-dem-lang-perspective-no-code-action-button"
        actionButton.appendChild(actionButtonTrigger);
        actionButton.id = 'net-splitcells-dem-lang-perspective-no-code-action-' + ++net_splitcells_gel_ui_editor_no_code_last_node_id;
        variableDefinition.insertBefore(actionButton, child.nextSibling);
    }
}
function net_splitcells_gel_ui_editor_no_code_variable_definition_help_show(helpAction) {
    net_splitcells_gel_ui_editor_no_code_pop_ups_close();
    let helpWindow = document.createElement("div");
    helpWindow.innerHTML = 'Help description  <iframe class="net-splitcells-gel-ui-editor-no-code-pop-up-iframe" src="variable-definition-help.html"></iframe> ';
    helpWindow.className = 'net-splitcells-gel-ui-editor-no-code-pop-up';
    helpAction.parentNode.insertBefore(helpWindow, helpAction.nextSibling);
}
function net_splitcells_gel_ui_editor_no_code_pop_ups_close() {
    let existingPopUps = document.getElementsByClassName("net-splitcells-gel-ui-editor-no-code-pop-up");
    for (var i = 0; i < existingPopUps.length; i++) {
        existingPopUps[i].parentNode.removeChild(existingPopUps[i]);
    }
}