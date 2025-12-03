/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
function net_splitcells_gel_ui_editor_geal_form_submit() {
    var config = net_splitcells_webserver_form_submit_config();
    config['form-id'] = 'net-splitcells-gel-ui-editor-geal-form'
    config['submit-button-id'] = 'net-splitcells-gel-ui-editor-geal-form-submit';
    config['on-submission-completion'] = () => {};
    net_splitcells_webserver_form_submit(config);
}
function net_splitcells_gel_ui_editor_geal_form_load(loadPath) {
    var config = net_splitcells_webserver_form_submit_config();
    config['form-id'] = 'net-splitcells-gel-ui-editor-geal-form';
    config['load-path'] = loadPath;
    net_splitcells_webserver_form_load(config);
}