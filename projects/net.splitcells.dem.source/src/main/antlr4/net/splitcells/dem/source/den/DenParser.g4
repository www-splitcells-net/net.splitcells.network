parser grammar DenParser;
/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 *
 * This is a grammar for the minimal programming language called Den, that describes arbitrary code.
 *
 * TODO Use another name for this language, in order to avoid confusion with Den XML.
 * Den XML should be probably called something else as well,
 * as it conflicts with Discovery based Networks of Perspectives and Changes (=Den).
 * On the other hand, it makes sense to have an overarching Den XML standard,
 * based on sew.xml for natural languages,
 * variable-tree.xml for variable storage as a tree,
 * document-tree.xml for storage of all variables of all documents as one variable tree,
 * which is used as a basis for link and path formats,
 * and programming.xml for links, interactions and integration with other formats.
 * Such a standard could have also an text based Den standard version,
 * that also contains the 3 components,
 * but is not based on existing standards like XML and therefore can be much more compact and simple.
 * Note, that the Perspective model, is the framework for the Den model.
 */
@header {
    package net.splitcells.dem.source.den;
}
options {
    tokenVocab=DenLexer;
}
source_unit: statement*;
/* Defining function chains recursively is probably a bad idea,
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
