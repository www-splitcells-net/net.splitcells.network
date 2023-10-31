let net_splitcells_gel_ui_editor_form_solution = null;
function net_splitcells_gel_ui_calculate_solution_form_solution_download_as_csv() {
    net_splitcells_gel_ui_editor_form_solution.download("csv", "solution.csv", {delimiter:","});
}
function net_splitcells_gel_ui_editor_form_submit() {
    var config = net_splitcells_webserver_form_submit_config();
    config['form-id'] = 'net-splitcells-gel-ui-editor-form'
    config['submit-button-id'] = 'net-splitcells-gel-ui-calculate-solution-form-submit-1';
    config['on-submission-completion'] = () => {
        net_splitcells_gel_ui_editor_form_solution = new Tabulator("#net-splitcells-gel-ui-calculate-solution-form-solution-data"
            , {data: document.getElementById('net-splitcells-gel-ui-editor-form-solution').value
            , importFormat: 'csv'
            , autoColumns: true });
    };
    net_splitcells_webserver_form_submit(config);
}