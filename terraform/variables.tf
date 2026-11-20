variable "aws_region" {
  type    = string
  default = "eu-north-1"
}

variable "project_name" {
  type    = string
  default = "ivolve"
}

variable "jenkins_instance_type" {
  type    = string
  default = "t3.small"
}

variable "eks_node_instance_type" {
  type    = string
  default = "t3.small"
}

variable "eks_desired_size" {
  type    = number
  default = 2
}

variable "ssh_public_key_path" {
  description = "Path of the SSH public key installed on the Jenkins server"
  type        = string
  default     = "~/.ssh/ivolve-jenkins-key.pub"
}

variable "admin_cidr" {
  description = "Address allowed to reach SSH and the Jenkins UI (use your own /32)"
  type        = string

  validation {
    condition     = can(cidrhost(var.admin_cidr, 0)) && var.admin_cidr != "0.0.0.0/0"
    error_message = "admin_cidr must be a valid CIDR and must not be 0.0.0.0/0."
  }
}
