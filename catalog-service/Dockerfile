FROM openjdk:17-ea-jdk-slim
VOLUME /tmp
COPY build/libs/catalog-service-1.0.jar CatalogService.jar
ENTRYPOINT ["java","-jar","CatalogService.jar"]




pipeline {
    agent any
    environment {
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-credentials') // Docker Hub 인증 정보
        DOCKER_HUB_REPO = 'tlrruddbs/user-service' // Docker Hub 저장소 이름
        VERSION = '1.0' // Docker 이미지 버전 설정
    }
    stages {
        stage('Build') {
            steps {
                // gradlew 파일에 실행 권한 부여
                sh 'chmod +x ./gradlew'
                // 빌드 실행
                sh './gradlew clean build'
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    // Docker 이미지를 1.0 버전으로 빌드
                    sh 'docker build -t ${DOCKER_HUB_REPO}:${VERSION} .'
                }
            }
        }
        stage('Push to Docker Hub') {
            steps {
                script {
                    // Docker Hub로 1.0 버전의 이미지를 푸시
                    docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                        sh 'docker push ${DOCKER_HUB_REPO}:${VERSION}'
                    }
                }
            }
        }
    }
}
