parser grammar GealParser;
/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 *
 * This is a grammar for the minimal programming language called Geal, that describes arbitrary program code.
 */
@header {
    package net.splitcells.dem.source.geal;
}
options {
    tokenVocab=GealLexer;
}
source_unit: statement*;
expression
    : function_call
    | Integer
    | Name
    | String
    ;
function_call: Name function_call_arguments?;
/* Function calls with round brackets and no arguments are not supported,
 * as these have conceptionally the same meaning as read access to an object field.
 * Therefore, round brackets with no arguments are considered noise and thereby avoided.
 */
function_call_arguments
    : Brace_round_open expression function_call_arguments_next* Brace_round_closed
    ;
function_call_arguments_next: Comma expression;
/* The dot is never avoided even when the function calls have round brackets,
 * as the dot helps to align function calls in chains.
 */
function_call_chain: expression (Dot function_call)*;
statement
    : variable_definition Semicolon
    | function_call_chain Semicolon
    ;
variable_definition: Name Equals expression;
