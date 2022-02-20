parser grammar Java11Parser;
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
    tokenVocab=Java11Lexer;
}
access
    : Dot type_argument? name call_arguments access?
    | Dot name access?
    | Brackets_open expression Brackets_closed
    ;
annotation
    /* TODO call_arguments is a hack. */
	: Keysymbol_at name call_arguments?;
annotation_definition
    : javadoc? Keyword_public Keyword_annotation* name
        Brace_curly_open Brace_curly_closed
    ;
call_arguments
    : Brace_round_open Brace_round_closed
    | Brace_round_open call_arguments_element call_arguments_next* Brace_round_closed
    ;
call_arguments_element
    : reference
    | variable_declaration
    ;
call_arguments_next
    : Comma call_arguments_element
    ;
class_definition
    : javadoc? annotation* Keyword_public? Keyword_final?
    	Keyword_class
    	type_declaration
    	class_extension
        Brace_curly_open class_member* Brace_curly_closed
    ;
class_extension
	: (Keyword_extends type_declaration)? (Keyword_implements type_declaration)? Extension_Exception?
	;
class_member
    : javadoc? Keysymbol_at Keyword_JavaLegacyBody .* Brace_curly_open .* Brace_curly_closed
    | javadoc? Keysymbol_at Keyword_JavaLegacyArtifact .* Brace_curly_open .* Brace_curly_closed
    | class_constructor
    | class_member_method_definition
    | class_member_value_declaration
    ;
class_constructor
    : javadoc? annotation* Keyword_private name call_arguments statement_body
    ;
class_member_method_definition
    : javadoc? annotation* modifier_visibility? Keyword_static?
    	type_argument? type_declaration
        name call_arguments Brace_curly_open statement*
        Brace_curly_closed
    ;
class_member_value_declaration
    : javadoc? annotation? modifier_visibility? Keyword_static? Keyword_final?
        type_declaration? name Equals statement
    | javadoc? annotation? modifier_visibility? Keyword_static? Keyword_final?
              type_declaration? name Semicolon
    ;
enum_definition
	/* TODO Create own enum grammar destinct from class. */
    : javadoc? Keyword_public? Keyword_enum
    	name
        Brace_curly_open enum_values class_member*
        Brace_curly_closed
    ;
enum_values
	: name enum_values_next Semicolon
	;
enum_values_next
    : Comma name enum_values_next?
    ;
expression
    : integer
    | Brace_round_open type_declaration
    	Brace_round_closed expression_child?
    	/* Only upcasting should be done. */
    | Brace_round_open expression
    	Brace_round_closed expression_child?
    | String access?
    | expression operator expression
    | prefix_operator expression
    | Keyword_new type_declaration call_arguments
        	Brace_curly_open class_member* Brace_curly_closed access?
    | Keyword_new type_declaration call_arguments access?
    | name call_arguments access?
    | name access?
    | expression operator expression
    | expression_with_prefix
    ;
expression_with_prefix
    : Operator_plus Operator_plus expression
    ;
expression_child
	: access
	| expression
	;
import_declaration
    : import_static_declaration
    | import_type_declaration
    ;
import_static_declaration
    : Keyword_import Keyword_static type_path Semicolon
    ;
import_type_declaration
    : Keyword_import type_path Semicolon
    ;
integer
	: Integer
	| Hyphen_minus Digits
	;
interface_definition
    : javadoc? annotation* Keyword_public? Keyword_final? Keyword_interface?
    	name
    	type_argument?
    	interface_extension?
        Brace_curly_open interface_definition_member* Brace_curly_closed
    ;
interface_extension
	: Keyword_extends type_declaration interface_extension_additional*
	;
interface_extension_additional
    : Comma type_declaration
    ;
interface_definition_member_method
    : javadoc?
    	annotation* modifier_visibility? Keyword_static?
    	type_argument? type_declaration
        name call_arguments Semicolon
    | javadoc? annotation* Keyword_static
		type_argument? type_declaration
		name call_arguments
		Brace_curly_open statement* Brace_curly_closed
	| javadoc? annotation* Keyword_default
		type_argument? type_declaration
		name call_arguments
		Brace_curly_open statement* Brace_curly_closed
    | interface_definition_member_static
    ;
interface_definition_member_static
	: javadoc? type_declaration? name Equals statement
	| javadoc? type_declaration? name
		Brace_curly_open statement* Brace_curly_closed
	;
interface_definition_member
	: interface_definition_member_method
	;
javadoc
    : Javadoc /*Javadoc_start Javadoc_end*/
    ;
license_declaration
    : Comment_multiline
    ;
modifier_visibility
    : Keyword_public
    | Keyword_private
    ;
name
    /* This is needed, because token fragments can currently only be used in tokens.
     * Combined grammars would allow this, but it could not be getting worked.
     */
    : Name
    | Keyword_class
    | Keyword_JavaLegacyBody
    | Keyword_JavaLegacyArtifact
    ;
operator
	: Keysymbol_not_equals
	| Keysymbol_equals
	| Keysymbol_and
	| Keysymbol_vertical_bar
	| Less_than Equals?
	| Keyword_instanceof
	| Operator_plus
	| Hyphen_minus
	;
package_declaration
    : 'package' package_name Semicolon
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
    | name Arrow expression
    | name Arrow reference
    | name Arrow Brace_curly_open statement* Brace_curly_closed
    | call_arguments Arrow Brace_curly_open statement* Brace_curly_closed
    | call_arguments Arrow expression
    | call_arguments Arrow reference
    | String
    | String Operator_plus reference
    ;
statement
    : Line_comment
    | Keyword_try Brace_curly_open statement+ Brace_curly_closed statement_catch?
        statement_finally?
    | Keyword_if Brace_round_open expression Brace_round_closed
    	Brace_curly_open statement+ Brace_curly_closed statement_if_else?
    | javadoc
    | Keyword_throw expression Semicolon
    | Keyword_return expression Semicolon
    | expression Semicolon
    | variable_declaration (Equals expression)? Semicolon
    | name access Equals expression Semicolon
    | name Equals expression Semicolon
    | statement_for
    ;
statement_for
    : Keyword_for Brace_round_open variable_declaration (Equals expression)? Semicolon expression Semicolon expression Brace_round_closed statement_body
    | Keyword_for Brace_round_open variable_declaration Keysymbol_colon reference Brace_round_closed statement_body
    ;
statement_body
    : Brace_curly_open statement* Brace_curly_closed
    ;
statement_if_else
	: Keyword_else Brace_curly_open statement+ Brace_curly_closed
	| Keyword_else_if Brace_round_open expression Brace_round_closed
		Brace_curly_open statement+ Brace_curly_closed statement_if_else?
	;
statement_catch
    : Keyword_catch Brace_round_open name name
        Brace_round_closed Brace_curly_open statement+ Brace_curly_closed
    ;
statement_finally
    : Keyword_finally Brace_curly_open statement+ Brace_curly_closed
    ;
source_unit
    : license_declaration package_declaration import_declaration* class_definition
    	EOF
    | license_declaration package_declaration import_declaration* interface_definition
        EOF
    | license_declaration package_declaration import_declaration* interface_definition
    	EOF
    | license_declaration package_declaration import_declaration* annotation_definition
        EOF
    | license_declaration package_declaration import_declaration* enum_definition
            	EOF
    ;
type_declaration
    : type_path type_argument? Keysymbol_vararg? type_declaration_array?
    ;
type_declaration_array
    : Brackets_open Brackets_closed
    ;
type_argument
    : Less_than type_argument_content? Bigger_than
    ;
type_argument_content
    : type_argument type_argument_content_next?
    | type_argument_element type_argument_content_next?
    ;
type_argument_content_next
    : Comma type_argument type_argument_content_next?
    | Comma name type_argument_content_next?
    | Comma Question_mark type_argument_content_next?
    | Comma type_declaration  type_argument_content_next?
    ;
type_argument_element
	: type_name
	| type_name Keyword_extends type_argument_element type_argument?
	;
type_name
	: name
	| Question_mark
	;
type_path
    : name
    | type_path Dot name
    ;
variable_declaration
    : Keyword_final? type_declaration name
    ;

