resource "aws_lambda_function" "auth_lambda" {
  function_name = "auth-document-lambda"

  role   = aws_iam_role.lambda_role.arn
  handler = "auth.document.DocumentAuthHandler::handleRequest"
  runtime = "java21"

  filename = "${path.module}/../../DocumentAuthFunction/target/DocumentAuthFunction-1.0.jar"
  source_code_hash = filebase64sha256(
    "${path.module}/../../DocumentAuthFunction/target/DocumentAuthFunction-1.0.jar"
  )

  timeout      = 10
  memory_size = 512

  environment {
    variables = {
      ENV = "develop"
    }
  }
}