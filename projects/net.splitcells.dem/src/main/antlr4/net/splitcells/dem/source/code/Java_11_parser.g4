parser grammar Java_11_parser;
@header {
    package net.splitcells.dem.source.code.antlr;
}
options {
    tokenVocab=Java_11_lexer;
}
source_unit
    : package_declaration import_declaration* EOF
    ;
import_declaration
    : import_static_declaration
    | import_type_declaration
    ;
import_static_declaration
    : Whitespace* Keyword_import Whitespace Keyword_static Whitespace type_name Statement_terminator
    ;
import_type_declaration
    : Whitespace* Keyword_import Whitespace type_name Statement_terminator
    ;
package_declaration
    : Whitespace* Keyword_package Whitespace package_name Statement_terminator
    ;
type_name
    : Name
    | type_name Object_accessor Name
    ;
package_name
    : Name
    | package_name Object_accessor Name
    ;