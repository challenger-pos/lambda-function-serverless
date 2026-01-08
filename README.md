# 📦 lambda-function-serverless

Este módulo contém uma função AWS Lambda escrita em Java que recebe requisições HTTP (API Gateway) e retorna uma resposta simples. O projeto usa Maven e produz um artefato `serverless-lambda.jar` (uber-jar) no diretório `target/`.

---

## 🧭 Estrutura do projeto

- `src/main/java` — código-fonte (classe principal `br.com.pos.chellenger.Handler`).
- `pom.xml` — configuração do Maven (inclui `maven-shade-plugin` para gerar um *fat JAR*).
- `.github/workflows/ci-cd-lambda.yml` — pipeline de build e deploy para GitHub Actions.

---

## ✨ Pré-requisitos

- Java JDK (recomendado 17 ou 21, ver observação abaixo)
- Maven 3.6+
- AWS CLI configurado (ou `aws-vault`, `aws-profile`)
- Conta AWS com permissões para criar funções Lambda, IAM, S3 (opcional), API Gateway e CloudWatch

> Observação: o projeto está configurado para compilar com Java 21. Confirme se o runtime do Lambda na sua conta suporta Java 21; caso contrário, altere `maven.compiler.source`/`target` para `17` e reconstrua.

---

## 🔨 Build local (passo-a-passo)

1. Compilar e empacotar:

```bash
mvn clean package -DskipTests
# artefato: target/serverless-lambda.jar
```

2. (Opção) Criar um arquivo ZIP para upload (se preferir):

```bash
cd target
zip -j lambda-package.zip serverless-lambda.jar
```

- Nota: para Java a *JAR* já é um ZIP válido; o CLI aceita tanto `jar` quanto `zip`.

---

## 🔐 Criar papel IAM para a função Lambda

1. Criar trust policy (arquivo `trust-policy.json`):

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {"Service": "lambda.amazonaws.com"},
      "Action": "sts:AssumeRole"
    }
  ]
}
```

2. Criar a role e anexar a policy de execução básica (CloudWatch logs):

```bash
aws iam create-role --role-name lambda-exec-role --assume-role-policy-document file://trust-policy.json
aws iam attach-role-policy --role-name lambda-exec-role --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole
```

- Se a função precisar acessar recursos em uma VPC (ex.: RDS), configure também as perms necessárias e coloque a função na VPC (subnets privadas e security groups).

---

## 🚀 Criar a função Lambda (CLI)

Exemplo de criação:

```bash
aws lambda create-function \
  --function-name my-serverless-lambda \
  --runtime java17 \
  --handler br.com.pos.chellenger.Handler::handleRequest \
  --role arn:aws:iam::<ACCOUNT_ID>:role/lambda-exec-role \
  --zip-file fileb://target/lambda-package.zip \
  --timeout 30 \
  --memory-size 512 \
  --region us-east-2
```

- Se a função já existir, faça o upload do código com `update-function-code`:

```bash
aws lambda update-function-code --function-name my-serverless-lambda --zip-file fileb://target/lambda-package.zip
```

- Para configurar variáveis de ambiente ou ajustar memória/timeout:

```bash
aws lambda update-function-configuration --function-name my-serverless-lambda --environment Variables={ENV=prod,LOG_LEVEL=info}
```

---

## 🌐 Integrar com API Gateway (HTTP API rápida — CLI)

1. Criar a API HTTP:

```bash
API_ID=$(aws apigatewayv2 create-api --name "lambda-http-api" --protocol-type HTTP --target arn:aws:lambda:us-east-2:<ACCOUNT_ID>:function:my-serverless-lambda --query "ApiId" --output text)
```

2. Criar integração e rota simples (ex.: POST /):

```bash
INTEGRATION_ID=$(aws apigatewayv2 create-integration --api-id $API_ID --integration-type AWS_PROXY --integration-uri arn:aws:lambda:us-east-2:<ACCOUNT_ID>:function:my-serverless-lambda --payload-format-version 2.0 --query "IntegrationId" --output text)
aws apigatewayv2 create-route --api-id $API_ID --route-key "POST /" --target "integrations/$INTEGRATION_ID"
aws apigatewayv2 create-deployment --api-id $API_ID
aws apigatewayv2 create-stage --api-id $API_ID --stage-name prod --auto-deploy
```

3. Permitir que API Gateway invoque a Lambda:

```bash
aws lambda add-permission --function-name my-serverless-lambda --statement-id apigw-invoke --action lambda:InvokeFunction --principal apigateway.amazonaws.com --source-arn "arn:aws:execute-api:us-east-2:<ACCOUNT_ID>:${API_ID}/*/*/"
```

4. Testar endpoint:

```bash
API_URL=$(aws apigatewayv2 get-api --api-id $API_ID --query "ApiEndpoint" --output text)
curl -X POST ${API_URL}/ -d '{"hello":"world"}' -H 'Content-Type: application/json'
```

> Obs.: Também é possível configurar a integração via Console se preferir UI.

---

## 📦 CI/CD (GitHub Actions)

O repositório já contém um workflow em `.github/workflows/ci-cd-lambda.yml` que compila o projeto e atualiza o código da função (usa as variáveis/segredos `AWS_ACCESS_KEY_ID` e `AWS_SECRET_ACCESS_KEY`). Para usar:

1. Configure os *secrets* no repositório (ou organization): `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`.
2. Ajuste `FUNCTION_NAME` e `AWS_REGION` no workflow conforme necessário.

---

## 🧪 Testes e monitoramento

- Logs: CloudWatch Logs (verifique o group `/aws/lambda/<function-name>`).
- Métricas: Latência, invocações, erros no CloudWatch Metrics.
- Recomendado configurar retenção de logs e alarmes para erros elevados.

---

## ❗ Problemas comuns & soluções

- Handler inválido (ex.: `Invalid handler`): confirme o valor de `--handler` — deve ser `package.Class::method` (`br.com.pos.chellenger.Handler::handleRequest`).
- Erro de runtime (versão Java): ajuste `maven.compiler.target` para a versão suportada pelo Lambda (ex.: 17) e recompile.
- Pacote grande (>50 MB): envie o artefato para S3 e use `--s3-bucket`/`--s3-key` no `create-function`/`update-function-code`.
- Permissões: verifique o papel IAM e a attachment da policy `AWSLambdaBasicExecutionRole`.

---
