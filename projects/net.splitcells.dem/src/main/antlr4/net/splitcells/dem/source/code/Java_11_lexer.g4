lexer grammar Java_11_lexer;
@header {
    package net.splitcells.dem.source.code.antlr;
}
Statement_terminator: ';';
Whitespace: ' ';
Keyword_package: 'package';
Keyword_import: 'import';
Object_accessor: '.';
Name
	: Name_prefix Name_suffix*
	;
fragment Name_suffix: [a-zA-Z0-9_];
fragment Name_prefix: [a-zA-Z];