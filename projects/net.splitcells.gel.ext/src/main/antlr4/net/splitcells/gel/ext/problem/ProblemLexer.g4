lexer grammar ProblemLexer;
/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
 @header {
     package net.splitcells.gel.ext.problem;
 }
Brace_round_open: '(';
Brace_round_closed: ')';
Brace_curly_open: '{';
Brace_curly_closed: '}';
Comma: ',';
Dot: '.';
Equals: '=';
Name: NamePrefix NameSuffix*;
Semicolon: ';';
String: '"' [a-zA-Z0-9_]* '"';
Whitespace: [ \t\r\n]+;

fragment NamePrefix: [a-zA-Z_];
fragment NameSuffix: [a-zA-Z0-9_];

/* Tokens Of Last Resort */
	WS:
		/* Ignore whitespace. */
    	[ \t\r\n]+ -> skip;