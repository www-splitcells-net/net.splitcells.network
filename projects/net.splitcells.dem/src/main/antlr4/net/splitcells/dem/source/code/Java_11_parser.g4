parser grammar Java_11_parser;
/* source_unit is the root rule. */
/* A grammar files with implicit tokens, would be easier to write, understand and maintain.
 * Implicit tokens are not used, because they have caused many cryptic errors.
 * The author of this does not understand how implicit tokens really work in ANLTR4.
 */
@header {
    package net.splitcells.dem.source.code.antlr;
}
options {
    tokenVocab=Java_11_lexer;
}
call_arguments
    : Brace_round_open Brace_round_closed
    | Brace_round_open Whitespace? reference Whitespace? call_arguments_next* Whitespace? Brace_round_closed
    ;
call_arguments_next
    : Comma Whitespace reference
    ;
class_definition
    : javadoc? Whitespace? Keyword_public? Whitespace? Keyword_final? Whitespace? Keyword_class? Whitespace? Name
        Whitespace? Brace_curly_open Whitespace? class_member* Whitespace? Brace_curly_closed
    ;
class_member
    : class_member_method_definition
    | class_member_value_declaration
    ;
class_member_method_definition
    : javadoc? Whitespace? modifier_visibility? Whitespace? Keyword_static? Whitespace? type_declaration Whitespace?
        Name Whitespace? call_arguments Whitespace? Brace_curly_open Whitespace? statement* Whitespace? Brace_curly_closed
    ;
class_member_value_declaration
    : javadoc? Whitespace? Keyword_private? Whitespace? Keyword_static? Whitespace? Keyword_final? Whitespace?
        type_declaration? Whitespace? Name Whitespace? Equals Whitespace? statement?
    ;
expression
    : Keyword_new Whitespace? type_declaration call_arguments
    | Name Whitespace? call_arguments
    | variable_declaration
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
javadoc
    : Javadoc /*Javadoc_start Javadoc_end*/ Whitespace?
    ;
modifier_visibility
    : Keyword_public
    | Keyword_private
    ;
package_declaration
    : 'package' Whitespace package_name Semicolon Whitespace*
    ;
package_name
    : Name
    | package_name Dot Name
    ;
reference
    : expression
    | Name
    /* This is an Lambda definition. */
    | Name Whitespace? Arrow Whitespace? reference
    | Name Whitespace? Arrow Whitespace? Brace_curly_open Whitespace? statement* Whitespace? Brace_curly_closed
    ;
statement
    : Keyword_return? Whitespace expression Semicolon
    | Whitespace? Line_comment Whitespace?
    ;
source_unit
    : package_declaration import_declaration* Whitespace? class_definition EOF
    ;
type_declaration
    : Name type_argument?
    ;
type_argument
    : Less_than Whitespace? type_argument_content? Whitespace? Bigger_than
    ;
type_argument_content
    : type_argument Whitespace? type_argument_content_next?
    | Name Whitespace? type_argument_content_next?
    ;
type_argument_content_next
    : Comma Whitespace? type_argument Whitespace? type_argument_content_next?
    | Comma Whitespace? Name Whitespace? type_argument_content_next?
    ;
type_path
    : Name
    | type_path Dot Name
    ;
variable_declaration
    : type_declaration Whitespace Name
    ;

