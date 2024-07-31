parser grammar NoCodeDenParser;
/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 *
 * This is a grammar for the no-code version of the minimal programming language called Den,
 * that describes arbitrary code.
 * No-code version means in this context,
 * that this grammar describes the HTML code used to visualize the original programming language Den
 * as a syntax tree.
 *
 * The no-code version exists,
 * so that users can edit such code without being able to create syntax errors.
 * The original version is needed,
 * as the text based coding is much more compact and therefore is easier to edit and read without special software.
 * The compact version also requires a **lot** less storage space than the no-code version.
 */
@header {
    package net.splitcells.dem.lang.perspective.no.code.antlr4;
}
options {
    tokenVocab=NoCodeDenLexer;
}
source_unit: Document_start
        Html_start
        Body_start
        No_code_start
        statement*
        Ordered_list_end /* This relates to No_code_tart. */
        Body_end
        Html_end
    | No_code_start
          statement*
          Ordered_list_end /* This relates to No_code_tart. */
    ;
function_call: Function_call_start function_call_name function_call_argument* Span_end;
function_call_name: Function_call_name_start string_value Span_end;
function_call_argument: Function_call_argument_start value Span_end;
statement
    : variable_definition
    | variable_access;
string_value
    : Integer
    | Name
    | String_value;
variable_definition:
    Variable_definition_start
    variable_definition_name
    variable_definition_value
    List_item_end;
value
    : Literal_start string_value Span_end
    | function_call+ /* A list of functions is a function chain. */
    | variable_reference
    | Function_call_var_arg
    | Undefined;
variable_definition_name: Variable_definition_name_start Name Span_end;
variable_definition_value: Variable_definition_value_start value Span_end;
variable_reference: Variable_reference_start Name Span_end;
variable_access:
    Variable_access_start
    variable_reference
    function_call+
    List_item_end;
