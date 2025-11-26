package com.AssessmentOnline;

import java.time.Duration;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import com.DriverUtility.driverUtility;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Attachment;
import io.qameta.allure.Description;
import io.qameta.allure.Step;

public class AssessmentOffline {

    @Test(description = "Full Assessment Online login and inspection flow")
    @Description("This test performs login, handles permissions, VIN/Make/Model input, and captures images for inspection.")
    public void testLocators() {

        AppiumDriver driver = driverUtility.initAndroidDriverSession();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));

        System.out.println("Waiting for app to launch...");
        enterCredentials(wait, driver);
        clickLogin(wait, driver);
        handlePermissions(wait, driver);
        startInspection(wait, driver);
        enterVehicleDetails(wait, driver);
        clickNextStep(wait, driver);
        captureImages(wait, driver);

        System.out.println("Inspection flow completed successfully.");
    }

    // ==================== Steps ====================

    @Step("Enter username and password")
    private void enterCredentials(WebDriverWait wait, AppiumDriver driver) {
        WebElement userIdField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                AppiumBy.xpath("(//android.widget.AutoCompleteTextView[@resource-id=\"io.clearquote.assessment:id/acInput\"])[1]")));
        userIdField.sendKeys("dacb15");

        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                AppiumBy.xpath("(//android.widget.AutoCompleteTextView[@resource-id=\"io.clearquote.assessment:id/acInput\"])[2]")));
        passwordField.sendKeys("Abcd@123");
    }

    @Step("Click Login button")
    private void clickLogin(WebDriverWait wait, AppiumDriver driver) {
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.id("io.clearquote.assessment:id/btnLogin")));
        loginButton.click();
    }

    @Step("Handle media/file permissions")
    private void handlePermissions(WebDriverWait wait, AppiumDriver driver) {
        try {
            WebDriverWait sysWait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebElement allowBtn = sysWait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.id("com.android.permissioncontroller:id/permission_allow_button")));
            allowBtn.click();
        } catch (Exception e) {
            System.out.println("No media permission popup detected.");
        }

        try {
            String permissionText = "While using the app";
            for (int i = 0; i < 2; i++) {
                try {
                    WebElement allowButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                            AppiumBy.androidUIAutomator("new UiSelector().text(\"" + permissionText + "\")")));
                    allowButton.click();
                    Thread.sleep(500);
                } catch (Exception e) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("No 'While using the app' popup detected.");
        }
    }

    @Step("Start inspection flow")
    private void startInspection(WebDriverWait wait, AppiumDriver driver) {
        WebElement inspectionIcon = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.id("io.clearquote.assessment:id/ivInspection")));
        inspectionIcon.click();
    }

    @Step("Enter VIN, Make, and Model")
    private void enterVehicleDetails(WebDriverWait wait, AppiumDriver driver) {
        try {
            WebElement vinInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    AppiumBy.xpath("//android.widget.AutoCompleteTextView[@resource-id='io.clearquote.assessment:id/acInput' and @text='VIN']")));
            vinInput.click();
            vinInput.sendKeys("TESTUSWEBUI002000");

            WebElement makeInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    AppiumBy.xpath("(//android.widget.AutoCompleteTextView[@resource-id='io.clearquote.assessment:id/acInput'])[2]")));
            makeInput.click();
            makeInput.sendKeys("Van");

            WebElement modelInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    AppiumBy.xpath("(//android.widget.AutoCompleteTextView[@resource-id='io.clearquote.assessment:id/acInput'])[3]")));
            modelInput.click();
            modelInput.sendKeys("Any Model");

        } catch (Exception e) {
            System.out.println("Failed to enter VIN/Make/Model: " + e.getMessage());
        }
    }

    @Step("Click Next Step")
    private void clickNextStep(WebDriverWait wait, AppiumDriver driver) {
        try {
            WebElement nextStepButton = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.id("io.clearquote.assessment:id/btnNextStep")));
            nextStepButton.click();
        } catch (Exception e) {
            System.out.println("Failed to click 'Next Step' button: " + e.getMessage());
        }
    }

    @Step("Capture images for inspection")
    private void captureImages(WebDriverWait wait, AppiumDriver driver) {
        try {
            for (int i = 1; i <= 5; i++) {
                WebElement captureImage = wait.until(ExpectedConditions.elementToBeClickable(
                        AppiumBy.id("io.clearquote.assessment:id/btnCaptureImage")));
                captureImage.click();
                attachScreenshot(driver, "Captured image #" + i);
                Thread.sleep(2000);
            }

            WebElement doneButton = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.id("io.clearquote.assessment:id/btnDone")));
            doneButton.click();
        } catch (Exception e) {
            System.out.println("Error during image capture: " + e.getMessage());
        }
    }

    // ==================== Allure Screenshot Attachment ====================
    @Attachment(value = "{0}", type = "image/png")
    public byte[] attachScreenshot(AppiumDriver driver, String name) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
