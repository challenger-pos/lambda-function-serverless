package auth.document;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DocumentAuthHandlerTest {

  @Test
  public void shouldReturn200AndTokenIfClienteAtivo() throws Exception {
    DatabaseService dbService = mock(DatabaseService.class);
    when(dbService.isActiveCustomer("92601708000")).thenReturn(true);

    DocumentAuthHandler handler = new DocumentAuthHandler(dbService);

    APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
    event.setBody("{\"documentNumber\":\"92601708000\"}");

    APIGatewayProxyResponseEvent result = handler.handleRequest(event, null);

    assertEquals(200, result.getStatusCode().intValue());
    assertTrue(result.getBody().contains("token"));
  }

  @Test
  public void shouldReturn403IfClienteInativo() throws Exception {
    DatabaseService dbService = mock(DatabaseService.class);
    when(dbService.isActiveCustomer("46367735003")).thenReturn(false);

    DocumentAuthHandler handler = new DocumentAuthHandler(dbService);

    APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
    event.setBody("{\"documentNumber\":\"46367735003\"}");

    APIGatewayProxyResponseEvent result = handler.handleRequest(event, null);

    assertEquals(403, result.getStatusCode().intValue());
    assertTrue(result.getBody().contains("Cliente não encontrado"));
  }

  @Test
  public void shouldReturn403IfClienteNaoExistir() throws Exception {
    DatabaseService dbService = mock(DatabaseService.class);
    when(dbService.isActiveCustomer("01782982043")).thenReturn(false);

    DocumentAuthHandler handler = new DocumentAuthHandler(dbService);

    APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
    event.setBody("{\"documentNumber\":\"01782982043\"}");

    APIGatewayProxyResponseEvent result = handler.handleRequest(event, null);

    assertEquals(403, result.getStatusCode().intValue());
  }

  @Test
  public void shouldReturn400IfDocumentIsInvalid() {
    APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
    event.setBody("{\"documentNumber\": \"11111111111\"}");

    DocumentAuthHandler handler = new DocumentAuthHandler();
    APIGatewayProxyResponseEvent result = handler.handleRequest(event, null);

    assertEquals(400, result.getStatusCode().intValue());
    assertTrue(result.getBody().contains("Documento inválido"));
  }
}