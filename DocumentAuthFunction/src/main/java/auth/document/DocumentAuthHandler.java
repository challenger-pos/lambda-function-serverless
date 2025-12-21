package auth.document;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class DocumentAuthHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final CustomerStatusService customerStatusService;

    public DocumentAuthHandler() {
        this.customerStatusService = new DatabaseService();
    }

    public DocumentAuthHandler(CustomerStatusService customerStatusService) {
        this.customerStatusService = customerStatusService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        response.setHeaders(headers);

        try {
            Map<String, String> body = mapper.readValue(event.getBody(), Map.class);
            String rawDocumentNumber = body.get("documentNumber");

            if (rawDocumentNumber == null || rawDocumentNumber.isBlank()) {
                response.setStatusCode(400);
                response.setBody("{\"error\": \"Documento é obrigatório\"}");
                return response;
            }

            String documentNumber = DocumentValidator.validateAndClean(rawDocumentNumber);

            if (!customerStatusService.isActiveCustomer(documentNumber)) {
                response.setStatusCode(403);
                response.setBody("{\"error\": \"Cliente não encontrado ou inativo\"}");
                return response;
            }

            String token = JwtService.generateToken(documentNumber);

            response.setStatusCode(200);
            response.setBody("{\"token\": \"" + token + "\"}");
            return response;

        } catch (IllegalArgumentException e) {
            response.setStatusCode(400);
            response.setBody("{\"error\": \"" + e.getMessage() + "\"}");
            return response;

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setBody("{\"error\": \"" + e.getMessage() + "\"}");
            return response;
        }
    }
}
