variable "project_name" {
  type    = string
  default = "ivolve"
}

variable "cluster_version" {
  type    = string
  default = "1.32"
}

variable "private_subnet_ids" {
  type = list(string)
}

variable "node_instance_type" {
  type    = string
  default = "t3.small"
}

variable "desired_size" {
  type    = number
  default = 2
}
