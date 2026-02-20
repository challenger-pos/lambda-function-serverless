locals {
  rds_state_path = "v4/rds-os/${var.environment}/terraform.tfstate"
}

locals {
  networking_state_path = "v4/networking/${var.environment}/terraform.tfstate"
}