pipeline {
    agent {
        label 'master'
    }
    options {
        timestamps()
    }
    environment {
       gitlabRepo = 'https://github.com/szubair/ucapstone.git'
       gitlabBranch = 'master' 
       namespace = 'udacity'
       application = 'cron-stopvms'
       dockerBaseTag = "sabeerz/gcproj6"
    }
    stages {
        stage("Checkout SCM") {
            steps {
                cleanWs()
                checkout(
                    changelog: true,
                    poll: true,
                    scm: [
                        $class : 'GitSCM',
                        branches : [[name: "${gitlabBranch}"]],
                        doGenerateSubmoduleConfigurations: false,
                        extensions : [
                            [$class: 'LocalBranch', localBranch: "${gitlabBranch}"],
                            [$class: 'RelativeTargetDirectory', relativeTargetDir: 'module']],
                        userRemoteConfigs : [[
                            credentialsId: 'credsIdk8s',
                            refspec : "+refs/heads/${gitlabBranch}:refs/remotes/origin/${gitlabBranch}",
                            url : "${gitlabRepo}"
                        ]]
                    ]
                )
            }
        }

        stage("Docker build"){
            steps {
                script {
                    dir('module'){
                        dockerTag = "${dockerBaseTag}${application}:$BUILD_NUMBER"
                        dockerTagLatest = "${dockerBaseTag}${application}:latest"
                        sh """
                            docker build -t ${dockerTag} .
                            docker tag ${dockerTag} ${dockerTagLatest}
                            docker push ${dockerTag}
                            docker push ${dockerTagLatest}
                            docker rmi -f ${dockerTagLatest} ${dockerTag}
                        """
                    }
                }
            }
        }
        
        stage("Deploy into k8s"){
            steps {
                dir('module'){
                    script {
                        echo "Create cronjob in k8s"
                        sh "kubectl create -f k8s-cronjob.yaml -n ${namespace}"
                        sh "kubectl get cronjob -n sco-indexer -n ${namespace}"
                    }
                }
            }
        }
    }
}
