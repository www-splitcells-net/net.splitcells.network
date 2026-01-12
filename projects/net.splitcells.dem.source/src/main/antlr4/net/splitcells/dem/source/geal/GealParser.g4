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
    | String
    ;
function_call: Name function_call_arguments?;
/* Function calls with round brackets and no arguments are not supported,
 * as these have conceptionally the same meaning as read access to an object field.
 * Therefore, round brackets with no arguments are considered noise and thereby avoided.
 */
function_call_arguments
    : Brace_round_open function_call_chain function_call_arguments_next* Brace_round_closed
    ;
function_call_arguments_next: Comma function_call_chain;
/* The dot is never avoided even when the function calls have round brackets,
 * as the dot helps to align function calls in chains.
 */
function_call_chain: expression (Dot function_call)*;
statement
    : variable_definition Semicolon
    | function_call_chain Semicolon
    ;
/* TODO Implement variable declaration via a modifiers and an attached declaration Name i.e. `public int counter`.
 * The base named variable is created via a function call of the type with a Name as an argument,
 * which defines the variable's name.
 */
variable_definition: Name Equals function_call_chain;
