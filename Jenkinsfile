pipeline {
    agent any

    environment {
        BUILD_SERVER_IP = "35.154.84.244"
        BUILD_DIR = "/home/ec2-user/java_application"
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
                        # Ensure build directory exists
                        mkdir -p ${BUILD_DIR}
                        cd ${BUILD_DIR}

                        # Create Maven project structure if not already present
                        if [ ! -d "${BUILD_DIR}/myapp" ]; then
                          mvn archetype:generate \
                            -DgroupId=com.myapp \
                            -DartifactId=myapp \
                            -DarchetypeArtifactId=maven-archetype-quickstart \
                            -DinteractiveMode=false
                        fi

                        # Move your existing Java file into the correct package
                        if [ -f "${BUILD_DIR}/MyApp.java" ]; then
                          mv ${BUILD_DIR}/MyApp.java ${BUILD_DIR}/myapp/src/main/java/com/myapp/
                        fi

                        # Build the project
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
                        scp -o StrictHostKeyChecking=no ec2-user@${BUILD_SERVER_IP}:${BUILD_DIR}/myapp/target/myapp-1.0-SNAPSHOT.jar .
                    """
                }
                archiveArtifacts artifacts: 'myapp-1.0-SNAPSHOT.jar', fingerprint: true
            }
        }
    }
}
