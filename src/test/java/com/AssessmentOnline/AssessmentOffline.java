package com.AssessmentOnline;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;


import com.DriverUtility.driverUtility1;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;

public class AssessmentOffline {

    @Test
    public void testLocators()  {

        // Create Appium driver session
        AppiumDriver driver = driverUtility1.initAndroidDriverSession();

        // Wait object
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));

        //  Wait until the app's main activity is fully loaded
        System.out.println("Waiting for app to launch...");

        // Adjust the first element you expect on the login screen
        WebElement userIdField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath("(//android.widget.AutoCompleteTextView[@resource-id=\"io.clearquote.assessment:id/acInput\"])[1]\n"
                		+ ""))
        );
        System.out.println("User ID field found. Entering username...");
        userIdField.sendKeys("dacb15");

        // Password field
        WebElement passwordField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath("(//android.widget.AutoCompleteTextView[@resource-id=\"io.clearquote.assessment:id/acInput\"])[2]\n"
                		+ ""))
        );
        System.out.println("Password field found. Entering password...");
        passwordField.sendKeys("Abcd@123");

        // Login button
        String loginButtonId = "io.clearquote.assessment:id/btnLogin"; 
        WebElement loginButton = wait.until(
                ExpectedConditions.elementToBeClickable(AppiumBy.id(loginButtonId))
        );
        System.out.println("Login button found. Clicking...");
        loginButton.click();

       
        System.out.println("Login test completed successfully.");
        
        try {
            WebDriverWait sysWait = new WebDriverWait(driver, Duration.ofSeconds(30));

            // Allow media/files access
            WebElement allowBtn = sysWait.until(
                ExpectedConditions.elementToBeClickable(AppiumBy.id("com.android.permissioncontroller:id/permission_allow_button"))
            );
            allowBtn.click();
            System.out.println("Clicked Allow button for media access");
        } catch (Exception e) {
            System.out.println("No media permission popup detected.");
        }
        
     // Wait object
        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(2000));
        
        //Create Inspection
        WebElement inspectionIcon = wait1.until(
                ExpectedConditions.elementToBeClickable(AppiumBy.id("io.clearquote.assessment:id/ivInspection"))
        );
        System.out.println("Inspection icon found. Clicking...");
        inspectionIcon.click();
        
     // Wait object
        WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(120));
        
        String permissionText = "While using the app";
        

        try {
            for (int i = 0; i < 2; i++) {
                try {
                    WebElement allowButton = wait1.until(ExpectedConditions.presenceOfElementLocated(
                            AppiumBy.androidUIAutomator("new UiSelector().text(\"" + permissionText + "\")")));
                    allowButton.click();
                    System.out.println(" Clicked '" + permissionText + "' permission popup (" + (i + 1) + ")");
                    Thread.sleep(500); // short delay for second popup
                } catch (Exception e) {
                    // break if not found second time
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(" No 'While using the app' popup detected or click failed: " + e.getMessage());
        }
    
        
        
        try {
            // VIN input
            WebElement vinInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                AppiumBy.xpath("//android.widget.AutoCompleteTextView[@resource-id='io.clearquote.assessment:id/acInput' and @text='VIN']")));
            vinInput.click();
            vinInput.sendKeys("TESTUSWEBUI002000");
            System.out.println(" VIN entered successfully: TESTUSWEBUI002000");

            // Make input
            WebElement makeInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                AppiumBy.xpath("(//android.widget.AutoCompleteTextView[@resource-id='io.clearquote.assessment:id/acInput'])[2]")));
            makeInput.click();
            makeInput.sendKeys("Van");
            System.out.println(" Make entered successfully");

            // Model input
            WebElement modelInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                AppiumBy.xpath("(//android.widget.AutoCompleteTextView[@resource-id='io.clearquote.assessment:id/acInput'])[3]")));
            modelInput.click();
            modelInput.sendKeys("Any Model");
            System.out.println(" Model entered successfully");

        } catch (Exception e) {
            System.out.println(" Failed to enter VIN/Make/Model: " + e.getMessage());
        }
        
        try {
            WebDriverWait wait3 = new WebDriverWait(driver, Duration.ofSeconds(60));

            // Wait until the button is clickable
            WebElement nextStepButton = wait1.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.id("io.clearquote.assessment:id/btnNextStep")));


            // Click the button
            nextStepButton.click();
            System.out.println(" 'Next Step' button clicked successfully.");

        } catch (Exception e) {
            System.out.println(" Failed to click 'Next Step' button: " + e.getMessage());
        }

        
        // Wait object
        WebDriverWait wait4 = new WebDriverWait(driver, Duration.ofSeconds(200));
        
        
        // === Image Capture Section ===
        for (int i = 1; i <= 5; i++) {
            WebElement captureImage = wait1.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.id("io.clearquote.assessment:id/btnCaptureImage")));
            System.out.println("Capturing image #" + i + "...");
            captureImage.click();

            // optional wait between captures
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 🔹 Done button (after image capture)
        WebElement doneButton = wait1.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.id("io.clearquote.assessment:id/btnDone")));
        System.out.println("Done button found. Clicking...");
        doneButton.click();

        System.out.println(" Inspection flow completed successfully.");
    }
}
