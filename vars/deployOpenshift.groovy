def call(Map config = [:]) {
    sh "oc project ${config.openshift_project}"
    def deploymentExists = sh(script: "oc get deploy/${config.app_name}", returnStatus: true) == 0
    if (deploymentExists) {
        echo "Deployment ${config.app_name} exists, refreshing app..."
        sh "oc set triggers deploy/${config.app_name} --from-image=${config.app_name}:latest -c ${config.app_name} "
        sh "oc rollout restart deploy/${config.app_name}"
    } else {
        echo "Deployment ${config.app_name} does not exist, deploying app..."
        sh "oc new-app --image=quay.io/${config.quay_repo}/${config.image_name} --name=${config.app_name}"
        sh "oc set env --from=secret/app-secrets deploy/${config.app_name}"
        sh "oc set env --from=configmap/app-configmap deploy/${config.app_name}"
        sh "oc expose svc/${config.app_name}"
    }
}
