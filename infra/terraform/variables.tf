variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "environment" {
  type    = string
  default = "develop"
}

variable "db_password" {
  type      = string
  sensitive = true
}

variable "db_schema" {
  type    = string
  default = "public"
}
