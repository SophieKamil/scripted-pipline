node {
    withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master-ssh-key1', keyFileVariable: 'SSHKEY', passphraseVariable: '', usernameVariable: 'SSHUSERNAME')]) {
        stage('Init') {
            sh 'ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@198.211.102.15 yum install epel-release -y'
        }
        stage("Install git") {
            sh 'ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@198.211.102.15 yum install git -y'
        }
        stage("Install Java"){
            sh 'ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@198.211.102.15 yum install java-1.8.0-openjdk-devel -y'
        }
    }
}