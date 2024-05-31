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
source_unit: statement*;
access: Dot Name function_call_arguments access?;
block_statement
    : Brace_curly_open Brace_curly_closed
    | Brace_curly_open variable_definition statement_reversed* Brace_curly_closed;
function_call: Name function_call_arguments? access?;
function_call_arguments
    : Brace_round_open Brace_round_closed
    | Brace_round_open function_call_arguments_element function_call_arguments_next* Brace_round_closed
    ;
function_call_arguments_element
    : Name
    | Integer
    | function_call;
function_call_arguments_next: Comma function_call_arguments_element;
statement
    : variable_definition Semicolon
    | function_call Semicolon;
statement_reversed: Semicolon variable_definition;
variable_definition
    : Name Equals function_call
    | Name Equals String
    | Name Equals block_statement;
