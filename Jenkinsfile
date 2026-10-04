pipeline {
    agent any

    tools {
        maven 'M2_HOME'
        jdk 'JAVA_HOME'
    }

    environment {
        SONAR_SCANNER_HOME = tool 'SonarQubeScanner'
        DOCKER_CREDS = credentials('ilyes-dockerhubTK')
        IMAGE_BACKEND = "ilyes_5arctic7_backend" 
        IMAGE_FRONTEND = "ilyes_5arctic7_frontend"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/ilyes-b-hammouda/5arctic7-ilyes.git',
                    credentialsId: '4a881dc0-ed67-4c1e-9d57-f0e2ec42af0a'
            }
        }

        stage('Compile') {
            steps {
                dir('backend') {
                    sh 'mvn compile'
                }
            }
        }

        stage('Unit Tests') {
            steps {
                dir('backend') {
                    sh '''
                        docker rm -f mysql-test || true

                        docker run -d --name mysql-test \
                            -e MYSQL_ROOT_PASSWORD=root \
                            -e MYSQL_DATABASE=testdb \
                            -p 3306:3306 mysql:8

                        echo "Waiting for MySQL to start..."
                        until docker exec mysql-test mysqladmin ping -h localhost -u root --password=root --silent; do
                            sleep 2
                        done
                        echo "MySQL is ready!"

                        mvn test
                    '''
                }
            }
            post {
                always {
                    junit 'backend/target/surefire-reports/*.xml'
                    sh 'docker rm -f mysql-test || true'
                }
            }
        }

        stage('Code Analysis') {
            steps {
                dir('backend') {
                    withSonarQubeEnv('SonarQube') {
                        sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar'
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package') {
            steps {
                dir('backend') {
                    sh 'mvn package -DskipTests'
                }
            }
        }

        
        stage('Build Docker Images') {
            steps {
                sh "docker build -t ${DOCKER_CREDS_USR}/${IMAGE_BACKEND}:latest ./backend"
                sh "docker build -t ${DOCKER_CREDS_USR}/${IMAGE_FRONTEND}:latest ./frontend"
            }
        }

        stage('Push to Docker Hub') {
            steps {
                retry(3) {
                    sh 'echo $DOCKER_CREDS_PSW | docker login -u $DOCKER_CREDS_USR --password-stdin'
                    sh "docker push ${DOCKER_CREDS_USR}/${IMAGE_BACKEND}:latest"
                    sh "docker push ${DOCKER_CREDS_USR}/${IMAGE_FRONTEND}:latest"
                }
            }
        }

        stage('Deploy (Docker Compose)') {
            steps {
                sh 'docker compose up -d'
            }
        }
    }

    post {
        always {
            sh 'docker logout'
        }
        success {
            archiveArtifacts artifacts: 'backend/target/*.jar', fingerprint: true
            echo 'Pipeline completed successfully ✅'
        }
        failure {
            echo 'Pipeline failed ❌'
        }
    }
}