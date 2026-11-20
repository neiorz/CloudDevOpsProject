variable "project_name" {
  type    = string
  default = "ivolve"
}

variable "repositories" {
  type    = list(string)
  default = ["frontend", "auth-service", "roadmap-service"]
}
