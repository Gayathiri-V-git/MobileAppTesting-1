package com.DriverUtility;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

public class driverUtility {

    public static AppiumDriver driver;

    public static AppiumDriver initAndroidDriverSession() {

        UiAutomator2Options options = new UiAutomator2Options();
        options.setDeviceName("pixel_9");
        options.setUdid("emulator-5556");
        options.setPlatformName("Android");
        options.setAutomationName("UiAutomator2");

        // APK path
        String appPath = System.getProperty("user.dir") + "/src/test/resources/cq-inspections-internal-4.0.6.apk";
        options.setApp(appPath);

        // Robust launch settings
        options.setAppWaitDuration(Duration.ofSeconds(120));
        options.setAppWaitActivity("*");
        options.setFullReset(true);
        options.setNoReset(false);

        try {
            // Appium server URL
            URL serverUrl = new URI("http://127.0.0.1:4723").toURL();

            // Create driver session
            driver = new AndroidDriver(serverUrl, options);
            System.out.println("Session started. Session ID: " + driver.getSessionId());

            // ✅ Handle any initial permission popups right after launch
            handleDoubleLocationPermission((AndroidDriver) driver);
            handleAllPermissionPopups((AndroidDriver) driver);

        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
        }

        return driver;
    }


    private static void handleDoubleLocationPermission(AndroidDriver driver2) {
		// TODO Auto-generated method stub
		
	}


	// ✅ Universal Android Permission Handler (for other dialogs)
    public static void handleAllPermissionPopups(AndroidDriver driver) {
        String[] possibleButtons = {
            "Only this time",
            "Allow all the time",
            "Allow",
            "OK",
            "Yes",
            "Got it",
            "Continue",
            "Allow access",
            "Allow ClearQuote Inspections to",
            "Allow ClearQuote to",
            "Allow permission"
        };

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        for (String text : possibleButtons) {
            try {
                WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(
                        AppiumBy.androidUIAutomator("new UiSelector().textContains(\"" + text + "\")")));
                if (button.isDisplayed()) {
                    button.click();
                    System.out.println(" Clicked permission button with text: " + text);
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                // ignore if specific button not found
            }
        }

        System.out.println("All visible permission popups handled (if any).");
    }
}
