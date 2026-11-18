def call(String imageName, String imageTag, String severity = 'HIGH,CRITICAL', int exitCode = 0) {
    echo "Scanning ${imageName}:${imageTag} with Trivy (${severity})"
    sh "trivy image --scanners vuln --timeout 30m --exit-code ${exitCode} --severity ${severity} ${imageName}:${imageTag}"
}
