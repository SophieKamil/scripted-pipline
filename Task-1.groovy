properties([parameters([string(defaultValue: '', description: 'Please provide the IP', name: 'nodeIP', trim: true)])])
node {
    withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master-ssh-key1', keyFileVariable: 'SSHKEY', passphraseVariable: '', usernameVariable: 'SSHUSERNAME')]) {

        stage("Pull SCM") {
            git 'https://github.com/ikambarov/melodi.git'
        }

        stage('Install the packages') {

            println("Installing the epel-release and httpd")
            sh 'ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@${nodeIP} sudo yum install epel-release -y &&  yum install httpd -y'
        }
        stage("Enable Apache"){

            sh 'ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@${nodeIP} sudo  systemctl enable httpd'
        }
        stage("Start Apache"){

            sh 'ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@${nodeIP} sudo systemctl start httpd'
        }
    }
}