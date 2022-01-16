grammar Java11Grammar;
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
 /* TODO Consider using BND grammar like JavaCC, because ANTLR4 is too complicated:
  * * https://javacc.github.io/javacc/documentation/bnf.html
  * * https://en.wikipedia.org/wiki/Backus%E2%80%93Naur_form
  *
  * New Grammars should be written in BNF.
  */
/* source_unit is the root rule. */
/* Grammar guide lines:
 * A grammar files with implicit tokens, would be easier to write, understand and maintain.
 * Implicit tokens are not used, because they have caused many cryptic errors.
 * The author of this does not understand how implicit tokens really work in ANLTR4.
 *
 * Whitespaces are not parsed at the end of the rule, if the whitespace is not required, in order to not
 * take away whitespace from rules, which need them.
 * Whitespace skipping is not used, because otherwise it is hard to allow an arbitrary amount of whitespace between
 * things.
 */
@header {
package net.splitcells.dem.source.code.antlr;
}

access
    : '.' Name call_arguments access?
    | '.' Name access?
    ;
allowed_Extensions
	: 'extends' 'RuntimeException'
	;
annotation
	: '@' Name;
annotation_definition
    : javadoc? 'public' '@interface' Name '{' '}'
    ;
call_arguments
    : '(' ')'
    | '(' call_arguments_element call_arguments_next* ')'
    ;
call_arguments_element
    : reference
    | variable_declaration
    ;
call_arguments_next
    : ',' call_arguments_element
    ;
class_definition
    : javadoc? 'public'? 'final'? 'class'
    	type_declaration
    	class_extension
        '{' class_member* '}'
    ;
class_extension
	: 'extends' type_declaration
	| 'implements' type_declaration
	| allowed_Extensions?
	;
class_member
    : class_constructor
    | class_member_method_definition
    | class_member_value_declaration
    ;
class_constructor
    : annotation? 'private' Name call_arguments statement_body
    ;
class_member_method_definition
    : javadoc? annotation? modifier_visibility? 'static'
    	type_argument? type_declaration
        Name call_arguments '{' statement* '}'
    ;
class_member_value_declaration
    : javadoc? modifier_visibility? 'static'? 'final'?
        type_declaration? Name Equals statement
    | javadoc? modifier_visibility? 'static' 'final'
              type_declaration? Name ';'
    ;
enum_definition
	/* TODO Create own enum grammar destinct from class. */
    : javadoc? 'public'? 'enum' Name '{' enum_values class_member*'}'
    ;
enum_values
	: Name enum_values_next ';'
	;
enum_values_next
    : ',' Name enum_values_next?
    ;
expression
    : Integer
    | '(' type_declaration ')' expression_child?
    	/* Only upcasting should be done. */
    | '(' expression ')' expression_child?
    | string access?
    | expression operator expression
    | prefix_operator expression
    | 'new' type_declaration call_arguments
        	'{' class_member* '}' access?
    | 'new' type_declaration call_arguments access?
    | Name call_arguments access?
    | Name access?
    | expression '+' expression
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
    : 'import' 'static' type_path ';'
    ;
import_type_declaration
    : 'import' type_path ';'
    ;
Integer
	: [-+]?[0-9]+
	;
interface_definition
    : javadoc? 'public'? 'final'? 'interface'
    	Name type_argument? interface_extension? 'extends' type_declaration
    	'{' interface_definition_member* '}'
    ;
interface_definition_member_method
    : javadoc?
    	annotation? modifier_visibility? 'static'?
    	type_argument? type_declaration
        Name call_arguments ';'
    | javadoc? annotation? 'static'
		type_argument? type_declaration
		Name call_arguments
		'{' statement* '}'
	| javadoc? annotation? 'default'
		type_argument? type_declaration
		Name call_arguments
		'{' statement* '}'
    | interface_definition_member_static
    ;
interface_definition_member_static
	: javadoc? type_declaration? Name '=' statement
	| javadoc? type_declaration? Name
		'{' statement* '}'
	;
interface_definition_member
	: interface_definition_member_method
	;
interface_extension
	: 'extends' type_declaration
	;
javadoc
    : Javadoc /*Javadoc_start Javadoc_end*/
    ;
license_declaration
    : '/*' .* '*/'
    ;
fragment Line_comment
	: '//' .* [\n\r]+
	;
modifier_visibility
    : 'public'
    | 'private'
    ;
operator
	: '!='
	| '='
	| '&&'
	| '<'
	| 'instanceof'
	;
package_declaration
    : 'package' package_name ';'
    ;
package_name
    : Name
    | package_name '.' Name
    ;
prefix_operator
	: '!'
	;
fragment Name: [a-zA-Z_][a-zA-Z0-9_]*;
reference
	: expression
    /* This is an Lambda definition. */
    | Name '->' reference
    | Name '->' '{'  statement* '}'
    | call_arguments '->' '{' statement* '}'
    ;
statement
    : Line_comment
    | 'try' '{' statement+ '}' statement_catch? statement_finally?
    | 'if' '(' expression ')'
    	'{' statement+ '}' statement_if_else?
    | javadoc
    | 'throw' expression ';'
    | 'return' expression ';'
    | expression ';'
    | variable_declaration ('=' expression)? ';'
    | Name access '=' expression ';'
    | Name '=' expression ';'
    ;
statement_body
    : '{' statement* '{'
    ;
statement_if_else
	: 'else' '{' statement+ '}'
	| 'elseif' '(' expression '}' '{' statement+ '}' statement_if_else?
	;
statement_catch
    : 'catch' '(' Name Name')' '{' statement+ '}'
    ;
statement_finally
    : 'finally' '{' statement+ '}'
    ;
source_unit
    : license_declaration package_declaration import_declaration*
    	class_definition EOF
    | license_declaration package_declaration import_declaration*
    	interface_definition EOF
    | license_declaration package_declaration import_declaration*
    	interface_definition EOF
    | license_declaration package_declaration import_declaration*
    	annotation_definition EOF
    | license_declaration package_declaration import_declaration*
    	enum_definition EOF
    ;
string
	: '"' String_content '"'
	;
fragment String_content
	: [a-zA-Z0-9_-]*
	;
type_declaration
    : type_path type_argument? '...'?
    ;
type_argument
    : '<' type_argument_content? '>'
    ;
type_argument_content
    : type_argument type_argument_content_next?
    | type_argument_element type_argument_content_next?
    ;
type_argument_content_next
    : ',' type_argument type_argument_content_next?
    | ',' Name type_argument_content_next?
    ;
type_argument_element
	: type_name
	| type_name 'extends' type_argument_element type_argument?
	;
type_name
	: Name
	| '?'
	;
type_path
    : Name
    | type_path '.' Name
    ;
variable_declaration
    : 'final' type_declaration Name
    ;
WS
	: [ \t\r\n]+ -> skip
	;