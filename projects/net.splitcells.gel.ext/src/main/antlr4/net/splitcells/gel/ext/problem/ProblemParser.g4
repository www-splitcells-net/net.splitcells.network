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
source_unit: variable_definition;
access: Dot Name call_arguments access?;
call_arguments
    : Brace_round_open Brace_round_closed
    | Brace_round_open call_arguments_element call_arguments_next* Brace_round_closed
    ;
call_arguments_element
    : Name
    ;
call_arguments_next
    : Comma call_arguments_element
    ;
function_call: Name call_arguments access?;
variable_definition: Name Equals function_call Semicolon;