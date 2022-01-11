[![Apache Sling](https://sling.apache.org/res/logos/sling.png)](https://sling.apache.org)

&#32;[![Build Status](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-launchpad-integration-tests/job/master/badge/icon)](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-launchpad-integration-tests/job/master/)&#32;[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=apache_sling-org-apache-sling-launchpad-integration-tests&metric=coverage)](https://sonarcloud.io/dashboard?id=apache_sling-org-apache-sling-launchpad-integration-tests)&#32;[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=apache_sling-org-apache-sling-launchpad-integration-tests&metric=alert_status)](https://sonarcloud.io/dashboard?id=apache_sling-org-apache-sling-launchpad-integration-tests)&#32;[![JavaDoc](https://www.javadoc.io/badge/org.apache.sling/org.apache.sling.launchpad.integration-tests.svg)](https://www.javadoc.io/doc/org.apache.sling/org.apache.sling.launchpad.integration-tests)&#32;[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.sling/org.apache.sling.launchpad.integration-tests/badge.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.apache.sling%22%20a%3A%22org.apache.sling.launchpad.integration-tests%22)&#32;[![launchpad](https://sling.apache.org/badges/group-launchpad.svg)](https://github.com/apache/sling-aggregator/blob/master/docs/groups/launchpad.md) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# Apache Sling Launchpad Integration Tests

This module is part of the [Apache Sling](https://sling.apache.org) project and
contains test classes used by the 
[launchpad/testing](https://github.com/apache/sling-org-apache-sling-launchpad-testing/) module.

To run a single test or a specific set of tests against a running Sling
instance, use for example:

    mvn test -Dtest=UploadFileTest -Dhttp.port=1234
    
Where UploadFileTest is the test to run. Wildcards are allowed, and test
classes are found in the src/main folder (not a typo - that's not src/test
as we want to pack the tests in the jar file that we build).

See the `<properties>` section in pom.xml for additional parameters that the
tests use.

Here's another example, running the tests against a Sling instance running 
on host xyzzy, port 1234, with the Sling main servlet mounted under /foo:

    mvn -o -s /dev/null test \
        -Dhttp.port=1234 \
        -Dtest.host=xyzzy \
        -Dhttp.base.path=foo \
        -Dwebdav.workspace.path=foo \
        -Dlaunchpad.readiness.mediatype=.json:application/json \ 
        -Dtest=**/integrationtest/**/*Test.java

To run or debug tests against the same instance that `launchpad/testing` module,
see that module's README for how to start the test instance.

The standard `-Dmaven.surefire.debug` option can be used to debug the tests
themselves.

Some tests might fail if not using a a Sling instance that's not setup by 
the `launchpad/testing module`, as that installs a few additional test
bundles.
