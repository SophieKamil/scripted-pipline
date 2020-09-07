properties([
    parameters([
        string(defaultValue: '', description: 'Please provide remote IP to provision.', name: 'remoHost', trim: true)
        ])
    ])
if (remoHost.length() > 6) {
        node {
            stage('Pull Repo') {
                git url: 'https://github.com/SophieKamil/ansible-petclinic.git'
            }
            withEnv(['ANSIBLE_HOST_KEY_CHECKING=False', 'SPRINGPETCLINIC_REPO=https://github.com/ikambarov/spring-petclinic.git', 'SPRINGPETCLINIC_BRANCH=master']) {
                stage("Install Prerequisites"){
                    ansiblePlaybook credentialsId: 'jenkins-master-ssh-key1', inventory: "${params.remoHost},", playbook: "${WORKSPACE}/prerequisites.yml"
                    }
                stage("Pull SpringPetClinic"){
                    ansiblePlaybook credentialsId: 'jenkins-master-ssh-key1', inventory: "${params.remoHost},", playbook: "${WORKSPACE}/pull_repo.yml"
                    }
                stage("Install java"){
                    ansiblePlaybook credentialsId: 'jenkins-master-ssh-key1', inventory: "${params.remoHost},", playbook: "${WORKSPACE}/install_java.yml"
                    }
                stage("Install maven"){
                    ansiblePlaybook credentialsId: 'jenkins-master-ssh-key1', inventory: "${params.remoHost},", playbook: "${WORKSPACE}/install_maven.yml"
                    }
                stage("Start SpringPetClinic"){
                    ansiblePlaybook credentialsId: 'jenkins-master-ssh-key1', inventory: "${params.remoHost},", playbook: "${WORKSPACE}/start_app.yml"
                    }
            }  
        }
    } else {
        error 'Please enter valid IP address for remote host'
}