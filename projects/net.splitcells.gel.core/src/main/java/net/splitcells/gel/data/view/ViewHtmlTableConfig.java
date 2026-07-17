/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.view;

import java.util.Optional;

public class ViewHtmlTableConfig {
    public static ViewHtmlTableConfig viewHtmlTableConfig() {
        return new ViewHtmlTableConfig();
    }
    private Optional<String> tablePopupViaColumnContent = Optional.empty();
    private ViewHtmlTableConfig() {

    }
    public Optional<String> tablePopupViaColumnContent() {
        return tablePopupViaColumnContent;
    }

    public ViewHtmlTableConfig withTablePopupViaColumnContent(Optional<String> arg) {
        tablePopupViaColumnContent = arg;
        return this;
    }
}
