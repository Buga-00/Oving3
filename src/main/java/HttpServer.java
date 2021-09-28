import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;

public class HttpServer {

    private final ServerSocket serverSocket;
    private Path rootDirectory;

    public HttpServer(int serverPort) throws IOException {
        serverSocket = new ServerSocket(serverPort);

        new Thread(this::handleClients).start();

    }

    private void handleClients(){
        try{
            Socket clientSocket = serverSocket.accept();

            String[] requestLine = HttpClient.readLine(clientSocket).split(" ");
            String requestTarget = requestLine[1];

            if (requestTarget.equals("/hello")){
                String responseText = "<p>Hello world</p>";

                String responseBody = "HTTP/1.1 200 OK\r\n" +
                        "Content-Length: " +  responseText.length() +  "\r\n" +
                        "Content-Type: text/html\r\n" +
                        "\r\n" +
                        responseText;
                clientSocket.getOutputStream().write(responseBody.getBytes());
            }else{
                String responseText = "File not found: " + requestTarget;

                String responseBody = "HTTP/1.1 404 Not found\r\n" +
                        "Content-Length: " +  responseText.length() +  "\r\n" +
                        "\r\n" +
                        responseText;
                clientSocket.getOutputStream().write(responseBody.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new HttpServer(8080);
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void setRoot(Path rootDirectory) {
        this.rootDirectory = rootDirectory;
    }
}
