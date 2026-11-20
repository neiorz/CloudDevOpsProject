variable "project_name" {
  type    = string
  default = "ivolve"
}

variable "vpc_id" {
  type = string
}

variable "subnet_id" {
  type = string
}

variable "instance_type" {
  type    = string
  default = "t3.small"
}

variable "public_key" {
  description = "SSH public key content for the Jenkins server"
  type        = string
}

variable "admin_cidr" {
  description = "Address allowed to reach SSH and the Jenkins UI, for example 203.0.113.10/32"
  type        = string
}
