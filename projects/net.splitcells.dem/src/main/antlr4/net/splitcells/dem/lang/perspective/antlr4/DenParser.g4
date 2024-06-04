parser grammar DenParser;
/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 *
 * This is a grammar for the minimal programming language called Den, that describes arbitrary code.
 *
 * TODO Use another name for this language, in order to avoid confusion with Den XML.
 * Den XML should be probably called something else as well,
 * as it conflicts with Discovery based Networks of Perspectives and Changes (=Den).
 */
@header {
    package net.splitcells.dem.lang.perspective.antlr4;
}
options {
    tokenVocab=DenLexer;
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
