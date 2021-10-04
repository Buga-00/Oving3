import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class HttpMessage {
    public String startLine;
    public HashMap<String, String> headerFields = new HashMap<>();
    public String messageBody;

    public HttpMessage(Socket socket) throws IOException {
     startLine = HttpMessage.readLine(socket);
     readHeaders(socket);
     messageBody = HttpMessage.readCharacters(socket, getContentLength());
    }

    public int getContentLength() {
        return Integer.parseInt(getHeader("Content-Length"));
    }

    public String getHeader(String headerName) {
        return headerFields.get(headerName);
    }


    static String readCharacters(Socket socket, int contentLength) throws IOException{
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < contentLength; i++) {
            result.append((char) socket.getInputStream().read());
        }

        return result.toString();
    }

    private void readHeaders(Socket socket) throws IOException {
        String headerLine;
        while (!(headerLine = HttpMessage.readLine(socket)).isBlank()){
            int colonPos = headerLine.indexOf(':');
            String key = headerLine.substring(0, colonPos);
            String value = headerLine.substring(colonPos + 1).trim();
            headerFields.put(key, value);
        }
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
}
