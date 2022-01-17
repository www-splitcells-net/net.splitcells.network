lexer grammar Java_11_lexer;
/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
/* Help:
 * Keep in mind, that error messages can be quite cryptic:
 * 2 Tokens with same content are not equals.
 *
 * This file has to to tokenize/match every character in a parsed files.
 * Otherwise there will be problems.
 *
 * Token matching appears in order of this file and only one token is matched
 * at a time for a given string.
 * Therefore, the more generic a token is, the more the token needs to be
 * moved back in general.
 *
 * Needs to start with upper case.
 */
@header {
    package net.splitcells.dem.source.code.antlr;
}
Annotation_JavaLegacyBody: 'JavaLegacyBody';

Arrow: '->';
Extension_Exception: 'extends RuntimeException';
Javadoc: '/**' .*? '*/';
Keysymbol_and: '&&';
Keysymbol_at: '@';
Keysymbol_equals: '==';
Keysymbol_not_equals: '!=';
Keysymbol_not: '!';
Keysymbol_vararg: '...';
Keyword_annotation: '@interface';
Keyword_class: 'class';
Keyword_catch: 'catch';
Keyword_default: 'default';
Keyword_else: 'else';
Keyword_else_if: 'else if';
Keyword_enum: 'enum';
Keyword_extends: 'extends';
Keyword_instanceof: 'instanceof';
Keyword_finally: 'finally';
Keyword_final: 'final';
Keyword_if: 'if';
Keyword_implements: 'implements';
Keyword_import: 'import';
Keyword_interface: 'interface';
Keyword_new: 'new';
Keyword_package: 'package';
Keyword_private: 'private';
Keyword_public: 'public';
Keyword_return: 'return';
Keyword_static: 'static';
Keyword_throw: 'throw';
Keyword_try: 'try';

Operator_plus: '+';

Bigger_than: '>';
Brace_curly_open: '{';
Brace_curly_closed: '}';
Brace_round_open: '(';
Brace_round_closed: ')';
Comma: ',';
Dot: '.';
Equals: '=';

Less_than: '<';
Question_mark: '?';
Comment_multiline: '/*' .*? '*/';
Line_comment: '//' .*? Line_ending;
fragment Line_ending: [\n\r]+;

fragment Name: [a-zA-Z_][a-zA-Z0-9_]*;
Semicolon: ';';
Whitespace: [ \t\n\r]+;
Quote: '"';
fragment String_content: [a-zA-Z0-9_-]*;
Integer: [-+]?[0-9]+;
Hyphen_minus: '-';