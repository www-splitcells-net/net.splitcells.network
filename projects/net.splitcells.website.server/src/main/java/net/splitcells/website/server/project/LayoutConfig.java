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
package net.splitcells.website.server.project;

import net.splitcells.dem.lang.tree.Tree;

import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;

/**
 * This config contains the metadata of a path/document,
 * which is used in order to improve its rendering.
 */
public class LayoutConfig {
    public static LayoutConfig layoutConfig(String path) {
        return new LayoutConfig(path);
    }

    private final String path;
    private Optional<String> title = Optional.empty();

    private Optional<Tree> localPathContext = Optional.empty();

    private Optional<Tree> relevantLocalPathContext = Optional.empty();

    private LayoutConfig(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }

    public Optional<String> title() {
        return title;
    }

    /**
     *
     * @return These are all paths, that are children, to {@link #path},
     * and which can be requested from relevant {@link Renderer}.
     */
    public Optional<Tree> localPathContext() {
        return localPathContext;
    }

    /**
     *
     * @return These are all paths, that are children, to {@link #path},
     * which can be requested from relevant {@link Renderer} and
     * which are relevant to the user.
     */
    public Optional<Tree> relevantLocalPathContext() {
        return relevantLocalPathContext;
    }

    public LayoutConfig withTitle(String title) {
        this.title = Optional.of(title);
        return this;
    }

    /**
     * This should only be set by the {@link Renderer}.
     *
     * @param localPathContext The Local Path Context
     * @return This
     */
    public LayoutConfig withLocalPathContext(Optional<Tree> localPathContext) {
        this.localPathContext = localPathContext;
        return this;
    }

    /**
     * This should only be set by the {@link Renderer}.
     *
     * @param relevantLocalPathContext The Relevant Local Path Context
     * @return This
     */
    public LayoutConfig withRelevantLocalPathContext(Optional<Tree> relevantLocalPathContext) {
        this.relevantLocalPathContext = relevantLocalPathContext;
        return this;
    }
}
