# Secure CQ

## Introduction

Secure CQ is a tool which can be used to find the most popular security problems in your CQ instance. It tests both instances (author, publish) and also the dispatcher, as some resources should be restricted in the cache configuration. It checks:

* if the default passwords are changed,
* if there are no unnecessary protocols enabled after being published,
* if the the administrator console access is disabled,
* if content-grabbing selectors are restricted on the dispatcher,
* etc.

Each test contains a description and the *More info* link which references the external site to additional information about a given security flaw.

You may also be interested in the blog post on [Secure CQ](http://www.cognifide.com/blogs/cq/keep-your-cms-safe-with-secure-cq/).

## Requirements

* CQ 5.4, 5.5 or 5.6

## Installation

You'll need Maven 2.x. If your author instance is running on `localhost:4502` and credentials to it are `admin:admin` then run:

        mvn clean package crx:install

Otherwise you may enter address and credentials explicitly:

        mvn clean package crx:install -Dinstance.url=http://localhost:4502 -Dinstance.username=YOUR_USERNAME -Dinstance.password=YOUR_PASSWORD

## Configuration

After installation, go to the CQ *Tools* page and choose *Secure CQ* from the list on the left. The application tries to find author, publish and dispatcher URLs automatically, but you may want to confirm that they have been recognized correctly. In order to do that click *Edit* on the Settings bar and optionally correct addresses. That's it. Wait for a moment until the tests are done and check the results.

## CLI version

Sometimes you may want to check remote CQ instance. *Secure CQ* may be compiled in the standalone mode and used from the CLI, without any additional dependencies. In order to build application this way, enter:

        mvn clean package -Pcli

JAR package will be available as `target/secure-cq-VERSION-cli.jar`.

### Usage

Usage is simple:

    java -jar secure-cq.jar [-a AUTHOR_URL] [-p PUBLISH_URL] [-d DISPATCHER_URL]
    
Enter at least one URL to test given instance, eg.:

    java -jar secure-cq-…-cli.jar -a http://localhost:4502
    
to invoke author tests on the localhost or

    java -jar secure-cq-…-cli.jar -a 192.168.35.105:4502 -p 192.168.35.105:4503 -d 192.168.35.105
    
to invoke author, publish and dispatcher-related tests. You may skip the starting `http://`, *SecureCQ* uses HTTP protocol by default.

## Writing own tests

### Test page

Test case is a standard CQ page under `/etc/securecq` parent. It contains some test metadata as title, severity, info URL, which can be edited using test page template. Click on the test name to show the page. Besides that, test page contains one `testComponent`. It's `sling:resourceType` defines the test type (eg. `cognifide/securecq/components/pageContent` will check if some page contains some string) and the rest of attributes is the test configuration. Example:

    <testComponent
        jcr:primaryType="nt:unstructured"
        enabled="true"
        sling:resourceType="cognifide/securecq/components/pageContent"
        paths="[/libs/shindig/proxy]"
        content="[INVALID_PARAMETER]"/>

### Test types

Each test type consists of CQ component and Java class.

#### Components

Test types are standard CQ components, inherited from `cognifide/securecq/components/abstractTest`. Components are linked to Java test class (extending `AbstractClass`) with `testClass` property. Sample test component:

    <?xml version="1.0" encoding="UTF-8"?>
    <jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
        jcr:primaryType="cq:Component"
        sling:resourceSuperType="cognifide/securecq/components/abstractTest"
        testClass="com.cognifide.securecq.tests.PathsTest"/>

You may as also override `dialog.xml` (to provide some user configuration for the component) and `metadata.jsp` (to display these settings on the test page).

#### Java classes

Each test class extends `AbstractTest` and implements some of the interfaces: `AuthorTest`, `PublishTest`, `DispatcherTest` to mark for which URLs it should be invoked. There is only one method to implement:

	/**
	 * Perform test.
	 * 
	 * @param url URL of the instance to test.
	 * @param instanceName Name of the instance (eg. author, publish or dispatcher).
	 * @return true if the test succeeded
	 * @throws Exception If you throw an exception, test result will be set to "Exception". You may throw
	 * special {@link InvalidConfigurationException} with message if the test configuration isn't set
	 * correctly.
	 */
	protected abstract boolean doTest(String url, String instanceName) throws Exception;

In the test implementation you may invoke two methods:

	protected void addInfoMessage(String message, Object... params)
	
	protected void addErrorMessage(String message, Object... params)
	
to add more detailed info about the result.