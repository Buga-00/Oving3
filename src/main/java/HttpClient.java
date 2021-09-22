import java.io.IOException;
import java.net.Socket;

public class HttpClient {

    public HttpClient(String s, int i, String s1) throws IOException {

    }

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("httpbin.org", 80);

        String request =
                "GET /html HTTP/1.1\r\n" +
                        "Host: httpbin.org\r\n" +
                        "\r\n";
        socket.getOutputStream().write(request.getBytes());
        int c;
        while ((c = socket.getInputStream().read()) != -1) {
            System.out.print((char) c);
        }

    }

    public int getStatusCode() {
        return 200;
    }
}
