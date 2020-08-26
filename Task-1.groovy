properties([parameters([string(defaultValue: '', description: 'Please provide the IP', name: 'nodeIP', trim: true)])])
node {
    withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master-ssh-key1', keyFileVariable: 'SSHKEY', passphraseVariable: '', usernameVariable: 'SSHUSERNAME')]) {

        stage("Pull SCM") {
            git 'https://github.com/ikambarov/melodi.git'
        }

        stage('Install epel') {

            sh 'ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@${nodeIP}  yum install epel-release -y'
        }
        stage('Install apache') {

            sh 'ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@${nodeIP}  yum install httpd -y'
        }
        stage("Enable Apache"){

            sh 'ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@${nodeIP}  systemctl enable httpd'
        }
        stage("Start Apache"){

            sh 'ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@${nodeIP}  systemctl start httpd'
        }
    }
}