parser grammar Java_11_parser;
@header {
    package net.splitcells.dem.source.code.antlr;
}
options {
    tokenVocab=Java_11_lexer;
}
source_unit
    : package_declaration Whitespace* EOF
    ;
package_declaration
    : Keyword_package Whitespace+ package_name Statement_terminator
    ;
package_name
    : Name
    | package_name Object_accessor Name
    ;