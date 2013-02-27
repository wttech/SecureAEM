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
