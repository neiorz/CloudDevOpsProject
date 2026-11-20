module "network" {
  source       = "./modules/network"
  project_name = var.project_name
  azs          = ["eu-north-1a", "eu-north-1b"]
}

module "server" {
  source        = "./modules/server"
  project_name  = var.project_name
  vpc_id        = module.network.vpc_id
  subnet_id     = module.network.public_subnet_ids[0]
  instance_type = var.jenkins_instance_type
  public_key    = file(pathexpand(var.ssh_public_key_path))
  admin_cidr    = var.admin_cidr
}

module "ecr" {
  source       = "./modules/ecr"
  project_name = var.project_name
  repositories = ["frontend", "auth-service", "roadmap-service"]
}

module "eks" {
  source             = "./modules/eks"
  project_name       = var.project_name
  private_subnet_ids = module.network.private_subnet_ids
  node_instance_type = var.eks_node_instance_type
  desired_size       = var.eks_desired_size
}
