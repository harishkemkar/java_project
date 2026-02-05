#!/bin/bash
# Update all packages
sudo yum update -y

# Install Git
sudo yum install -y git

# Install Java (Amazon Corretto 17 is recommended)
sudo yum install -y java-17-amazon-corretto

# Install Maven
sudo yum install -y maven

# Create application folder
mkdir -p /home/ec2-user/java_application

# Create a text file with version info
{
  echo "Git version: $(git --version)"
  echo "Java version:"
  java -version 2>&1 | head -n 1
  echo "Maven version: $(mvn -v | head -n 1)"
} > /home/ec2-user/java_application/version_info.txt

# Ensure ec2-user owns the folder and file
chown -R ec2-user:ec2-user /home/ec2-user/java_application
