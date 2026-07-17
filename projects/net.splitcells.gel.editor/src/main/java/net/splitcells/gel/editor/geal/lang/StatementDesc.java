/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.lang;

import net.splitcells.gel.editor.lang.SourceCodeQuotation;

public sealed interface StatementDesc extends SourceCodeQuotation permits VariableDefinitionDesc, FunctionCallChainDesc {
}
