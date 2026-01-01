resource "aws_lambda_function" "auth_lambda" {
  function_name = "auth-document-lambda-${var.environment}"

  role   = aws_iam_role.lambda_role.arn
  handler = "auth.document.DocumentAuthHandler::handleRequest"
  runtime = "java21"

  filename = "${path.module}/../../DocumentAuthFunction/target/DocumentAuthFunction-1.0.jar"
  source_code_hash = filebase64sha256(
    "${path.module}/../../DocumentAuthFunction/target/DocumentAuthFunction-1.0.jar"
  )

  timeout      = 10
  memory_size = 512

  vpc_config {
    subnet_ids         = data.terraform_remote_state.rds.outputs.subnet_ids
    security_group_ids = [aws_security_group.lambda_sg.id]
  }

  environment {
    variables = {
      ENV       = var.environment
      DB_SCHEMA = var.environment
      DB_HOST   = data.terraform_remote_state.rds.outputs.rds_endpoint_host
      DB_NAME   = data.terraform_remote_state.rds.outputs.db_name
      DB_USER   = data.terraform_remote_state.rds.outputs.db_username
      DB_PASSWORD   = var.db_password
      DB_PORT   = data.terraform_remote_state.rds.outputs.db_port
    }
  }
}