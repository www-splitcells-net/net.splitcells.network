parser grammar Java11Parser;
/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
 /*
  * TODO Create JavaDoc grammar.
  */
/* source_unit is the root rule.
 *
 * Implementation note:
 * One grammar file with implicit tokens, would be easier to write, understand and maintain.
 * Implicit tokens are not used, because they have caused many cryptic errors in the past.
 * The author of this does not understand how implicit tokens really work in ANLTR4.
 *
 * Empty blocks like `if ([...]) {}` are allowed, as these can be part of valid, but incomplete code,
 * that is in development.
 */
@header {
    package net.splitcells.dem.source.java;
}
options {
    tokenVocab=Java11Lexer;
}
/* This is the root node. Root nodes should be placed on the start of the document.
 *
 * Normally, one would have to add the rule `(.)*? Keysymbol_at Keyword_JavaLegacyArtifact (.)*? EOF` to this rule,
 * but this is avoided by the calling Java code, that checks for this property manually instead.
 * This is done, because such a rule breaks all error messages of the ANTLR4 parser.
 * As this rule basically says,
 * that no grammar applies to a source code file containing `@JavaLegacyArtifact`,
 * this is also a rule, we can ignore.
 */
source_unit
    : license_declaration package_declaration import_declaration* class_definition
    	EOF
    | license_declaration package_declaration import_declaration* interface_definition
        EOF
    | license_declaration package_declaration import_declaration* annotation_definition
        EOF
    | license_declaration package_declaration import_declaration* enum_definition
       EOF
    ;
access
    : inline_comment? Dot type_argument? name call_arguments access? inline_comment?
    | inline_comment? Dot name access? inline_comment?
    | Brackets_open expression Brackets_closed access?
    ;
annotation
    /* TODO call_arguments is a hack. */
	: Keysymbol_at name annotation_arguments?;
annotation_arguments
    : Brace_round_open Brace_round_closed
    | Brace_round_open annotation_arguments_element annotation_arguments_next* Brace_round_closed
    ;
annotation_arguments_element
    : Line_comment* annotation? (name Equals)? reference
    | Line_comment* annotation? (name Equals)? expression
    | Line_comment* annotation? variable_declaration
    ;
annotation_arguments_next
    : Comma annotation_arguments_element
    ;
/* TODO Using interface_definition_member is a hack. */
annotation_definition
    : javadoc? annotation* Keyword_public Keyword_annotation* name
        Brace_curly_open interface_definition_member* Brace_curly_closed
    ;
call_arguments
    : Brace_round_open Brace_round_closed
    | Brace_round_open call_arguments_element call_arguments_next* Brace_round_closed
    ;
call_arguments_element
    : Line_comment* annotation? reference
    | Line_comment* annotation? expression
    | Line_comment* annotation? variable_declaration
    ;
call_arguments_next
    : Comma call_arguments_element
    ;
class_definition
    : javadoc? Keysymbol_at Keyword_JavaLegacyBody (.)*?
    | javadoc? annotation* Keyword_public? Keyword_final?
        /* Keyword_abstract? TODO Is this really needed? */
    	Keyword_class
    	type_declaration
    	class_extension
        Brace_curly_open class_member* Brace_curly_closed
    ;
class_extension
	: (Keyword_extends type_declaration)? (Keyword_implements type_declaration (Comma type_declaration)* )? Extension_Exception?
	;
class_member
    : javadoc? Keysymbol_at Keyword_JavaLegacyBody (.)*? Brace_curly_open (.)*? Brace_curly_closed
    | class_constructor
    | class_member_class_definition
    | class_member_method_definition
    | class_member_value_declaration
    | Comment_multiline
    | Line_comment
    | Brace_curly_open statement+ Brace_curly_closed
    ;
class_member_class_definition
    : javadoc?
      annotation*
      Keyword_private? Keyword_public? Keyword_static Keyword_final?
      Keyword_class
      type_declaration
      class_extension
      Brace_curly_open class_member* Brace_curly_closed
    ;
class_constructor
    : javadoc? annotation* Keyword_private name call_arguments statement_body
    /* This is needed for resource classes, so that instance of these can be created via reflection. */
    | javadoc? annotation* Keyword_public name call_arguments statement_body
    ;
class_member_method_definition
    : javadoc? annotation* modifier_visibility? Keyword_static? Keyword_synchronized?
    	type_argument? type_declaration
        name call_arguments  class_member_method_definition_throws? Brace_curly_open statement*
        Brace_curly_closed
    ;
class_member_method_definition_throws : Keyword_throws name;
class_member_value_declaration
    : javadoc? annotation* modifier_visibility? Keyword_static? Keyword_final?
        type_declaration? name Equals statement
    | javadoc? annotation* modifier_visibility? Keyword_static? Keyword_final?
              type_declaration? name Semicolon
    ;
enum_constructor
    : javadoc? annotation* name call_arguments statement_body
    /* This is needed for resource classes, so that instance of these can be created via reflection. */
    | javadoc? annotation* Keyword_public name call_arguments statement_body
    ;
enum_definition
	/* TODO Create own enum grammar destinct from class. */
    : javadoc? annotation* Keyword_public? Keyword_enum
    	name
    	(Keyword_implements type_declaration)?
        Brace_curly_open enum_values enum_members*
        Brace_curly_closed
    ;
enum_members
    : enum_constructor
    | class_member
    ;
enum_values
	: Javadoc? annotation? name call_arguments? enum_values_next? Semicolon
	;
enum_values_next
    : Comma Javadoc? annotation? name call_arguments? enum_values_next?
    ;
expression
    : integer
    | Brace_round_open type_declaration
    	Brace_round_closed expression_child?
    	/* Only upcasting should be done. */
    | Brace_round_open expression
    	Brace_round_closed expression_child?
    | TextBlock access?
    | String access?
    | Char access?
    | expression Keyword_instanceof type_declaration name?
    | expression operator expression
    | prefix_operator expression
    | Keyword_new type_declaration call_arguments
        	Brace_curly_open class_member* Brace_curly_closed access?
    | Keyword_new type_declaration Brackets_open expression Brackets_closed access?
    | Keyword_new type_declaration call_arguments access?
    | function_call access?
    | name access?
    | expression operator expression
    | expression Hyphen_minus Hyphen_minus
    | expression Operator_plus Operator_plus
    | expression_with_prefix
    | Hyphen_minus expression
    | lambda
    | statement_switch
    ;
expression_with_prefix
    : Operator_plus Operator_plus expression
    | Hyphen_minus Hyphen_minus expression
    ;
expression_child
	: access
	| expression
	;
function_call: name call_arguments inline_comment?;
import_declaration
    : import_static_declaration
    | import_type_declaration
    ;
import_static_declaration
    : Keyword_import Keyword_static type_path (Dot Keysymbol_star)? Semicolon
    ;
import_type_declaration
    : Keyword_import type_path (Dot Keysymbol_star)? Semicolon
    | Legacy_Imports
    ;
inline_comment
    : Line_comment
    | Comment_multiline
    | Javadoc
    ;
integer
	: Integer
	| Floating_point
	| Floating_point_short
	| Hyphen_minus Digits
	;
/* Sealed classes are only allowed for providing a list of things, where only exactly one element is present.
 * So, when a class contains multiple optional attributes, where exactly one is set at any given time,
 * sealed classes aka union types can be used.
 * This is also useful for lists, that have to contain a fixed set of types.
 * Sealed classes are not intended to be used as an replacement for interfaces or polymorphism.
 */
interface_definition
    : javadoc? annotation* Keyword_public? Keyword_final? Keyword_interface?
    	name
    	type_argument?
    	interface_extension?
        Brace_curly_open interface_definition_member* Brace_curly_closed
    | javadoc? annotation* Keyword_public? Keyword_sealed? Keyword_interface?
        name
        interface_extension?
        interface_permits
        Brace_curly_open interface_definition_member* Brace_curly_closed
    ;
interface_permits: Keyword_permits name (Comma name)*;
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
    | javadoc? annotation* modifier_visibility? Keyword_static
		type_argument? type_declaration
		name call_arguments
		Brace_curly_open statement* Brace_curly_closed
	| javadoc? annotation* (Keyword_default | Keyword_private)
		type_argument? type_declaration
		name call_arguments
		Brace_curly_open statement* Brace_curly_closed
    | interface_definition_member_static
    ;
interface_definition_member_static
	: javadoc? annotation* type_declaration? name Equals reference Semicolon
	| javadoc? annotation* type_declaration? name
		Brace_curly_open statement* Brace_curly_closed
	;
interface_definition_member
	: interface_definition_member_method
	| javadoc? Keysymbol_at Keyword_JavaLegacyBody (.)*?
	;
javadoc
    : Javadoc /*Javadoc_start Javadoc_end*/
    ;
lambda
	: name Arrow expression
    | name Arrow reference
    | name Arrow Brace_curly_open statement* Brace_curly_closed
    | call_arguments Arrow Brace_curly_open statement* Brace_curly_closed
    | call_arguments Arrow expression
    | call_arguments Arrow reference
    ;
license_declaration
    : Comment_multiline
    ;
modifier_visibility
    : Keyword_public
    | Keyword_private
    | Keyword_protected
    ;
name
    /* This is needed, because token fragments can currently only be used in tokens.
     * Combined grammars would allow this, but it could not be getting worked.
     */
    : Name
    | Keyword_class
    | Keyword_JavaLegacyBody
    | Keyword_JavaLegacyArtifact
    | Keyword_super
    | Keyword_var
    | Keyword_Java
    ;
operator
	: Keysymbol_not_equals
	| Keysymbol_equals
	| Keysymbol_and
	| Keysymbol_vertical_bar
	| Less_than Equals?
	| Bigger_than Equals?
	| Keyword_instanceof
	| Operator_plus
	| Hyphen_minus
	| Keysymbol_star
	| Keysymbol_slash
	;
package_declaration
    : Keyword_package package_name Semicolon
    ;
package_name
    : name
    | package_name Dot name
    ;
prefix_operator
	: Keysymbol_not
	;
/* TODO It seems that reference should be just part of expression.
 * The distinction between expression and reference does not seem to have a purpose.
 * Furthermore, the term reference seems to be incorrect.
 */
reference
	: expression
    | lambda
    | TextBlock
    | String
    | String Operator_plus reference
    | reference Keysymbol_function_reference type_argument? name
    ;
statement
    : Line_comment
    | Comment_multiline
    | Keyword_try (Brace_round_open variable_declaration Equals expression Brace_round_closed)? Brace_curly_open statement+ Brace_curly_closed statement_catch*
        statement_finally?
    | Keyword_try Brace_round_open variable_declaration Brace_round_closed Brace_curly_open statement+ Brace_curly_closed statement_catch?
            statement_finally?
    | Keyword_if Brace_round_open expression Brace_round_closed
    	Brace_curly_open statement* Brace_curly_closed statement_if_else?
    | Keyword_if Brace_round_open expression Brace_round_closed statement
    | javadoc
    | Keyword_throw expression Semicolon
    | Keyword_return (expression | reference | name Operator_plus Equals expression | name Hyphen_minus Equals expression)? Semicolon
    | name Operator_plus Equals expression Semicolon
    | name Hyphen_minus Equals expression Semicolon
    | name Keysymbol_star Equals expression Semicolon
    | expression Semicolon
    | variable_declaration (Equals expression)? Semicolon
    | name access Equals expression Semicolon
    | name Equals expression Semicolon
    | name Equals_and expression Semicolon
    | name Equals_or expression Semicolon
    | statement_for
    | Brace_curly_open statement+ Brace_curly_closed
    | Keyword_while Brace_round_open expression Brace_round_closed statement_body
    | Keyword_do statement_body Keyword_while Brace_round_open expression Brace_round_closed Semicolon
    | annotation statement
    | statement_switch
    | Keyword_synchronized Brace_round_open expression Brace_round_closed statement_body
    ;
statement_switch
    : Keyword_switch Brace_round_open call_arguments_element Brace_round_closed
        Brace_curly_open statement_switch_case_by_type+ Brace_curly_closed
    | Keyword_switch Brace_round_open call_arguments_element Brace_round_closed
        Brace_curly_open statement_switch_case_by_value+ Brace_curly_closed
    ;
statement_switch_case_by_type
    : Keyword_case type_declaration name Arrow Brace_curly_open statement* Brace_curly_closed
    | Keyword_case type_declaration name Arrow statement
    | Keyword_default Arrow Brace_curly_open statement* Brace_curly_closed
    | Keyword_default Arrow statement
    ;
statement_switch_case_by_value
    : Keyword_default Arrow statement
    | Keyword_default Keysymbol_colon statement
    | Keyword_case String (Comma String)* Arrow statement
    | Keyword_case String (Comma String)* Keysymbol_colon statement
    ;
statement_for
    : Keyword_for Brace_round_open variable_declaration (Equals expression)? Semicolon expression Semicolon expression Brace_round_closed statement_body
    | Keyword_for Brace_round_open variable_declaration Keysymbol_colon reference Brace_round_closed statement_body
    ;
statement_body
    : Brace_curly_open statement* Brace_curly_closed
    ;
statement_if_else
	: Keyword_else Brace_curly_open statement* Brace_curly_closed
	| Keyword_else_if Brace_round_open expression Brace_round_closed
		Brace_curly_open statement* Brace_curly_closed statement_if_else?
	;
statement_catch
    : Keyword_catch Brace_round_open name name
        Brace_round_closed Brace_curly_open statement+ Brace_curly_closed
    ;
statement_finally
    : Keyword_finally Brace_curly_open statement+ Brace_curly_closed
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
    | Comma type_declaration type_argument_content_next?
    | Comma type_argument_element type_argument_content_next?
    | Comma type_path type_argument?
    ;
type_argument_element
	: type_name
	| type_name Extension_Exception
	| type_name Keyword_extends type_argument_element type_argument?
	| type_name Keyword_super type_argument_element type_argument?
	| Question_mark
	| type_path type_argument?
	| type_name Brackets_open Brackets_closed
	;
type_name
	: name
	| Question_mark
	;
type_path
    /* References to foreign code like the standard Java library are only allowed in legacy code. */
    : name
    | name Dot type_path
    ;
variable_declaration
    : Keyword_final? type_declaration name
    | Keyword_final? Keyword_var name
    ;

