data "terraform_remote_state" "networking" {
  backend = "s3"

  config = {
    bucket = "tf-state-challenge-bucket"
    key    = local.networking_state_path
    region = "us-east-2"
  }
}