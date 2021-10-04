import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpClientTest {
    @Test
    void shouldGetSuccessfulResponseCode() throws IOException {
        HttpClient client = new HttpClient("httpbin.org", 80, "/html");
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldGetFailureResponseCode() throws IOException {
        HttpClient client = new HttpClient("httpbin.org", 80, "/status/401");
        assertEquals(401, client.getStatusCode());
    }

    @Test
    void shouldReadResponseHeaders() throws IOException {
        HttpClient client = new HttpClient("httpbin.org", 80, "/html");
        assertEquals("text/html; charset=utf-8", client.getHeader("Content-Type"));
    }

    @Test
    void shouldReadMessageBody() throws IOException {
        HttpClient client = new HttpClient("httpbin.org", 80, "/html");
        assertTrue(
                client.getMessageBody().startsWith("<!DOCTYPE html>\n<html"),
                "should be html: " + client.getMessageBody()
                );
    }
}
