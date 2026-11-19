@Library('cdo-lib') _

pipeline {
    agent { label 'agent-1' }

    environment {
        SERVICE  = 'roadmap-service'
        IMAGE    = '400326779380.dkr.ecr.eu-north-1.amazonaws.com/ivolve-roadmap-service'
        CONTEXT  = 'app/roadmap-service'
        MANIFEST = 'k8s/06-roadmap-service.yaml'
        TAG      = "${BUILD_NUMBER}"
    }

    stages {
        stage('Build Image') {
            steps {
                buildImage(env.IMAGE, env.TAG, env.CONTEXT)
            }
        }
        stage('Scan Image') {
            steps {
                scanImage(env.IMAGE, env.TAG)
            }
        }
        stage('Push Image') {
            steps {
                pushImage(env.IMAGE, env.TAG)
            }
        }
        stage('Delete Image Locally') {
            steps {
                deleteImage(env.IMAGE, env.TAG)
            }
        }
        stage('Update Manifests') {
            steps {
                updateManifest(env.MANIFEST, env.IMAGE, env.TAG)
            }
        }
        stage('Push Manifests') {
            steps {
                pushManifest(env.MANIFEST, env.SERVICE, env.TAG)
            }
        }
    }

    post {
        always {
            echo "Pipeline for ${env.SERVICE} finished."
        }
        success {
            echo "Image ${env.IMAGE}:${env.TAG} built, pushed and the manifest updated."
        }
        failure {
            echo "Pipeline for ${env.SERVICE} failed."
        }
    }
}
