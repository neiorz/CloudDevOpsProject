def call(String manifestFile, String serviceName, String imageTag, String branch = 'main', String credentialsId = 'github-cdo-token') {
    echo "Pushing ${manifestFile} to GitHub (branch ${branch})"
    withCredentials([usernamePassword(credentialsId: credentialsId,
                                      usernameVariable: 'GIT_USER',
                                      passwordVariable: 'GIT_PASS')]) {
        sh """
            git config user.email 'jenkins@ivolve.com'
            git config user.name 'Jenkins CI'
            git add ${manifestFile}
            if git diff --cached --quiet; then echo 'No manifest change to push'; exit 0; fi
            git commit -m 'ci(${serviceName}): update image tag to ${imageTag}'
            REPO=\$(git config --get remote.origin.url | sed 's|https://||')
            for i in 1 2 3; do
                git fetch https://\${GIT_USER}:\${GIT_PASS}@\$REPO ${branch} && git rebase FETCH_HEAD && git push https://\${GIT_USER}:\${GIT_PASS}@\$REPO HEAD:${branch} && exit 0
                echo "push failed, retrying (\$i/3)"
                git rebase --abort 2>/dev/null || true
                sleep 5
            done
            exit 1
        """
    }
}
