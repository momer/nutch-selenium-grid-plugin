package org.apache.nutch.protocol.selenium;

import java.net.URL;

import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.lang.String;

public class HttpWebClient {
    private static final Logger LOG = LoggerFactory.getLogger("org.apache.nutch.protocol");

    public static String getHtmlPage(URL url, Configuration conf) {
        WebDriver driver = null;
        String seleniumHubHost = conf.get("selenium.hub.host", "");
        int seleniumHubPort = Integer.parseInt(conf.get("selenium.hub.port", "4444"));
        String seleniumHubPath = conf.get("selenium.hub.path", "");
        String seleniumHubProtocol = conf.get("selenium.hub.protocol", "");

        try {
            driver = new RemoteWebDriver(new URL(seleniumHubProtocol, seleniumHubHost, seleniumHubPort, seleniumHubPath), DesiredCapabilities.firefox());
            driver.get(url.toString());

            // Wait for the page to load, timeout after 3 seconds
            new WebDriverWait(driver, 3);

            String innerHtml = driver.findElement(By.tagName("body")).getAttribute("innerHTML");

            return innerHtml;
            // I'm sure this catch statement is a code smell ; borrowing it from lib-htmlunit
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (driver != null) { driver.quit(); }
        }
    };

    public static String getHtmlPage(URL url) {
        return getHtmlPage(url, null);
    }
}