pipeline {
    agent any
    environment {
        ANDROID_SDK_ROOT = "${env.ANDROID_SDK_ROOT ?: 'C:\\Android\\Sdk'}"
        AVD_NAME = "${env.AVD_NAME ?: 'Pixel_9'}"  // default AVD name
    }
    stages {
        stage('Checkout') {
            steps { checkout scm }
        }
        stage('Verify Tools') {
            steps {
                bat 'java -version'
                bat 'mvn -v'
                bat 'where adb'
                bat 'adb version'
                bat 'emulator -list-avds'
            }
        }
        stage('Start Emulator') {
            when { expression { return env.USE_REAL_DEVICE != 'true' } }
            steps { 
                // Pass AVD name as argument to batch file
                bat "cd %WORKSPACE%\\ci && call start_emulator.bat \"%AVD_NAME%\"" 
            }
        }
        stage('Start Appium') {
            steps { bat 'cd %WORKSPACE%\\ci && call start_appium.bat' }
        }
        stage('Run Tests') {
            steps { bat 'mvn clean test -DskipTests=false -Dsurefire.useFile=false' }
        }
        stage('Publish Results') {
            steps {
                junit 'target/surefire-reports/**/*.xml'
                allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]
            }
        }
    }
    post {
        always {
            bat 'cd %WORKSPACE%\\ci && call stop_appium_and_emulator.bat'
            archiveArtifacts artifacts: '**/target/*.zip, **/target/*.apk', allowEmptyArchive: true
        }
        success { echo 'Tests passed ✅' }
        failure { echo 'One or more stages failed ❌' }
    }
}