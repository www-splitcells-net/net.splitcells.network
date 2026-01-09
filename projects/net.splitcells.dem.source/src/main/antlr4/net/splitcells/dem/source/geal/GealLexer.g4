lexer grammar GealLexer;
/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
@header {
    package net.splitcells.dem.source.geal;
}
Brace_round_open: '(';
Brace_round_closed: ')';
Brace_curly_open: '{';
Brace_curly_closed: '}';
Comma: ',';
Dot: '.';
Equals: '=';
Integer: [-+]?[0-9]+;
Name: NamePrefix NameSuffix*;
Semicolon: ';';
// Single quote strings do not support escape symbols just like in many other programming languages.
String: '\'' [a-zA-Z0-9_\- \\.]* '\'';

fragment NamePrefix: [a-zA-Z_];
fragment NameSuffix: [a-zA-Z0-9_];

/* Ignore whitespace. */
WS: [ \t\r\n]+ -> skip;