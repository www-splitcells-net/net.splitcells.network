lexer grammar Java_11_lexer;
@header {
    package net.splitcells.dem.source.code.antlr;
}
Object_accessor: '.';
Statement_terminator: ';';
Whitespace: [ \t\n\r]+;
Keyword_package: 'package';
Keyword_import: 'import';
Keyword_static: 'static';
Keyword_public: 'public';
Keyword_final: 'final';
Keyword_class: 'class';
Scope_start: '{';
Scope_end: '}';
Javadoc_start: '/**';
Javadoc_end: '*/';
fragment Name_suffix: [a-zA-Z0-9_];
fragment Name_prefix: [a-zA-Z];
Name: Name_prefix Name_suffix*;
Javadoc_content: Javadoc_content_character;
fragment Javadoc_content_character: [\n\r\t @*{}/a-zA-Z];