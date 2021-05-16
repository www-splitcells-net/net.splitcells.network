lexer grammar Java_11_lexer;
/* Keep in mind that error messages can be quite cryptic: 2 Tokens with same content are not equals. */
/* This file has to to tokenize/match every character in a parsed files. Otherwise there will be problems. */
/* Token matching appears in order of this file and only one token is matched at a time for a given string. */
/* Needs to start with upper case. */
@header {
    package net.splitcells.dem.source.code.antlr;
}
Arrow: '->';
Bigger_than: '>';
Brace_curly_open: '{';
Brace_curly_closed: '}';
Brace_round_open: '(';
Brace_round_closed: ')';
Comma: ',';
Dot: '.';
Equals: '=';
Javadoc: '/**' .*? '*/';
Keyword_class: 'class';
Keyword_final: 'final';
Keyword_import: 'import';
Keyword_new: 'new';
Keyword_package: 'package';
Keyword_private: 'private';
Keyword_public: 'public';
Keyword_return: 'return';
Keyword_static: 'static';
Less_than: '<';
Name
    : [a-zA-Z0-9_] [a-zA-Z0-9_]*
    ;
Semicolon
    : ';'
    ;
Whitespace
    : [ \t\n\r]+
    ;