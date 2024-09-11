lexer grammar NoCodeDenLexer;
/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
@header {
    package net.splitcells.dem.lang.perspective.no.code.antlr4;
}
Body_start: '<body>';
Body_end: '</body>';
Brace_round_open: '(';
Brace_round_closed: ')';
Brace_curly_open: '{';
Brace_curly_closed: '}';
Comma: ',';
Document_start: '<!DOCTYPE html>';
Dot: '.';
Equals: '=';
Function_call_start: '<span class="net-splitcells-dem-lang-perspective-no-code-function-call">';
Function_call_name_start: '<span class="net-splitcells-dem-lang-perspective-no-code-function-call-name">';
Function_call_argument_start: '<span class="net-splitcells-dem-lang-perspective-no-code-function-call-argument">';
Function_call_var_arg: '<span class="net-splitcells-dem-lang-perspective-no-code-var-arg">...</span>';
Greater_than: '>';
Lesser_than: '<';
Integer: [-+]?[0-9]+;
Html_start: '<html xmlns="http://www.w3.org/1999/xhtml">';
Html_end: '</html>';
Literal_start: '<span class="net-splitcells-dem-lang-perspective-no-code-literal">';
List_item_end: '</li>';
Name: NamePrefix NameSuffix*;
No_code_start: '<ol class="net-splitcells-dem-lang-perspective-no-code-lang">';
Ordered_list_end: '</ol>';
Semicolon: ';';
Slash: '/';
String: '"' [a-zA-Z0-9_]* '"';
String_value: [a-zA-Z0-9_]+;
Span_end: '</span>';
Variable_access_start: '<li class="net-splitcells-dem-lang-perspective-no-code-variable-access">';
Variable_definition_name_start: '<span class="net-splitcells-dem-lang-perspective-no-code-variable-name">';
Variable_definition_start: '<li class="net-splitcells-dem-lang-perspective-no-code-variable-definition">';
Variable_definition_value_start: '<span class="net-splitcells-dem-lang-perspective-no-code-variable-value">';
Variable_reference_start: '<span class="net-splitcells-dem-lang-perspective-no-code-variable-reference">';
Undefined: '<span class="net-splitcells-dem-lang-perspective-no-code-undefined">?</span>';

fragment NamePrefix: [a-zA-Z_];
fragment NameSuffix: [a-zA-Z0-9_\-];

/* Ignore whitespace. */
WS: [ \t\r\n]+ -> skip;