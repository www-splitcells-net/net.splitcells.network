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
package net.splitcells.website.server.project;

import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.lang.perspective.Perspective;

import java.util.Optional;

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

    private Optional<Perspective> localPathContext = Optional.empty();

    private Optional<Perspective> relevantLocalPathContext = Optional.empty();

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
     * TODO In the future, there should be probably something like an {@link LayoutConfig},
     * that is internal to the main renderer,
     * in order to prevent an incorrect {@code localPathContext},
     * because only the main renderer can determine the correct {@code localPathContext}.
     *
     * @return These are all paths, that are children, to {@link #path},
     * and which can be requested from relevant {@link Renderer}.
     */
    public Optional<Perspective> localPathContext() {
        return localPathContext;
    }

    /**
     * TODO In the future, there should be probably something like an {@link LayoutConfig},
     * that is internal to the main renderer,
     * in order to prevent an incorrect {@code relevantLocalPathContext}.
     * Extension should only be allowed to determine a subset of {@link #localPathContext},
     * which should be provided by the main renderer.
     *
     * @return These are all paths, that are children, to {@link #path},
     * which can be requested from relevant {@link Renderer} and
     * which are relevant to the user.
     */
    public Optional<Perspective> relevantLocalPathContext() {
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
    public LayoutConfig withLocalPathContext(Optional<Perspective> localPathContext) {
        this.localPathContext = localPathContext;
        return this;
    }

    /**
     * This should only be set by the {@link Renderer}.
     *
     * @param relevantLocalPathContext The Relevant Local Path Context
     * @return This
     */
    public LayoutConfig withRelevantLocalPathContext(Optional<Perspective> relevantLocalPathContext) {
        this.relevantLocalPathContext = relevantLocalPathContext;
        return this;
    }
}
