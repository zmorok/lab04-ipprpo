pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
    }

    parameters {
        string(
            name: 'APP_NAME',
            defaultValue: 'FoodDelivery',
            description: 'Название приложения доставки еды'
        )
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo "Building food delivery application: ${params.APP_NAME}"
                sh 'mvn -B -ntp compile'
            }
        }

        stage('Static Analysis') {
            steps {
                sh 'mvn -B -ntp checkstyle:check'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B -ntp test'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn -B -ntp package -DskipTests'
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/logs/*.log', allowEmptyArchive: true
        }
    }
}
