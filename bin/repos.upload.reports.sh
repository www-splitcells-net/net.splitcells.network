#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

set -e
set +x # Prevent printing secrets in CI pipeline.
# Integration tests need to be enabled here as well, as verify does not only start the source code check, but also reruns the test and therefore the coverage.
# The JaCoCo report is created only for the main report, as otherwise the Distro repo causes errors.
# `-Dsonar.inclusions=src/main/**,bin/*` does not seem to work.`
# Enabling debug logging via -X makes it easier to find reason for errors in daily CI.
# SonarCloud:
# * Adding the following to the mvn verify command did not work and broke authorization.
#   The install step is required, according to SonarCloud doc https://docs.sonarsource.com/sonarqube-server/10.8/analyzing-source-code/scanners/sonarscanner-for-maven#analyzing
# * If one gets an 403 HTTP status code, consider regenerating the SONAR_TOKEN: https://sonarcloud.io/account/security
mvn -B clean install \
  org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
  -Dsonar.projectKey=www-splitcells-net_net.splitcells.network \
  -Dsonar.organization=www-splitcells-net \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.test.exclusions=src/main/java/**
# Clean has to be executed, as otherwise the code coverage report will be missing for `repos.upload.reports.sh`.
# Furthermore, one cannot execute both mvn commands as one, as otherwise the `repos.upload.reports.sh` will not work as well.
# JavaDocs are primarily build here and uploaded to `repos.upload.reports.sh` and not at other places,
# because generating JavaDocs takes a lot of time.
mvn clean install site \
    -Dtest_codecov=1 \
    -Dsource_code_check=1 \
    -Dtest.groups=testing_unit,testing_integration \
    -DexcludedGroups="experimental_test"
# -z is used because -v is not supported in Forgejo workflows.
if [ -z "$NET_SPLITCELLS_MARTINS_AVOTS_WEBSITE_SFTP_PRIVATE_KEY" ]; then
  echo Upload to https://splitcells.net is disabled, because the environment variable NET_SPLITCELLS_MARTINS_AVOTS_WEBSITE_SFTP_PRIVATE_KEY is not set.
else
  mkdir -p ~/.ssh-website-upload/
  echo "$NET_SPLITCELLS_MARTINS_AVOTS_WEBSITE_SFTP_PRIVATE_KEY" > ~/.ssh-website-upload/id_rsa
  chmod 700 ~/.ssh-website-upload/id_rsa # Otherwise, scp may not work.
  mkdir -p target/website-upload/public_html/net/splitcells/martins/avots/website/jacoco-aggregate # Using subdirectories, ensures, that scp does not get a problem with missing directories.
  cp -r target/site/jacoco-aggregate/* target/website-upload/public_html/net/splitcells/martins/avots/website/jacoco-aggregate
  set -x
  set -e
  realpath target/website-upload/public_html/net/splitcells/martins/avots/website/jacoco-aggregate
  # The targeted scp upload folder is dependent on the relative source path. Therefore, cd and the current folder are used, in order to avoid this strange mapping.
  cd target/website-upload/
  scp -v -i ~/.ssh-website-upload/id_rsa -o PubkeyAuthentication=yes -o StrictHostKeyChecking=no -r ./ splitcm@www322.your-server.de:
  rm ~/.ssh-website-upload/id_rsa
fi