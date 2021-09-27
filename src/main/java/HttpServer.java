import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    private final ServerSocket serverSocket;

    public HttpServer(int serverPort) throws IOException {
        serverSocket = new ServerSocket(serverPort);

        new Thread(this::handleClients).start();


    }

    private void handleClients(){
        try{
            Socket clientSocket = serverSocket.accept();
            String responseBody = "HTTP/1.1 404 Not found\r\nContent-Length: 0\r\n\r\n";
            clientSocket.getOutputStream().write(responseBody.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);


        Socket clientSocket = serverSocket.accept();

        String requestLine = HttpClient.readLine(clientSocket);

        System.out.println(requestLine);

        String headerLine;
        while (!(headerLine = HttpClient.readLine(clientSocket)).isBlank()){
            System.out.println(headerLine);
        }

        String messageBody = "Hello world";
        String contentType = "text/html";

        String responseBody = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + messageBody.length() + "\r\n" +
                "Content-type: " + contentType + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                messageBody;

        clientSocket.getOutputStream().write(responseBody.getBytes());
    }

}
