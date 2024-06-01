parser grammar NoCodeDenParser;
/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
@header {
    package net.splitcells.dem.lang.perspective.no.code.antlr4;
}
options {
    tokenVocab=NoCodeDenLexer;
}
source_unit: Document_start statement* Ordered_list_end;

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
    : string_value
    | function_call+ /* A list of functions is a function chain. */
    | variable_reference;
variable_definition_name: Variable_definition_name_start Name Span_end;
variable_definition_value: Variable_definition_value_start value Span_end;
variable_reference: Variable_reference_start Name Span_end;
variable_access:
    Variable_access_start
    variable_definition_name
    function_call+
    List_item_end;
