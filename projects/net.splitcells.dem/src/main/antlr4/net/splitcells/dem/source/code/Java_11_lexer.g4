lexer grammar Java_11_lexer;
@header {
    package net.splitcells.dem.source.code.antlr;
}
Bigger_than: '>';
Brace_round_open: '(';
Brace_round_closed: ')';
Comma: ',';
Dot: '.';
Equals: '=';
Keyword_class: 'class';
Keyword_final: 'final';
Keyword_import: 'import';
Keyword_package: 'package';
Keyword_private: 'private';
Keyword_public: 'public';
Keyword_new: 'new';
Keyword_return: 'return';
Keyword_static: 'static';
Javadoc_start: '/**';
Javadoc_end: '*/';
Less_than: '<';
Minus: '-';
Scope_start: '{';
Scope_end: '}';
Statement_terminator: ';';
Whitespace: [ \t\n\r]+;

Name: Name_prefix Name_suffix*;
fragment Name_suffix: [a-zA-Z0-9_];
fragment Name_prefix: [a-zA-Z];

Javadoc_content: Javadoc_content_character;
fragment Javadoc_content_character
    : [\n\r\t @*{}/a-zA-Z];