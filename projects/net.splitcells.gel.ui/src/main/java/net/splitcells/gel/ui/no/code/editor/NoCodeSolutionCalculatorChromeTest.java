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
package net.splitcells.gel.ui.no.code.editor;

import net.splitcells.dem.Dem;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumDriverLogLevel;

@JavaLegacy
public class NoCodeSolutionCalculatorChromeTest {
    /**
     * TODO Chromium is not always closed after the execution, even when close is called.
     *
     * @param args
     */
    public static void main(String... args) {
        final var serviceConfig = new ChromeDriverService.Builder().withSilent(false)
                .withVerbose(true)
                .withLogLevel(ChromiumDriverLogLevel.ALL)
                .withLogOutput(System.out)
                .build();
        System.setProperty("webdriver.chrome.silentOutput", "false");
        final var config = new ChromeOptions();
        config.addArguments("--headless");
        // `remote-debugging-port` is required, as otherwise the connection to the browser does not work.
        config.addArguments("--remote-debugging-port=9999");
        final WebDriver browser = new ChromeDriver(serviceConfig, config);
        browser.get("http://localhost:8443/net/splitcells/gel/ui/no/code/editor/index.html");
        browser.findElement(By.className("net-splitcells-website-pop-up-confirmation-button")).click();
        browser.findElement(By.id("net-splitcells-gel-ui-no-code-editor-calculate-solution-form-submit-1")).click();
        Dem.sleepAtLeast(5000);
        browser.close();
    }
}
