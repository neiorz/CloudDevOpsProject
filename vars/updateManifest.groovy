def call(String manifestFile, String imageName, String imageTag) {
    echo "Setting ${imageName}:${imageTag} in ${manifestFile}"
    sh "sed -i 's|image: ${imageName}:.*|image: ${imageName}:${imageTag}|' ${manifestFile}"
    sh "grep -n 'image: ${imageName}' ${manifestFile}"
}
