parser grammar Java_11_parser;
/* source_unit is the root rule. */
@header {
    package net.splitcells.dem.source.code.antlr;
}
options {
    tokenVocab=Java_11_lexer;
}
call_arguments
    : Brace_round_open Brace_round_closed
    | Brace_round_open Whitespace? variable_declaration Whitespace? call_arguments_next* Whitespace? Brace_round_closed
    ;
call_arguments_next
    : Comma Whitespace variable_declaration
    ;
class_definition
    : javadoc? Whitespace? Keyword_public? Whitespace? Keyword_final? Whitespace? Keyword_class? Whitespace? Name
        Whitespace? Scope_start Whitespace? class_member* Whitespace? Scope_end
    ;
class_member
    : class_member_method_definition
    | class_member_value_declaration
    ;
class_member_method_definition
    : javadoc? Whitespace? modifier_visibility? Whitespace? Keyword_static? Whitespace? type_declaration Whitespace?
        Name Whitespace? call_arguments Whitespace? Scope_start Whitespace? Scope_end
    ;
class_member_value_declaration
    : javadoc? Whitespace? Keyword_private? Whitespace? Keyword_static? Whitespace? Keyword_final? Whitespace?
        type_declaration? Whitespace? Name Whitespace? Equals Whitespace? statement?
    ;
import_declaration
    : import_static_declaration
    | import_type_declaration
    ;
import_static_declaration
    : Keyword_import Whitespace Keyword_static Whitespace type_path Statement_terminator Whitespace*
    ;
import_type_declaration
    : Keyword_import Whitespace type_path Statement_terminator Whitespace*
    ;
javadoc
    : Javadoc_start javadoc_content* Javadoc_end Whitespace*
    ;
javadoc_content
    : Dot
    | Statement_terminator
    | Whitespace
    | Keyword_package
    | Keyword_import
    | Keyword_static
    | Javadoc_start
    | Name
    | Javadoc_content
    | Comma
    | Less_than
    | Bigger_than
    | Brace_round_open
    | Brace_round_closed
    ;
modifier_visibility
    : Keyword_public
    | Keyword_private
    ;
package_declaration
    : Keyword_package Whitespace package_name Statement_terminator Whitespace*
    ;
package_name
    : Name
    | package_name Dot Name
    ;
statement
    : Keyword_new Whitespace? type_declaration call_arguments Statement_terminator
    ;
source_unit
    : package_declaration import_declaration* class_definition EOF
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