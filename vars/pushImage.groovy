def call(String imageName, String imageTag, String region = 'eu-north-1', String credentialsId = 'docker-hub-credentials') {
    if (imageName.contains('.dkr.ecr.')) {
        def registry = imageName.split('/')[0]
        echo "Pushing ${imageName}:${imageTag} to AWS ECR (${registry}) in ${region}"
        sh "aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin ${registry}"
        sh "docker push ${imageName}:${imageTag}"
    } else {
        echo "Pushing ${imageName}:${imageTag} to Docker Hub"
        withCredentials([usernamePassword(credentialsId: credentialsId,
                                          usernameVariable: 'DOCKER_USER',
                                          passwordVariable: 'DOCKER_PASS')]) {
            sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
            sh "docker push ${imageName}:${imageTag}"
        }
    }
}
