properties([
    parameters([
        string(defaultValue: '', description: 'Please enter VM IP', name: 'nodeIP', trim: true)
        ])
    ])
if (nodeIP.length() > 6) {
    node { 
        stage('Pull Repo') {
            git changelog: false, poll: false, url: 'https://github.com/SophieKamil/ansible-petclinic.git'
        }
        withEnv(['ANSIBLE_HOST_KEY_CHECKING=False', 'SPRINGPETCLINIC_REPO=https://github.com/ikambarov/spring-petclinic.git', 'SPRINGPETCLINIC_BRANCH=master']) {
           
            stage("Install Prerequisites"){
                ansiblePlaybook credentialsId: 'jenkins-master-ssh-key1', inventory: '${nodeIP},', playbook: 'prerequisites.yml'
                }
           
            stage("Pull SpringPetClinic"){
                ansiblePlaybook credentialsId: 'jenkins-master-ssh-key1', inventory: '${nodeIP},', playbook: 'pull_repo.yml'
                }
            stage("Install java"){
                ansiblePlaybook credentialsId: 'jenkins-master-ssh-key1', inventory: '${nodeIP},', playbook: 'install_java.yml'
                }
            stage("Install maven"){
                ansiblePlaybook credentialsId: 'jenkins-master-ssh-key1', inventory: '${nodeIP},', playbook: 'install_maven.yml'
                }
            stage("Start SpringPetClinic"){
                ansiblePlaybook credentialsId: 'jenkins-master-ssh-key1', inventory: '${nodeIP},', playbook: 'start_app.yml'
                }
        }  
    }
}
else {
    error 'Please enter valid IP address'
}