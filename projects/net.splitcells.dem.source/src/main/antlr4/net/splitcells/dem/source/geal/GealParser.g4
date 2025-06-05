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
/* Defining function chains recursively may be a bad idea,
 * because `access` and `function_call` parsing cannot share parsing code as easily as
 * when one uses dedicated `function_call` and the list `function_chain` containing `function_call`.
 * Also using a list for a list like thing instead of nesting is probably a good idea.
 */
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
