pipeline {
    agent any

    environment {
        BUILD_SERVER_IP = "3.110.157.209"
        DEPLOY_SERVER_IP = "3.110.157.209"
        BUILD_DIR = "/home/ec2-user/java_application"
        DEPLOY_DIR = "/home/ec2-user/app"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/harishkemkar/java_project.git'
            }
        }

        stage('Build on Build Server') {
            steps {
                sshagent (credentials: ['build-server-key']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ec2-user@${BUILD_SERVER_IP} << 'EOF'
                        # Update packages
                        sudo yum update -y

                        # Install Git
                        sudo yum install -y git

                        # Install Java (Amazon Linux 3 uses Corretto/OpenJDK)
                        sudo yum install -y java-17-amazon-corretto

                        # Install Maven
                        sudo yum install -y maven

                        # Verify installations
                        git --version
                        java -version
                        mvn -v

                        # Navigate to app directory and build
                        cd ${BUILD_DIR}
                        git pull
                        mvn clean package
                        EOF
                    """
                }
            }
        }

        stage('Copy JAR file to Deployment Server') {
            steps {
                sshagent (credentials: ['deploy-server-key']) {
                    sh """
                        scp -o StrictHostKeyChecking=no \
                        ec2-user@${BUILD_SERVER_IP}:${BUILD_DIR}/target/myapp-1.0-SNAPSHOT.jar \
                        ec2-user@${DEPLOY_SERVER_IP}:${DEPLOY_DIR}/
                    """
                }
            }
        }

        stage('Run on Deployment Server') {
            steps {
                sshagent (credentials: ['deploy-server-key']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ec2-user@${DEPLOY_SERVER_IP} \
                        "cd ${DEPLOY_DIR} && nohup java -jar myapp-1.0-SNAPSHOT.jar > app.log 2>&1 &"
                    """
                }
            }
        }
    }
}
