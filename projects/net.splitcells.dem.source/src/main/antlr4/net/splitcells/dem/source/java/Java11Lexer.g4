lexer grammar Java11Lexer;
/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
/* Help:
 * The Tokenizers 4 main functions are:
 * * Ensure, that the parser only uses valid constants.
 * * Define valid string patterns used by the parser.
 * * Define which content to skip entirely.
 * * Ensure that every character of the input
 *   is matched to the 3 previous points and therefore all input is processed.
 *
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
 * Place fragment to end of file, because these are never used as tokens.
 *
 * Needs to start with upper case.
 *
 * Backslash characters always mark the start of an escape sequence in ANTLR.
 * This is also the case for strings like '\n', which is a new line character.
 *
 * TODO User wrapper for date time wrapper, in order to ease things like calculating durations.
 *
 * TODO Use only one wrapper for a date time with timezone type, in order to maximize accuracy.
 * Only create another type, if something like performances becomes important.
 */
@header {
    package net.splitcells.dem.source.java;
}
/* Keywords and Keysymbols */
    Legacy_Imports: Legacy_Import_Instances;
    fragment Legacy_Import_Instances
    		: 'import java.io.InputStream;'
    		| 'import java.nio.file.Path;'
    		| 'import java.nio.file.Paths;'
    		| 'import java.time.format.DateTimeFormatter;'
    		| 'import java.time.LocalDate;'
    		| 'import java.time.ZonedDateTime;'
    		| 'import java.time.Instant;'
    		| 'import java.util.function.Consumer;'
    		| 'import java.util.function.Supplier;'
    		| 'import java.util.function.Predicate;'
    		| 'import java.util.function.BiPredicate;'
    		| 'import java.util.function.BiFunction;'
    		| 'import java.util.Optional;'
    		| 'import java.util.regex.Pattern;'
    		| 'import java.util.stream.Stream;'
    		| 'import org.junit.jupiter.api.net.splitcells.Test;'
    		| 'import org.junit.jupiter.api.Tag;'
    		| 'import org.junit.jupiter.api.TestFactory;'
    		| 'import static org.assertj.core.api.Assertions.assertThat;'
    		| 'import static org.junit.jupiter.api.Assertions.assertThrows;'
    		| 'import static java.util.stream.IntStream.range;'
    		| 'import static java.util.stream.IntStream.rangeClosed;'
    		| 'import static java.util.stream.Stream.concat;'
    		| 'import java.util.function.Function;'
    		| 'import java.util.Iterator;'
    		| 'import java.util.ListIterator;'
    		| 'import java.util.Collection;'
    		| 'import lombok.Getter;'
    		| 'import lombok.Setter;'
    		| 'import lombok.experimental.Accessors;' /* TODO Remove Accessors. See Java guidelines. */
    		| 'import lombok.experimental.Delegate'
    		| 'import lombok.EqualsAndHashCode'
    		| 'import lombok.Data'
    		;
	Arrow: '->';
	Bigger_than: '>';
	Brace_curly_open: '{';
	Brace_curly_closed: '}';
	Brace_round_open: '(';
	Brace_round_closed: ')';
	Brackets_open: '[';
	Brackets_closed: ']';
	Comma: ',';
	Equals: '=';
	Equals_or: '|=';
	Equals_and: '&=';
	Extension_Exception: 'extends RuntimeException';
	Floating_point: [-+]?[0-9]+([_][0-9]+)*([\\.][_0-9]+)?[dflL]?;
	Floating_point_short: [-+]?[\\.][0-9]+[0-9_]*[dflL]?;
	Integer: [-+]?[0-9]+([_][0-9]+)*[dflL]?;
	Keysymbol_and: '&&';
	Keysymbol_at: '@';
	Keysymbol_function_reference: '::';
	Keysymbol_colon: ':';
	Keysymbol_equals: '==';
	Keysymbol_not_equals: '!=';
	Keysymbol_not: '!';
	Keysymbol_slash: '/';
	Keysymbol_star: '*';
	Keysymbol_vararg: '...';
	Keysymbol_vertical_bar: '||';
	Keyword_Java: 'java';
	Keyword_JavaLegacy: 'JavaLegacy';
	Keyword_abstract: 'abstract';
	Keyword_annotation: '@interface';
	Keyword_class: 'class';
	Keyword_case: 'case';
	Keyword_catch: 'catch';
	Keyword_default: 'default';
	Keyword_do: 'do';
	Keyword_else: 'else';
	Keyword_else_if: 'else if';
	Keyword_enum: 'enum';
	Keyword_extends: 'extends';
	Keyword_for: 'for';
	Keyword_instanceof: 'instanceof';
	Keyword_finally: 'finally';
	Keyword_final: 'final';
	Keyword_if: 'if';
	Keyword_implements: 'implements';
	Keyword_import: 'import';
	Keyword_interface: 'interface';
	Keyword_new: 'new';
	Keyword_package: 'package';
	Keyword_percent: '%';
	Keyword_permits: 'permits';
	Keyword_private: 'private';
	Keyword_protected: 'protected';
	Keyword_public: 'public';
	Keyword_return: 'return';
	Keyword_static: 'static';
	Keyword_sealed: 'sealed';
	Keyword_synchronized: 'synchronized';
	Keyword_super: 'super';
	Keyword_switch: 'switch';
	Keyword_throw: 'throw';
	Keyword_throws: 'throws';
	Keyword_try: 'try';
	Keyword_var: 'var';
	Keyword_while: 'while';
	Less_than: '<';
	Question_mark: '?';
	Operator_plus: '+';
	Semicolon: ';';
/* Multiple Line Token */
	Javadoc: '/**' .*? '*/';
	Comment_multiline: '/*' .*? '*/';
	Line_comment: '//' .*? Line_ending;
/* Generic Content */
	Name: [a-zA-Z_][a-zA-Z0-9_]*;
	TextBlock: '"""' TextBlock_character+ '"""';
	fragment TextBlock_character
		: [a-zA-Z0-9_-]
		| '\\"'
		| '\n'
		| '\r'
		| '\t'
		| '\\n'
		| '\\r'
		| '\\t'
		| '\\"'
		| '\\\\'
		| '.'
		| ' '
		| '<'
		| '>'
		| '?'
		| '='
		| ':'
		| '/'
		| ','
		| '['
		| ']'
		| '$'
		| '*'
		| '`'
		| '\''
		| '('
		| ')'
		| '^'
		| ';'
		| '+'
		| '#'
		| '!'
		| '&'
		| '~'
		| '_'
		| '-'
		| '{'
		| '}'
		| '|'
		| '™' /* TODO Maybe this should be done via dedicated static variables, that are code pointers? */
		| '@'
		| '%'
		| '^'
		| '"'
		;
	String
		:'"' String_character* '"';
	Char: '\'' String_character '\'';
	fragment String_character
		: [a-zA-Z0-9_-]
		| '\\"'
		| '\\n'
		| '\\r'
		| '\\t'
		| '.'
		| ' '
		| '<'
		| '>'
		| '?'
		| '='
		| ':'
		| '/'
		| '\\\\'
		| ','
		| '['
		| ']'
		| '$'
		| '*'
		| '`'
		| '\''
		| '('
		| ')'
		| '^'
		| ';'
		| '+'
		| '#'
		| '!'
		| '&'
		| '~'
		| '_'
		| '-'
		| '{'
		| '}'
		| '|'
		| '™' /* TODO Maybe this should be done via dedicated static variables, that are code pointers? */
		| '@'
		| '%'
		| '^'
		;
/* Tokens Of Last Resort */
	WS:
		/* Ignore whitespace. */
    	[ \t\r\n]+ -> skip;
/* Token Fragments */
    /* These are special cases of String patterns, that do not require a special token. */
	fragment Line_ending: [\n\r]+;
Digits: [0-9];
Dot: '.';
Hyphen_minus: '-';