properties([
    parameters([
        string(defaultValue: '', description: 'Please enter VM IP', name: 'nodeIP', trim: true),
        string(defaultValue: '', description: 'Please enter branch name', name: 'branch', trim: true)
        ])
    ])
if (nodeIP?.trim()) {
    node {
        withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master-ssh-key1', keyFileVariable: 'SSHKEY', passphraseVariable: '', usernameVariable: 'SSHUSERNAME')]) {
            stage('Pull Repo') {
                git changelog: false, poll: false, url: 'https://github.com/ikambarov/ansible-melodi.git'
            }
            withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master-ssh-key1', keyFileVariable: 'SSHKEY', passphraseVariable: '', usernameVariable: 'SSHUSERNAME')]) {
                stage("Install Playbook"){
                    sh '''
                        export ANSIBLE_HOST_KEY_CHECKING=False
                        ansible-playbook -i "161.35.120.248," --private-key $SSHKEY main.yml
                        '''
                }
            }
        }
    }
}
else {
    error 'Please enter valid IP address'
}