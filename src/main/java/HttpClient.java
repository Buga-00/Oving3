import java.io.IOException;
import java.net.Socket;

public class HttpClient {

    private final int statusCode;


    private HttpMessage httpMessage;

    public HttpClient(String hostname, int port, String requestTarget) throws IOException {
        Socket socket = new Socket(hostname, port);
        String request =
                "GET " + requestTarget + " HTTP/1.1\r\n" +
                        "Host: " + hostname + "\r\n" +
                        "Connection: close \r\n" +
                        "\r\n";
        socket.getOutputStream().write(request.getBytes());

        httpMessage = new HttpMessage(socket);
        String[] statusLine = httpMessage.startLine.split(" ");
        this.statusCode = Integer.parseInt(statusLine[1]);


    }


    public int getStatusCode() {
        return statusCode;
    }

    public String getHeader(String headerName) {
        return httpMessage.headerFields.get(headerName);
    }

    public String getMessageBody() {
        return httpMessage.messageBody;
    }

    public static void main(String[] args) throws IOException {
        HttpClient client = new HttpClient("httpbin.org", 80, "/html");
        System.out.println(client.getMessageBody());

    }
}


