terraform {
  backend "s3" {
    bucket         = "tf-state-challenge-bucket"
    key            = "lambda/${var.environment}/terraform.tfstate"
    region         = "us-east-2"
    encrypt        = true
  }
}