pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Building...'  // 최소 하나의 스텝 추가
                // 여기에 빌드 관련 명령어 추가 (예: sh 'mvn clean package')
            }
        }

        stage('Test'){
            steps{
                echo 'Testing...' // 최소 하나의 스텝 추가
                //여기에 테스트 실행 추가
            }
        }
        stage('Deploy'){
            steps{
                echo 'Deploying...' // 최소 하나의 스텝 추가
                //여기에 배포 스크립트 실행
            }
        }
    }
}