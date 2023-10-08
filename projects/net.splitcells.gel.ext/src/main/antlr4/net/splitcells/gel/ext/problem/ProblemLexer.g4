lexer grammar ProblemLexer;
/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
 @header {
     package net.splitcells.gel.ext.problem;
 }
Brace_round_open: '(';
Brace_round_closed: ')';
Whitespace: [ \t\r\n]+;

fragment Name: [a-zA-Z_][a-zA-Z0-9_]*;
