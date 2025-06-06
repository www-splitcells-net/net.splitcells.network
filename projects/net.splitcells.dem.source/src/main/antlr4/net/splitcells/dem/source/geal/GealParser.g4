parser grammar GealParser;
/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 *
 * This is a grammar for the minimal programming language called Geal,
 * that describes arbitrary program code.
 * It was initially created only for the Generic Allocator and therefore was named after it.
 * The name Geal comes from using the first 2 letters for every word of the Generic Allocator,
 * but Geal's full name is the Generic Allocator Language.
 * Geal seem to also stand for `to congeal`, which is quite fitting for an constraint language.
 * Previously, the name Den was considered, which stands Discovery based Perspective Network,
 * but the name was already used for an XML namespace in this project.l.
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
