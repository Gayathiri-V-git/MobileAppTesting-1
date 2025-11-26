pipeline {
    agent any

    environment {
        ANDROID_SDK_ROOT = "C:\\Android\\Sdk"
        PATH = "${env.ANDROID_SDK_ROOT}\\platform-tools;${env.ANDROID_SDK_ROOT}\\emulator;${env.PATH}"
        AVD_NAME = "${env.AVD_NAME ?: 'CI_Test_AVD'}"
        USE_REAL_DEVICE = "${env.USE_REAL_DEVICE ?: 'false'}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify Tools') {
            steps {
                bat 'java -version'
                bat 'mvn -v'
                bat 'where adb'
                bat 'adb version'
                bat 'emulator -list-avds'
                // Optional: verify allure available on PATH
                bat 'where allure || echo "Allure not found on PATH - ensure it is installed on the Jenkins node"'
            }
        }

        stage('Start Emulator') {
            when { expression { return env.USE_REAL_DEVICE != 'true' } }
            steps {
                // start_emulator.bat should start AVD and wait until it's booted
                bat 'cd %WORKSPACE%\\ci && call start_emulator.bat %AVD_NAME%'
                // small pause to let adb detect device (optional)
                bat 'adb devices -l'
            }
        }

        stage('Start Appium') {
            steps {
                // start_appium.bat should start Appium server (port 4723 by default)
                bat 'cd %WORKSPACE%\\ci && call start_appium.bat'
                // verify Appium is reachable (simple curl check if curl installed)
                bat 'powershell -Command "try { iwr http://127.0.0.1:4723/wd/hub/status -UseBasicParsing | Out-Null; Write-Host \'Appium reachable\' } catch { Write-Host \'Appium not reachable (verify start_appium.bat)\' }"'
            }
        }

        stage('Run Tests') {
            steps {
                // run tests normally so Surefire writes reports and Allure adapter produces allure-results
                bat 'mvn clean test -DskipTests=false'
            }
        }

        stage('Record JUnit Results') {
            steps {
                // Record surefire XMLs so Jenkins shows test results (helpful alongside Allure)
                junit 'target/surefire-reports/**/*.xml'
            }
        }

        stage('Generate Allure Report') {
            steps {
                // Generate a static Allure HTML report from allure-results
                // Ensure `allure` CLI is on PATH on build agent
                bat 'allure generate target/allure-results --clean -o target/allure-report'
            }
        }

        stage('Publish Allure Report') {
            steps {
                // Publish via Jenkins Allure plugin (requires plugin installed)
                allure([
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'target/allure-results']]
                ])
            }
        }
    }

    post {
        always {
            // stop appium and emulator; scripts should handle missing processes gracefully
            bat 'cd %WORKSPACE%\\ci && call stop_appium_and_emulator.bat || echo "No emulator/Appium running, skipping stop."'
            // archive any useful artifacts (adjust globs if needed)
            archiveArtifacts artifacts: '**/target/*.zip, **/target/*.apk, **/target/allure-report/**', allowEmptyArchive: true
        }
        success {
            echo 'Tests passed ✅'
        }
        failure {
            echo 'One or more stages failed ❌'
            // optionally collect extra logs for debugging
            bat 'adb devices -l || echo "adb not available"'
            bat 'if exist target\\allure-results (dir target\\allure-results) else (echo "no allure results")'
        }
    }
}
