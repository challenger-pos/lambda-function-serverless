# resource "aws_security_group" "lambda_sg" {
#   name   = "lambda-sg"
#   vpc_id = data.terraform_remote_state.rds.outputs.vpc_id
#
#   egress {
#     from_port   = 0
#     to_port     = 0
#     protocol    = "-1"
#     cidr_blocks = ["0.0.0.0/0"]
#   }
#
#   tags = {
#     Name = "lambda-sg"
#   }
# }
