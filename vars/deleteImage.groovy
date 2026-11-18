def call(String imageName, String imageTag) {
    echo "Deleting local image ${imageName}:${imageTag}"
    sh "docker rmi ${imageName}:${imageTag}"
}
