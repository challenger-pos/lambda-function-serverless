variable "aws_region" {
  type    = string
  default = "us-east-2"
}

variable "environment" {
  type    = string
  default = "develop"
}

variable "db_user" {
  type = string
}

variable "db_password" {
  type      = string
  sensitive = true
}
