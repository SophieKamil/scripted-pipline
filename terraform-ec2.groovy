properties([
    parameters([
        booleanParam(defaultValue: true, description: 'Do you want to run terrform apply', name: 'terraform_apply'),
        booleanParam(defaultValue: false, description: 'Do you want to run terrform destroy', name: 'terraform_destroy'),
        choice(choices: ['dev', 'qa', 'prod'], description: '', name: 'environment'),
        string(defaultValue: '', description: 'Provide AMI ID', name: 'ami_id', trim: false)
    ])
])

def aws_region_var = ''

if(params.environment == "dev"){
    aws_region_var = "us-east-1"
}
else if(params.environment == "qa"){
    aws_region_var = "us-east-2"
}
else if(params.environment == "prod"){
    aws_region_var = "us-west-2"
}

def tf_vars = """
    s3_bucket = \"jenkins-terraform-class\"
    s3_folder_project = \"terraform_ec2\"
    s3_folder_region = \"us-east-1\"
    s3_folder_type = \"class\"
    s3_tfstate_file = \"infrastructure.tfstate\"
    environment = \"${params.environment}\"
    region      = \"${aws_region_var}\"
    public_key  = \"ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDNUxbUgQ1rR6KIVE2VoY4iASU+W6FuhDXlbkII9KdkDu450p8eIKBjIdSQ8K8d2ZD8dRCCvzazevcp4/OngIaMFRUcuN9w+M++eaEpy0PZAiyQyIYefhir62cikuaPlX9zCOQ7n/2TSJsf+HcaAWIcfeDeXAmMjHYWxgnzO7TUatld9TIHrUqQy8zJXPUcNcMxiFUK8HAj7mnJwmw4Y19i983f5uou4GX0YeFAq/Q3Bi7gzdnYK+7/5GOSidsB1jfdlt0MT8SgW9tWxAy5l87WRGNpynXjDgmTqsuCDExEjRdCnIqqhY6FHZhjKMHAllCxoQ9Izc5JxBF1ILwUY5W1 sohibasaipova@Sohibas-Air\"
    ami_id      = \"${params.ami_id}\"
"""

node{
    stage("Pull Repo"){
        cleanWs()
        git url: 'https://github.com/SophieKamil/terraform-ec2.git'
    }

    withCredentials([usernamePassword(credentialsId: 'jenkins-aws-access-key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${aws_region_var}"]) {
            stage("Terrraform Init"){
                writeFile file: "${params.environment}.tfvars", text: "${tf_vars}"
                sh """
                    bash setenv.sh ${environment}.tfvars
                    terraform-13 init
                """
            }        
            
            if (terraform_apply.toBoolean()) {
                stage("Terraform Apply"){
                    sh """
                        terraform-13 apply -var-file ${environment}.tfvars -auto-approve
                    """
                }
            }
            else if (terraform_destroy.toBoolean()) {
                stage("Terraform Destroy"){
                    sh """
                        terraform-13 destroy -var-file ${environment}.tfvars -auto-approve
                    """
                }
            }
            else {
                stage("Terraform Plan"){
                    sh """
                        terraform-13 plan -var-file ${environment}.tfvars
                    """
                }
            }
        }        
    }    
}
