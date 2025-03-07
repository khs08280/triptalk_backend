pipeline {
    agent any

    environment {
            DOCKER_USER = 'jenkins-access'
            DOCKER_SERVER = '175.45.204.245'
            DOCKER_PROJECT_PATH = '/home/ubuntu/triptalk_docker-compose'

            NCP_REGISTRY = 'triptalk-registry.kr.ncr.ntruss.com/triptalk-app:latest'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build Project') {
            steps {
                sh './gradlew clean build'
            }
        }
        stage('NCP Push') {
            steps {
                sshagent(credentials: ['docker-server-ssh-credentials']) {
                    withCredentials([usernamePassword(
                        credentialsId: 'ncp-registry-credentials',
                        usernameVariable: 'NCP_REGISTRY_USER',
                        passwordVariable: 'NCP_REGISTRY_PASSWORD'
                    )]){
                        sh '''
                            echo ${NCP_REGISTRY_PASSWORD} | docker login -u ${NCP_REGISTRY_USER} --password-stdin ${NCP_REGISTRY}

                            docker build -t ${NCP_REGISTRY} .
                            docker push ${NCP_REGISTRY}
                        '''
                    }
                }
            }
        }
        stage('Deploy to Docker Server') {
            steps {
                sshagent(credentials: ['docker-server-ssh-credentials']) {
                    withCredentials([usernamePassword(
                        credentialsId: 'ncp-registry-credentials',
                        usernameVariable: 'NCP_REGISTRY_USER',
                        passwordVariable: 'NCP_REGISTRY_PASSWORD'
                    )]) {
                        sh '''
                            ssh ${DOCKER_USER}@${DOCKER_SERVER} << EOF
                            cd ${DOCKER_PROJECT_PATH}

                            echo ${NCP_REGISTRY_PASSWORD} | docker login -u ${NCP_REGISTRY_USER} ${NCP_REGISTRY} --password-stdin
                            git pull origin main

                            docker pull ${NCP_REGISTRY}

                            docker compose down
                            docker compose up -d

                            docker image prune -f
                        '''
                    }

                }
            }
        }
    }
}