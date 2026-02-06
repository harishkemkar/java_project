pipeline {
    agent any

    environment {
        BUILD_SERVER_IP = "13.126.94.137"
        DEPLOY_SERVER_IP = "65.0.18.21033333333336"
        BUILD_DIR = "/home/ec2-user/java_application"
        DEPLOY_DIR = "/home/ec2-user/java_application"
        APP_JAR = "myapp-1.0-SNAPSHOT.jar"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/harishkemkar/java_project.git'
            }
        }

        stage('Setup Maven Project on Build Server') {
            steps {
                sshagent (credentials: ['build-server-key']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ec2-user@${BUILD_SERVER_IP} << 'EOF'
                        mkdir -p ${BUILD_DIR}
                        cd ${BUILD_DIR}

                        if [ ! -d "${BUILD_DIR}/myapp" ]; then
                          mvn archetype:generate \
                            -DgroupId=com.myapp \
                            -DartifactId=myapp \
                            -DarchetypeArtifactId=maven-archetype-quickstart \
                            -DinteractiveMode=false
                        fi
*
                        if [ -f "${BUILD_DIR}/MyApp.java" ]; then
                          mv ${BUILD_DIR}/MyApp.java ${BUILD_DIR}/myapp/src/main/java/com/myapp/
                        fi
EOF
                    """
                }
            }
        }

        stage('Fix POM for Java 17') {
            steps {
                sshagent (credentials: ['build-server-key']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ec2-user@${BUILD_SERVER_IP} << 'EOF'
                        cd ${BUILD_DIR}/myapp

EOF
                    """
                }
            }
        }

        stage('Build on Build Server') {
            steps {
                sshagent (credentials: ['build-server-key']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ec2-user@${BUILD_SERVER_IP} << 'EOF'
                        cd ${BUILD_DIR}/myapp
                        mvn clean package
EOF
                    """
                }
            }
        }

        stage('Archive Artifact') {
            steps {
                sshagent (credentials: ['build-server-key']) {
                    sh """
                        scp -o StrictHostKeyChecking=no ec2-user@${BUILD_SERVER_IP}:${BUILD_DIR}/myapp/target/${APP_JAR} .
                    """
                }
                archiveArtifacts artifacts: "${APP_JAR}", fingerprint: true
            }
        }

        stage('Deploy JAR to Production') {
            steps {
                sshagent (credentials: ['build-server-key']) {
                    sh """
                        scp -o StrictHostKeyChecking=no ${APP_JAR} ec2-user@${DEPLOY_SERVER_IP}:${DEPLOY_DIR}/
                    """
                }
            }
        }
    }
}
