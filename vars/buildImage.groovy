def call(String imageName, String imageTag, String context = '.') {
    echo "Building image ${imageName}:${imageTag} from ${context}"
    sh "docker build -t ${imageName}:${imageTag} ${context}"
}
