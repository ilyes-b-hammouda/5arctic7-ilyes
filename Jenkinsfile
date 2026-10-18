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
        IMAGE_TAG = "${BUILD_NUMBER}"
        K8S_NAMESPACE = "devops-app"
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

                    publishHTML(target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'backend/target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report'
                    ])
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
                        script {
                            echo "Checking SonarQube Quality Gate status..."
                            // Give SonarQube a few seconds to finish processing the task
                            sleep 10
                            
                            def qgStatus = sh(
                                script: "curl -s 'http://192.168.33.10:9000/api/qualitygates/project_status?projectKey=tn.esprit:backend' | grep -o '\"status\":\"[^\"]*\"' | head -1 | cut -d'\"' -f4",
                                returnStdout: true
                            ).trim()
                            
                            echo "SonarQube Quality Gate Status: ${qgStatus}"
                            
                            if (qgStatus != "OK") {
                                error("Pipeline aborted: Quality Gate failed with status: ${qgStatus}")
                            } else {
                                echo "Quality Gate Passed Successfully ✅"
                            }
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
                sh "docker build -t ${DOCKER_CREDS_USR}/${IMAGE_BACKEND}:${IMAGE_TAG} -t ${DOCKER_CREDS_USR}/${IMAGE_BACKEND}:latest ./backend"
                sh "docker build -t ${DOCKER_CREDS_USR}/${IMAGE_FRONTEND}:${IMAGE_TAG} -t ${DOCKER_CREDS_USR}/${IMAGE_FRONTEND}:latest ./frontend"
            }
        }

        stage('Push to Docker Hub') {
            steps {
                retry(3) {
                    sh 'echo $DOCKER_CREDS_PSW | docker login -u $DOCKER_CREDS_USR --password-stdin'
                    sh "docker push ${DOCKER_CREDS_USR}/${IMAGE_BACKEND}:${IMAGE_TAG}"
                    sh "docker push ${DOCKER_CREDS_USR}/${IMAGE_BACKEND}:latest"
                    sh "docker push ${DOCKER_CREDS_USR}/${IMAGE_FRONTEND}:${IMAGE_TAG}"
                    sh "docker push ${DOCKER_CREDS_USR}/${IMAGE_FRONTEND}:latest"
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                    script {
                        try {
                            sh '''
                                kubectl version --client
                                kubectl get nodes

                                # Inject the Docker Hub user and build tag into the manifests
                                sed -i "s|DOCKER_USER|${DOCKER_CREDS_USR}|g; s|IMAGE_TAG|${IMAGE_TAG}|g" k8s/backend.yaml k8s/frontend.yaml

                                kubectl apply -f k8s/namespace.yaml
                                kubectl apply -f k8s/mysql.yaml
                                kubectl apply -f k8s/backend.yaml
                                kubectl apply -f k8s/frontend.yaml

                                kubectl rollout status deployment/mysql    -n ${K8S_NAMESPACE} --timeout=180s
                                kubectl rollout status deployment/backend  -n ${K8S_NAMESPACE} --timeout=240s
                                kubectl rollout status deployment/frontend -n ${K8S_NAMESPACE} --timeout=180s

                                kubectl get pods,svc -n ${K8S_NAMESPACE}
                            '''
                        } catch (err) {
                            sh '''
                                kubectl get pods -n ${K8S_NAMESPACE} || true
                                kubectl rollout undo deployment/backend  -n ${K8S_NAMESPACE} || true
                                kubectl rollout undo deployment/frontend -n ${K8S_NAMESPACE} || true
                            '''
                            throw err
                        }
                    }
                }
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