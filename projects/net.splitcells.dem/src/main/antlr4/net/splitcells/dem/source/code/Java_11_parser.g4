parser grammar Java_11_parser;
@header {
    package net.splitcells.dem.source.code.antlr;
}
options {
    tokenVocab=Java_11_lexer;
}
source_unit
    : package_declaration import_declaration* class_definition EOF
    ;
class_definition
    : javadoc? Whitespace? Keyword_public? Whitespace? Keyword_final? Whitespace? Keyword_class? Whitespace? Name Whitespace? Scope_start Whitespace? Scope_end
    ;
javadoc
    : Javadoc_start javadoc_content* Javadoc_end Whitespace*
    ;
javadoc_content
    : Object_accessor
    | Statement_terminator
    | Whitespace
    | Keyword_package
    | Keyword_import
    | Keyword_static
    | Javadoc_start
    | Name
    | Javadoc_content
    ;
import_declaration
    : import_static_declaration
    | import_type_declaration
    ;
import_static_declaration
    : Keyword_import Whitespace Keyword_static Whitespace type_name Statement_terminator Whitespace*
    ;
import_type_declaration
    : Keyword_import Whitespace type_name Statement_terminator Whitespace*
    ;
package_declaration
    : Keyword_package Whitespace package_name Statement_terminator Whitespace*
    ;
type_name
    : Name
    | type_name Object_accessor Name
    ;
package_name
    : Name
    | package_name Object_accessor Name
    ;