def call(String imageName, String imageTag, String credentialsId = 'docker-hub-credentials') {
    echo "Pushing ${imageName}:${imageTag} to Docker Hub"
    withCredentials([usernamePassword(credentialsId: credentialsId,
                                      usernameVariable: 'DOCKER_USER',
                                      passwordVariable: 'DOCKER_PASS')]) {
        sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
        sh "docker push ${imageName}:${imageTag}"
    }
}
