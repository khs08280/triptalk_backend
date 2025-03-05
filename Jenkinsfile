pipeline {
    agent any

    environment {
            DOCKER_SERVER = '175.45.204.245'
            DOCKER_USER = 'jenkins-access'
            DOCKER_PROJECT_PATH = '/home/ubuntu/triptalk'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('SSH into Docker Server') {
            steps {
                sshagent(credentials: ['docker-server-ssh-credentials']) {
                    sh """
                        ssh ${DOCKER_USER}@${DOCKER_SERVER} << EOF
                        cd ${DOCKER_PROJECT_PATH}
                        docker ps
                        EOF
                    """
                }
            }
        }
        stage('Test'){
            steps{
                echo 'Testing...dfdf' // 최소 하나의 스텝 추가
            }
        }
        stage('Deploy'){
            steps{
                echo 'Deploying...' // 최소 하나의 스텝 추가
            }
        }
    }
}