parser grammar ProblemParser;
/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
options {
    tokenVocab=ProblemLexer;
}
@header {
    package net.splitcells.gel.ext.problem;
}
source_unit: demand_definition;
call_arguments
    : Brace_round_open Brace_round_closed
    | Brace_round_open call_arguments_element call_arguments_next* Brace_round_closed
    ;
call_arguments_element
    : Name
    | function_call
    ;
call_arguments_next
    : Comma call_arguments_element
    ;
demand_definition: Keyword_demands Equals function_call;
function_call: Name call_arguments;