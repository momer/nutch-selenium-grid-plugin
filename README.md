Nutch Selenium
==============

This plugin allows you to fetch javascript pages using an existing Selenium Hub/Node set-up, while relying on the rest of the awesome Nutch stack! This allows you to

A) Leverage Nutch, a world class web crawler

B) Not have to use some paid service just to perform large-scale javascript/ajax aware web crawls

C) Not have to wait another 2 years for Nutch to patch in either the [Ajax crawler hashbang workaround](https://issues.apache.org/jira/browse/NUTCH-1323) and then, not having to patch it to get the use case of ammending the original url with the hashbang-workaround's content.

The underlying code is based on the nutch-htmlunit plugin, which was in turn based on nutch-httpclient. I also have patches to send through on nutch-htmlunit which get it working with nutch 2.2.1, so stay tuned if you want to use htmlunit for some reason.

## IMPORTANT NOTE:

This plugin is currently being merged into the Nutch Core - see [issue #1933 on Nutch's JIRA](https://issues.apache.org/jira/browse/NUTCH-1933)

## Inspiration / borrowed code

- https://github.com/xautlx/nutch-htmlunit which borrowed heavily from
- https://nutch.apache.org/apidocs/apidocs-2.2.1/org/apache/nutch/protocol/httpclient/package-summary.html

## TODO

- Set up to handle https:// as well in Nutch

## NOTE:

- Currently, the plugin is set to retrieve only the content of `<body>` - you can change this yourself in `src/plugin/lib-selenium/src/java/org/apache/nutch/protocol/selenium/HttpWebClient.java`

## Getting Selenium Hub/Node Started (Docker)

I've created two docker containers to help you get started: Selenium-Hub and Selenium-Node if you're comfortable with Docker.

1. https://github.com/momer/docker-selenium-hub
2. https://github.com/momer/docker-selenium-node

## Installation (tested on Ubuntu 14.0x)

Part 1: Installing plugin for Nutch (where NUTCH_HOME is the root of your nutch install)

A) Add Selenium to your Nutch dependencies
```
<!-- NUTCH_HOME/ivy/ivy.xml -->

<ivy-module version="1.0">
  <dependencies>
    ...

    <!-- begin selenium dependencies -->
    <dependency org="org.seleniumhq.selenium" name="selenium-java" rev="2.42.2" />

    <!-- end selenium dependencies -->

    ...
  </dependencies>
</ivy-module>
```
B) Add the required plugins to your `NUTCH_HOME/src/plugin/build.xml` - or use the included build.xml if you're using Nutch 2.2.1 and this is the only additional plugin you'll be using.
```
<!-- NUTCH_HOME/src/plugin/build.xml -->

<project name="Nutch" default="deploy-core" basedir=".">
  <!-- ====================================================== -->
  <!-- Build & deploy all the plugin jars.                    -->
  <!-- ====================================================== -->
  <target name="deploy">
    ... 
    <ant dir="lib-selenium" target="deploy"/>
    <ant dir="protocol-selenium" target="deploy" />
    ...
  </target>
</project>

```
C) Ensure that the plugin will be used as the fetcher/initial parser in your config
```
<!-- NUTCH_HOME/conf/nutch-site.xml -->

<configuration>
  ...

  <property>
    <name>plugin.includes</name>
    <value>protocol-selenium|urlfilter-regex|parse-(html|tika)|index-(basic|anchor)|urlnormalizer-(pass|regex|basic)|scoring-opic</value>
    <description>Regular expression naming plugin directory names to
    include.  Any plugin not matching this expression is excluded.
    In any case you need at least include the nutch-extensionpoints plugin. By
    default Nutch includes crawling just HTML and plain text via HTTP,
    and basic indexing and search plugins. In order to use HTTPS please enable 
    protocol-httpclient, but be aware of possible intermittent problems with the 
    underlying commons-httpclient library.
    </description>
  </property>

  ...
</configuration>

```
D) Define your Selenium Hub configuration
```
<!-- NUTCH_HOME/conf/nutch-site.xml -->

<configuration>
  ...

  <property>
    <name>selenium.hub.port</name>
    <value>4444</value>
    <description>Selenium Hub Location connection port</description>
  </property>

  <property>
    <name>selenium.hub.path</name>
    <value>/wd/hub</value>
    <description>Selenium Hub Location connection path</description>
  </property>

  <property>
    <name>selenium.hub.host</name>
    <value>server1.mycluster.com</value>
    <description>Selenium Hub Location connection host</description>
  </property>

  <property>
    <name>selenium.hub.protocol</name>
    <value>http</value>
    <description>Selenium Hub Location connection protocol</description>
  </property>

  ...
</configuration>
```

E) Add the plugin folders to your installation's `NUTCH_HOME/src/plugin` directory

![Nutch plugin directory](http://i.imgur.com/CzLqoqO.png)

F) Compile nutch
```
ant runtime
```

G) Start your web crawl (Ensure that you followed the above steps and that your Selenium-Hub / Selenium-Node set-up is running)
```
NUTCH_HOME/runtime/local/bin/crawl /opt/apache-nutch-2.2.1/urls/ webpage $NUTCH_SOLR_SERVER $NUTCH_CRAWL_DEPTH
```

