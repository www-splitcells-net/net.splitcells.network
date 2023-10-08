parser grammar ProblemParser;
/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
options {
    tokenVocab=ProblemLexer;
}
@header {
    package net.splitcells.gel.ext.problem;
}
source_unit: Whitespace;