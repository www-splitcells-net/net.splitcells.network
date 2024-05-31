/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
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
	}
}