def call(Map config = [:]) {
    sh "oc project ${openshift_project}"
    def deploymentExists = sh(script: "oc get deploy/${app_name}", returnStatus: true) == 0
    if (deploymentExists) {
        echo "Deployment ${app_name} exists, refreshing app..."
        sh "oc set triggers deploy/${app_name} --from-image=${app_name}:latest"
        sh "oc rollout restart deploy/${app_name}"
    } else {
        echo "Deployment ${app_name} does not exist, deploying app..."
        sh "oc new-app --image=quay.io/${quay_repo}/${image_name} --name=${app_name}"
        sh "oc set env --from=secret/app-secrets deploy/${app_name}"
        sh "oc set env --from=configmap/app-configmap deploy/${app_name}"
        sh "oc expose svc/${app_name}"
    }
}
