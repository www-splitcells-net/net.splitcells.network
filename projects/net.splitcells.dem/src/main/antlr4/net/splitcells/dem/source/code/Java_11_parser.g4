parser grammar Java_11_parser;
/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
 /* TODO Use camel case for naming.
  * TODO Look up how to do grammar with inline strings, via a minimal grammar.
  * TODO Consider using BND grammar like JavaCC, because ANTLR4 is too complicated:
  * * https://javacc.github.io/javacc/documentation/bnf.html
  * * https://en.wikipedia.org/wiki/Backus%E2%80%93Naur_form
  *
  * New Grammars should be written in BNF.
  */
/* source_unit is the root rule. */
/* Grammar guidelines:
 * A grammar files with implicit tokens, would be easier to write, understand and maintain.
 * Implicit tokens are not used, because they have caused many cryptic errors.
 * The author of this does not understand how implicit tokens really work in ANLTR4.
 *
 * Whitespaces are not parsed at the end of the rule, if the whitespace is not required, in order to not
 * take away whitespace from rules, which need them.
 * Whitespace skipping is not used, because otherwise it is hard to allow an arbitrary amount of whitespace between
 * things.
 *
 * Parser guidelines:
 * Make parser as much independent from Lexer as possible.
 * Use string constants instead of token identifier.
 * Use tokens only for string patterns (tokens with regex or fragments with quantifiers).
 *
 */
@header {
    package net.splitcells.dem.source.code.antlr;
}
options {
    tokenVocab=Java_11_lexer;
}
access
    : Dot Whitespace? name Whitespace? call_arguments Whitespace? access?
    | Whitespace? Dot Whitespace? name Whitespace? Whitespace? access?
    ;
allowed_Extensions
	: Whitespace? Extension_Exception
	;
annotation
	: Whitespace? Keysymbol_at name;
annotation_definition
    : Whitespace? javadoc? Whitespace? Keyword_public Whitespace? Keyword_annotation?
    	Whitespace? name
        Whitespace? Brace_curly_open Whitespace? Brace_curly_closed
    ;
call_arguments
    : Whitespace? Brace_round_open Brace_round_closed
    | Whitespace? Brace_round_open Whitespace? call_arguments_element Whitespace? call_arguments_next* Whitespace? Brace_round_closed
    ;
call_arguments_element
    : reference
    | variable_declaration
    ;
call_arguments_next
    : Comma Whitespace call_arguments_element
    ;
class_definition
    : Whitespace? javadoc? Whitespace? Keyword_public? Whitespace? Keyword_final?
    	Whitespace? Keyword_class
    	type_declaration
    	class_extension
        Whitespace? Brace_curly_open Whitespace? class_member* Whitespace? Brace_curly_closed
    ;
class_extension
	: Whitespace? Keyword_extends Whitespace? type_declaration
	| Whitespace? Keyword_implements Whitespace? type_declaration
	| Whitespace? allowed_Extensions?
	;
class_member
    : class_constructor
    | class_member_method_definition
    | class_member_value_declaration
    ;
class_constructor
    : Whitespace? annotation? Whitespace? Keyword_private Whitespace name call_arguments statement_body
    ;
class_member_method_definition
    : Whitespace? javadoc? Whitespace? annotation? Whitespace? modifier_visibility? Whitespace? Keyword_static?
    	Whitespace? type_argument? Whitespace? type_declaration Whitespace?
        name Whitespace? call_arguments Whitespace? Brace_curly_open Whitespace? statement* Whitespace?
        Brace_curly_closed
    ;
class_member_value_declaration
    : Whitespace? javadoc? modifier_visibility? Whitespace? Keyword_static? Whitespace? Keyword_final? Whitespace?
        type_declaration? Whitespace? name Whitespace? Equals Whitespace? statement
    | Whitespace? javadoc? modifier_visibility? Whitespace? Keyword_static? Whitespace? Keyword_final? Whitespace?
              type_declaration? Whitespace? name Whitespace? Semicolon
    ;
enum_definition
	/* TODO Create own enum grammar destinct from class. */
    : Whitespace? javadoc? Whitespace? Keyword_public? Whitespace? Keyword_enum
    	Whitespace? name
        Whitespace? Brace_curly_open enum_values Whitespace? class_member*
        Whitespace? Brace_curly_closed
    ;
enum_values
	: Whitespace? name enum_values_next Semicolon
	;
enum_values_next
    : Whitespace? Comma Whitespace? name enum_values_next?
    ;
expression
    : Whitespace? integer
    | Whitespace? Brace_round_open Whitespace? type_declaration Whitespace?
    	Brace_round_closed Whitespace? expression_child?
    	/* Only upcasting should be done. */
    | Brace_round_open Whitespace? expression Whitespace?
    	Brace_round_closed expression_child?
    | String Whitespace? access?
    | expression Whitespace operator Whitespace expression
    | prefix_operator expression
    | Whitespace? Keyword_new Whitespace? type_declaration Whitespace? call_arguments
        	Whitespace? Brace_curly_open class_member* Whitespace? Brace_curly_closed access?
    | Whitespace? Keyword_new Whitespace? type_declaration Whitespace? call_arguments access?
    | Whitespace? name Whitespace? call_arguments Whitespace? access?
    | Whitespace? name Whitespace? access?
    | expression Whitespace? Operator_plus Whitespace? expression
    | Whitespace expression
    ;
expression_child
	: Whitespace? access
	| expression
	;
import_declaration
    : import_static_declaration
    | import_type_declaration
    ;
import_static_declaration
    : Keyword_import Whitespace Keyword_static Whitespace type_path Semicolon Whitespace*
    ;
import_type_declaration
    : Keyword_import Whitespace type_path Semicolon Whitespace*
    ;
integer
	: Integer
	| Hyphen_minus Digits
	;
interface_definition
    : Whitespace? javadoc? Whitespace? Keyword_public? Whitespace? Keyword_final? Whitespace? Keyword_interface?
    	Whitespace? name
    	type_argument?
    	interface_extension?
        Whitespace? Brace_curly_open interface_definition_member* Whitespace? Brace_curly_closed
    ;
interface_extension
	: Whitespace Keyword_extends Whitespace type_declaration
	;
interface_definition_member_method
    : Whitespace? javadoc?
    	Whitespace? annotation? Whitespace? Whitespace? modifier_visibility? Whitespace? Keyword_static?
    	Whitespace? type_argument? Whitespace? type_declaration Whitespace?
        name Whitespace? call_arguments Semicolon
    | Whitespace? javadoc? Whitespace? annotation? Whitespace? Keyword_static
		Whitespace? type_argument? Whitespace? type_declaration Whitespace?
		name Whitespace? call_arguments Whitespace?
		Brace_curly_open Whitespace? statement* Whitespace? Brace_curly_closed
	| Whitespace? javadoc? Whitespace? annotation? Whitespace? Keyword_default
		Whitespace? type_argument? Whitespace? type_declaration Whitespace?
		name Whitespace? call_arguments Whitespace?
		Brace_curly_open Whitespace? statement* Whitespace? Brace_curly_closed
    | interface_definition_member_static
    ;
interface_definition_member_static
	: Whitespace? javadoc? Whitespace? type_declaration? Whitespace? name Whitespace? Equals Whitespace? statement
	| Whitespace? javadoc? Whitespace? type_declaration? Whitespace? name Whitespace?
		Brace_curly_open Whitespace? statement* Whitespace? Brace_curly_closed
	;
interface_definition_member
	: Whitespace? interface_definition_member_method
	;
javadoc
    : Javadoc /*Javadoc_start Javadoc_end*/ Whitespace?
    ;
license_declaration
    : Comment_multiline
    ;
modifier_visibility
    : Whitespace? Keyword_public
    | Whitespace? Keyword_private
    ;
name
    /* This is needed, because token fragments can currently only be used in tokens.
     * Combined grammars would allow this, but it could not be getting worked.
     */
    : Name
    | Keyword_class
    ;
operator
	: Keysymbol_not_equals
	| Keysymbol_equals
	| Keysymbol_and
	| Less_than
	| Keyword_instanceof
	;
package_declaration
    : 'package' Whitespace package_name Semicolon Whitespace*
    ;
package_name
    : name
    | package_name Dot name
    ;
prefix_operator
	: Keysymbol_not
	;
reference
	: expression
    /* This is an Lambda definition. */
    | name Whitespace? Arrow Whitespace? reference
    | name Whitespace? Arrow Whitespace Brace_curly_open Whitespace? statement* Whitespace? Brace_curly_closed
    | call_arguments Whitespace? Arrow Whitespace? Brace_curly_open Whitespace? statement* Whitespace? Brace_curly_closed
    ;
statement
    : Whitespace? Line_comment
    | Whitespace? Keyword_try Whitespace? Brace_curly_open statement+ Whitespace? Brace_curly_closed statement_catch?
        statement_finally?
    | Whitespace? Keyword_if Whitespace? Brace_round_open expression Whitespace? Brace_round_closed Whitespace?
    	Brace_curly_open statement+ Whitespace? Brace_curly_closed statement_if_else?
    | Whitespace? javadoc
    | Whitespace? Keyword_throw expression Whitespace? Semicolon
    | Whitespace? Keyword_return Whitespace expression Semicolon
    | Whitespace? expression Semicolon
    | Whitespace? variable_declaration (Whitespace Equals Whitespace expression)? Semicolon
    | Whitespace? name Whitespace? access Whitespace Equals Whitespace expression Semicolon
    | Whitespace? name                    Whitespace Equals Whitespace expression Semicolon
    ;
statement_body
    : Whitespace? Brace_curly_open statement* Whitespace? Brace_curly_closed
    ;
statement_if_else
	: Whitespace? Keyword_else Whitespace Brace_curly_open statement+ Whitespace? Brace_curly_closed
	| Whitespace? Keyword_else_if Whitespace Brace_round_open expression Whitespace? Brace_round_closed Whitespace?
		Brace_curly_open statement+ Whitespace? Brace_curly_closed statement_if_else?
	;
statement_catch
    : Whitespace? Keyword_catch Whitespace? Brace_round_open Whitespace? name Whitespace? name Whitespace?
        Brace_round_closed Whitespace? Brace_curly_open statement+ Whitespace? Brace_curly_closed
    ;
statement_finally
    : Whitespace? Keyword_finally Whitespace? Brace_curly_open statement+ Whitespace? Brace_curly_closed
    ;
source_unit
    : license_declaration Whitespace? package_declaration import_declaration* Whitespace? class_definition Whitespace?
    	EOF
    | license_declaration Whitespace? package_declaration import_declaration* Whitespace? interface_definition
        Whitespace? EOF
    | license_declaration Whitespace? package_declaration import_declaration* Whitespace? interface_definition
    	Whitespace? EOF
    | license_declaration Whitespace? package_declaration import_declaration* Whitespace? annotation_definition
        Whitespace? EOF
    | license_declaration Whitespace? package_declaration import_declaration* Whitespace? enum_definition Whitespace?
            	EOF
    ;
type_declaration
    : Whitespace? type_path type_argument? Keysymbol_vararg?
    ;
type_argument
    : Whitespace? Less_than Whitespace? type_argument_content? Whitespace? Bigger_than
    ;
type_argument_content
    : type_argument Whitespace? type_argument_content_next?
    | type_argument_element Whitespace? type_argument_content_next?
    ;
type_argument_content_next
    : Comma Whitespace? type_argument Whitespace? type_argument_content_next?
    | Comma Whitespace? name Whitespace? type_argument_content_next?
    ;
type_argument_element
	: type_name
	| type_name Whitespace Keyword_extends Whitespace type_argument_element type_argument?
	;
type_name
	: name
	| Question_mark
	;
type_path
    : Name
    | type_path Dot Name
    ;
variable_declaration
    : Keyword_final? Whitespace? type_declaration Whitespace name
    ;

