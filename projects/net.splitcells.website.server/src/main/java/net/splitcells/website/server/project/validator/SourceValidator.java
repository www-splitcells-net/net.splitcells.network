/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.project.validator;

import java.nio.file.Path;
import java.util.Optional;

@FunctionalInterface
public interface SourceValidator {

    SourceValidator VOID_VALIDATOR = arg -> Optional.empty();

    Optional<String> validate(String validationSubject);
}
