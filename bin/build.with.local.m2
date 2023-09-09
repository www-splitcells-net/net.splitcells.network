#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
mvn --settings src/main/xml-pom/net/splitcells/network/build-with-local-m2.pom.xml clean install
