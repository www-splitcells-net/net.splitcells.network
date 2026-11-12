/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.client;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.util.Optional;
import java.util.function.Function;

import static net.splitcells.dem.utils.ExecutionException.execException;

@JavaLegacy public class ElementImpl implements Element {

    public static ElementImpl elementImpl(Locator argLocator, Page argPage, Object argPlaywrightSynchronizer, Runnable argOnError) {
        return new ElementImpl(argLocator, argPlaywrightSynchronizer, argPage, argOnError);
    }

    private final Object playwrightSynchronizer;
    private final Page page;
    private final Runnable onError;
    private final Locator locator;

    private ElementImpl(Locator argLocator, Object argPlaywrightSynchronizer, Page argPage, Runnable argOnError) {
        this.playwrightSynchronizer = argPlaywrightSynchronizer;
        this.page = argPage;
        onError = argOnError;
        locator = argLocator;
    }

    @Override public <T> Optional<T> evalIfExists(Function<Element, T> evaluation) {
        if (!locator.all().isEmpty()) {
            try {
                return Optional.of(evaluation.apply(this));
            } catch (TimeoutError e) {
                throw execException("This element does not actually exist in the browser. This is probably not a real network timeout.", e);
            } catch (RuntimeException e) {
                onError.run();
                throw e;
            }
        }
        return Optional.empty();
    }

    @Override public void click() {
        synchronized (playwrightSynchronizer) {
            try {
                locator.click();
            } catch (TimeoutError e) {
                throw execException("This element does not actually exist in the browser. This is probably not a real network timeout.", e);
            } catch (RuntimeException e) {
                onError.run();
                throw e;
            }
        }
    }

    @Override public String textContent() {
        synchronized (playwrightSynchronizer) {
            try {
                return locator.textContent();
            } catch (TimeoutError e) {
                throw execException("This element does not actually exist in the browser. This is probably not a real network timeout.", e);
            } catch (RuntimeException e) {
                onError.run();
                throw e;
            }

        }
    }

    @Override public Element setTextContent(String content) {
        synchronized (playwrightSynchronizer) {
            try {
                locator.evaluate("(node, text) => node.textContent = text", content);
                return this;
            } catch (TimeoutError e) {
                throw execException("This element does not actually exist in the browser. This is probably not a real network timeout.", e);
            } catch (RuntimeException e) {
                onError.run();
                throw e;
            }
        }
    }

    @Override public String value() {
        synchronized (playwrightSynchronizer) {
            try {
                return locator.inputValue();
            } catch (TimeoutError e) {
                throw execException("This element does not actually exist in the browser. This is probably not a real network timeout.", e);
            } catch (RuntimeException e) {
                onError.run();
                throw e;
            }
        }
    }
}
