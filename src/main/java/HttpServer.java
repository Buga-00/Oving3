import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServer {

    private final ServerSocket serverSocket;
    private Path rootDirectory;
    private List<String> categories = new ArrayList<>();
    private List<Product> item = new ArrayList<>();

    public HttpServer(int serverPort) throws IOException {
        serverSocket = new ServerSocket(serverPort);

        new Thread(this::handleClients).start();

    }

    private void handleClients(){
        try{
            while (true){
                handleClient();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient() throws IOException {
        Socket clientSocket = serverSocket.accept();

        HttpMessage httpMessage = new HttpMessage(clientSocket);
        String[] requestLine = httpMessage.startLine.split(" ");
        String requestTarget = requestLine[1];

        int questionPos = requestTarget.indexOf('?');
        String fileTarget;
        String query = null;
        if (questionPos != -1) {
            fileTarget = requestTarget.substring(0, questionPos);
            query = requestTarget.substring(questionPos + 1);
        } else {
            fileTarget = requestTarget;
        }

        if (fileTarget.equals("/hello")) {
            String yourName = "world";
            if (query != null) {
                Map<String, String> queryMap = parseRequestParameters(query);

                yourName = queryMap.get("productName") + ", " + queryMap.get("firstName");
            }
            String responseText = "<p>Hello " + yourName + "</p>";

            writeOkResponse(clientSocket, responseText, "text/html");
        }else if (fileTarget.equals("/api/newProduct")){
            Map<String, String> queryMap = parseRequestParameters(httpMessage.messageBody);
            Product product = new Product();
            product.setLastName(queryMap.get("productName"));
            item.add(product);

            writeOkResponse(clientSocket, "it is done", "text/html");
        }else if (fileTarget.equals("/api/categoryOptions")){
            String responseText = "";

            int value = 1;
            for (String category : categories) {
                responseText += "<option value=" + (value++) + ">" + category + "</option>";
            }


            writeOkResponse(clientSocket, responseText, "text/html");
        }else{
            if ( rootDirectory != null && Files.exists(rootDirectory.resolve(fileTarget.substring(1)))){
                String responseText = Files.readString(rootDirectory.resolve(fileTarget.substring(1)));

                String contentType = "text/plain";
                if (requestTarget.endsWith(".html")){
                    contentType = "text/html";
                }
                writeOkResponse(clientSocket, responseText, contentType);
                return;
            }


            String responseText = "File not found: " + requestTarget;

            String responseBody = "HTTP/1.1 404 Not found\r\n" +
                    "Content-Length: " +  responseText.length() +  "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n" +
                    responseText;
            clientSocket.getOutputStream().write(responseBody.getBytes());
        }
    }

    private Map<String, String> parseRequestParameters(String query) {
        Map<String, String> queryMap = new HashMap<>();
        for (String queryParameter : query.split("&")) {
            int equalsPos = queryParameter.indexOf('=');
            String parameterName = queryParameter.substring(0, equalsPos);
            String parameterValue = queryParameter.substring(equalsPos+1);
            queryMap.put(parameterName, parameterValue);
        }
        return queryMap;
    }

    private void writeOkResponse(Socket clientSocket, String responseText, String contentType) throws IOException {
        String responseBody = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + responseText.length() + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                responseText;
        clientSocket.getOutputStream().write(responseBody.getBytes());
    }

    public static void main(String[] args) throws IOException {
       HttpServer httpServer = new HttpServer(8080);
       httpServer.setCategories(List.of("Food", "Drinks", "Candy"));
       httpServer.setRoot(Paths.get("."));
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void setRoot(Path rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<Product> getItem() {
        return item;
    }
}
