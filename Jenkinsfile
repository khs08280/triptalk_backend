pipeline {
    agent any

    environment {
            DOCKER_SERVER = '175.45.204.245'
            DOCKER_USER = 'jenkins-access'
            DOCKER_PROJECT_PATH = '/home/ubuntu/triptalk'

            NCP_REGISTRY = 'triptalk-registry.kr.ncr.ntruss.com'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                sshagent(credentials: ['docker-server-ssh-credentials']) {
                    sh '''
                        ssh ${DOCKER_USER}@${DOCKER_SERVER} << EOF
                        cd ${DOCKER_PROJECT_PATH}
                        docker compose down
                        git pull
                        ./gradlew clean build
                    '''
                }
            }
        }
        stage('Docker Build And NCP Push') {
            steps {
                sshagent(credentials: ['docker-server-ssh-credentials']) {
                    withCredentials([usernamePassword(
                        credentialsId: 'ncp-registry-credentials',
                        usernameVariable: 'NCP_REGISTRY_USER',
                        passwordVariable: 'NCP_REGISTRY_PASSWORD'
                    )]){
                        sh '''
                            ssh ${DOCKER_USER}@${DOCKER_SERVER} << EOF
                            cd ${DOCKER_PROJECT_PATH}

                            docker compose up -d --build
                            docker image prune -f
                        '''
                    }
                }
            }
        }
    }
}