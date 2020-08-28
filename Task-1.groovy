properties([parameters([string(defaultValue: '', description: 'Please enter IP', name: 'nodeIP', trim: false), string(defaultValue: '', description: 'Please provide branch', name: 'DIVISION', trim: false)])])

node {
    withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master-ssh-key1', keyFileVariable: 'SSHKEY', passphraseVariable: '', usernameVariable: 'SSHUSERNAME')]) {

        stage("Pull SCM") {
            checkout([$class: 'GitSCM', branches: [[name: '*/${DIVISION}']], 
            doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: 
            [[url: 'https://github.com/ikambarov/melodi']]])        
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
        stage("Copy Files"){

            sh 'scp -o StrictHostKeyChecking=no -i $SSHKEY -rv * $SSHUSERNAME@${nodeIP}:/var/www/html'
        }
        stage("Clean Workspace"){
            cleanWs()
        }
    }
} 




