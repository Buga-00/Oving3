import java.io.IOException;
import java.net.Socket;

public class HttpClient {

    private final int statusCode;

    public HttpClient(String hostname, int port, String requestTarget) throws IOException {
        Socket socket = new Socket(hostname, port);
        String request =
                "GET " + requestTarget + " HTTP/1.1\r\n" +
                        "Connection: close \r\n" +
                        "Host: " + hostname + "\r\n" +
                        "\r\n";
        socket.getOutputStream().write(request.getBytes());
        StringBuilder result = new StringBuilder();
        int c;
        while ((c = socket.getInputStream().read()) != -1) {
            result.append((char) c);
        }
        String responseMessage = result.toString();
        this.statusCode = Integer.parseInt(responseMessage.split(" ")[1]);
    }

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("httpbin.org", 80);

        String request =
                "GET /html HTTP/1.1\r\n" +
                        "Connection: close \r\n" +
                        "Host: httpbin.org\r\n" +
                        "\r\n";
        socket.getOutputStream().write(request.getBytes());
        int c;
        while ((c = socket.getInputStream().read()) != -1) {
            System.out.print((char) c);
        }

    }

    public int getStatusCode() {
        return statusCode;
    }
}
