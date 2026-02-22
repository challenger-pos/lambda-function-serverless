variable "aws_region" {
  type    = string
  default = "us-east-2"
}

variable "environment" {
  type    = string
  default = "dev"
}

variable "db_password" {
  type      = string
  sensitive = true
}

variable "db_schema" {
  type    = string
  default = "public"
}
