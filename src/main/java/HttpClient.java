import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Locale;

public class HttpClient {

    private final int statusCode;
    private final HashMap<String, String> headerFields = new HashMap<>();
    private final String messageBody;

    public HttpClient(String hostname, int port, String requestTarget) throws IOException {
        Socket socket = new Socket(hostname, port);
        String request =
                "GET " + requestTarget + " HTTP/1.1\r\n" +
                        "Host: " + hostname + "\r\n" +
                        "Connection: close \r\n" +
                        "\r\n";
        socket.getOutputStream().write(request.getBytes());

        String statusLine = readLine(socket);
        this.statusCode = Integer.parseInt(statusLine.split(" ")[1]);

        String headerLine;
        while (!(headerLine = readLine(socket)).isBlank()){
            int colonPos = headerLine.indexOf(':');
            String key = headerLine.substring(0, colonPos);
            String value = headerLine.substring(colonPos + 1).trim();
            headerFields.put(key, value);
        }

        this.messageBody = readCharacters(socket, getContentLength());
    }

    private String readCharacters(Socket socket, int contentLength) throws IOException{
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < contentLength; i++) {
            result.append((char) socket.getInputStream().read());
        }

        return result.toString();
    }

    static String readLine(Socket socket) throws IOException {
        StringBuilder result = new StringBuilder();
        int c;
        while ((c = socket.getInputStream().read()) != '\r') {
            result.append((char) c);
        }
        //noinspection ResultOfMethodCallIgnored
        socket.getInputStream().read();
        return result.toString();
    }

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("httpbin.org", 80);

        String request =
                "GET /html HTTP/1.1\r\n" +
                        "Host: httpbin.org\r\n" +
                        "Connection: close \r\n" +
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

    public String getHeader(String headerName) {
        return headerFields.get(headerName);
    }

    public int getContentLength() {
        return Integer.parseInt(getHeader("Content-Length"));
    }

    public String getMessageBody() {
        return messageBody;
    }
}
