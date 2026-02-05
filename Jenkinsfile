pipeline {
    agent any

    environment {
        BUILD_SERVER_IP = "13.201.126.151"
        DEPLOY_SERVER_IP = "3.108.227.85"
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

                        if grep -q '<artifactId>maven-compiler-plugin</artifactId>' pom.xml; then
                          sed -i 's/<source>[0-9]*<\\/source>/<source>17<\\/source>/g' pom.xml
                          sed -i 's/<target>[0-9]*<\\/target>/<target>17<\\/target>/g' pom.xml
                        else
                          sed -i '/<\\/project>/i \
                          <build>\\n\
                            <plugins>\\n\
                              <plugin>\\n\
                                <groupId>org.apache.maven.plugins<\\/groupId>\\n\
                                <artifactId>maven-compiler-plugin<\\/artifactId>\\n\
                                <version>3.8.1<\\/version>\\n\
                                <configuration>\\n\
                                  <source>17<\\/source>\\n\
                                  <target>17<\\/target>\\n\
                                <\\/configuration>\\n\
                              <\\/plugin>\\n\
                            <\\/plugins>\\n\
                          <\\/build>' pom.xml
                        fi
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
