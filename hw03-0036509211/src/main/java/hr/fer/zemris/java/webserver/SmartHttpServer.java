package hr.fer.zemris.java.webserver;

import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SmartHttpServer {

    private final String domainName;
    private final int port;
    private final int workerThreads;
    private final String address;
    private final int sessionTimeout;
    private final Map<String, String> mimeTypes = new HashMap<>();
    private final Map<String, IWebWorker> workersMap = new HashMap<>();
    private final Map<String, SessionMapEntry> sessions = new HashMap<>();
    private final Random sessionRandom = new Random();
    private final Path documentRoot;
    private ServerThread serverThread;
    private ExecutorService threadPool;

    public SmartHttpServer(String configFileName) throws Exception {
        InputStream inputStreamServer = new FileInputStream(configFileName);
        Properties serverProps = new Properties();

        serverProps.load(inputStreamServer);

        this.address = serverProps.getProperty("server.address");
        this.domainName = serverProps.getProperty("server.domainName");
        this.port = Integer.parseInt(serverProps.getProperty("server.port"));
        this.workerThreads = Integer.parseInt(serverProps.getProperty("server.workerThreads"));
        this.documentRoot = Paths.get(serverProps.getProperty("server.documentRoot"));

        Properties mimeTypeProps = new Properties();
        InputStream inputStreamMimeType = new FileInputStream(serverProps.getProperty("server.mimeConfig"));
        mimeTypeProps.load(inputStreamMimeType);

        for (String key : mimeTypeProps.stringPropertyNames()) {
            mimeTypes.put(key, mimeTypeProps.getProperty(key));
        }

        this.sessionTimeout = Integer.parseInt(serverProps.getProperty("session.timeout"));
        Properties workersProps = new Properties();
        InputStream inputStreamWorkers = new FileInputStream(serverProps.getProperty("server.workers"));
        workersProps.load(inputStreamWorkers);

        for (String key : workersProps.stringPropertyNames()) {
            if (workersMap.containsKey(key)) {
                throw new RuntimeException("Workers must be unique!");
            }

            Class<?> referenceToClass = null;
            try {
                referenceToClass = this.getClass().getClassLoader().loadClass(workersProps.getProperty(key));
            } catch (ClassNotFoundException e) {
                System.out.println("No class from workers: " + workersProps.getProperty(key));
                System.exit(1);
            }
            Object newObject = referenceToClass.newInstance();
            IWebWorker iww = (IWebWorker) newObject;

            workersMap.put(key, iww);
        }

    }

    private static String extractExtension(String fileName) {
        int p = fileName.lastIndexOf(".");
        if (p < 1) return "";
        return fileName.substring(p + 1).toLowerCase();
    }

    private static Optional<byte[]> readRequest(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int state = 0;
        l:
        while (true) {
            int b = is.read();
            if (b == -1) {
                if (bos.size() != 0) {
                    throw new IOException("Incomplete header received.");
                }
                return Optional.empty();
            }
            if (b != 13) {
                bos.write(b);
            }
            switch (state) {
                case 0:
                    if (b == 13) {
                        state = 1;
                    } else if (b == 10) state = 4;
                    break;
                case 1:
                    if (b == 10) {
                        state = 2;
                    } else state = 0;
                    break;
                case 2:
                    if (b == 13) {
                        state = 3;
                    } else state = 0;
                    break;
                case 3:
                case 4:
                    if (b == 10) {
                        break l;
                    } else state = 0;
                    break;
            }
        }
        return Optional.of(bos.toByteArray());
    }

    private static List<String> extractHeaders(String requestHeader) {
        List<String> headers = new ArrayList<String>();
        String currentLine = null;
        for (String s : requestHeader.split("\n")) {
            if (s.isEmpty()) break;
            char c = s.charAt(0);
            if (c == 9 || c == 32) {
                currentLine += s;
            } else {
                if (currentLine != null) {
                    headers.add(currentLine);
                }
                currentLine = s;
            }
        }
        if (!currentLine.isEmpty()) {
            headers.add(currentLine);
        }
        return headers;
    }

    private static void sendResponseWithData(OutputStream cos, int statusCode, String statusText, String contentType, byte[] data) throws IOException {

        cos.write(
                ("HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                        "Server: simple java server\r\n" +
                        "Content-Type: " + contentType + "\r\n" +
                        "Content-Length: " + data.length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n").getBytes(StandardCharsets.US_ASCII)
        );
        cos.write(data);
        cos.flush();
    }

    private static void sendEmptyResponse(OutputStream cos, int statusCode, String statusText) throws IOException {
        sendResponseWithData(cos, statusCode, statusText, "text/plain;charset=UTF-8", new byte[0]);
    }

    public static void main(String[] args) throws Exception {
        SmartHttpServer server = new SmartHttpServer(args[0]);
        server.start();
    }

    protected synchronized void start() {
        if (serverThread == null) {
            serverThread = new ServerThread();
            serverThread.start();

            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            exec.scheduleAtFixedRate(() -> {
                for (Map.Entry<String, SessionMapEntry> entry : sessions.entrySet()) {
                    if (System.currentTimeMillis() / 1000 > entry.getValue().validUntil) {
                        sessions.remove(entry.getKey());
                    }
                }
            }, 0, 5, TimeUnit.MINUTES);
        }

        threadPool = Executors.newFixedThreadPool(workerThreads);
    }

    protected synchronized void stop() {
        serverThread.running.set(false);
        threadPool.shutdown();
    }

    private static class SessionMapEntry {
        private final String sid;
        private final String host;
        private final Map<String, String> map;
        private long validUntil;

        public SessionMapEntry(String sid, String host, long validUntil, ConcurrentHashMap<String, String> map) {
            this.sid = sid;
            this.host = host;
            this.validUntil = validUntil;
            this.map = map;
        }
    }

    protected class ServerThread extends Thread {
        private final AtomicBoolean running = new AtomicBoolean(false);

        @Override
        public void run() {
            running.set(true);
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                while (running.get()) {
                    serverSocket.setSoTimeout(sessionTimeout * 1000);
                    Socket client;
                    try {
                        client = serverSocket.accept();
                    } catch (SocketTimeoutException e) {
                        break;
                    }
                    ClientWorker cw = new ClientWorker(client);
                    threadPool.submit(cw);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientWorker implements Runnable, IDispatcher {
        private final Socket csocket;
        private final Map<String, String> tempParams = new HashMap<>();
        private final List<RequestContext.RCCookie> outputCookies = new ArrayList<>();
        private RequestContext context = null;
        private InputStream istream;
        private OutputStream ostream;
        private String version;
        private String method;
        private String host;
        private Map<String, String> params = new HashMap<>();
        private Map<String, String> permPrams = new HashMap<>();
        private String SID;

        public ClientWorker(Socket csocket) {
            this.csocket = csocket;
        }

        @Override
        public void run() {
            try {
                istream = new BufferedInputStream(csocket.getInputStream());
                ostream = new BufferedOutputStream(csocket.getOutputStream());

                Optional<byte[]> request = readRequest(istream);

                if (request.isEmpty()) {
                    return;
                }
                String requestStr = new String(request.get(), StandardCharsets.US_ASCII);

                List<String> headers = extractHeaders(requestStr);
                String[] firstLine = headers.isEmpty() ? null : headers.get(0).split(" ");
                if (firstLine == null || firstLine.length != 3) {
                    sendEmptyResponse(ostream, 400, "Bad request");
                    ostream.flush();
                    csocket.close();
                    return;
                }

                method = firstLine[0].toUpperCase();
                if (!method.equals("GET")) {
                    sendEmptyResponse(ostream, 400, "Method Not Allowed");
                    ostream.flush();
                    csocket.close();
                    return;
                }

                version = firstLine[2].toUpperCase();
                if (!version.equals("HTTP/1.0") && !version.equals("HTTP/1.1")) {
                    sendEmptyResponse(ostream, 400, "Version Not Supported");
                    ostream.flush();
                    csocket.close();
                    return;
                }

                host = domainName;
                for (String header : headers) {
                    if (header.startsWith("Host:")) {
                        host = header.split(" ")[1].split(":")[0];
                    }
                }

                checkSession(headers);

                String[] pathAndParamString = firstLine[1].split("\\?");
                if (pathAndParamString.length == 2) {
                    params = parseParameters(pathAndParamString[1]);
                }

                try {
                    internalDispatchRequest(pathAndParamString[0], true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ostream.flush();
                csocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private synchronized void checkSession(List<String> headers) {
            String sidCandidate = "";
            for (String line : headers) {
                if (!line.startsWith("Cookie: ")) {
                    continue;
                }

                String[] cookies = line.substring(8).split(";");
                for (String cookie : cookies) {
                    if (cookie.startsWith("sid=")) {
                        sidCandidate = cookie.substring(5, cookie.length() - 1);
                    }
                }
            }

            if (!sidCandidate.isEmpty() && sessions.containsKey(sidCandidate)) {
                SessionMapEntry session = sessions.get(sidCandidate);
                if (session.host.equals(host)) {
                    if (session.validUntil < System.currentTimeMillis() / 1000) {
                        sessions.remove(sidCandidate);
                    } else {
                        permPrams = session.map;
                        session.validUntil = System.currentTimeMillis() / 1000 + sessionTimeout;
                        return;
                    }
                }
            }

            StringBuilder newSid = new StringBuilder();
            for (int i = 0; i < 20; i++) {
                newSid.append((char) ('A' + sessionRandom.nextInt(26)));
            }
            SID = newSid.toString();
            SessionMapEntry newSession = new SessionMapEntry(SID, host,
                    System.currentTimeMillis() / 1000 + sessionTimeout, new ConcurrentHashMap<>());
            permPrams = newSession.map;
            sessions.put(SID, newSession);
            outputCookies.add(new RequestContext.RCCookie("sid", SID, sessionTimeout, host, "/"));
        }

        private Map<String, String> parseParameters(String s) {
            if (s.isEmpty()) {
                return null;
            }

            Map<String, String> parametersMap = new HashMap<>();
            String[] parameters = s.split("&");

            for (String param : parameters) {
                String[] keyValue = param.split("=");
                parametersMap.put(keyValue[0], keyValue[1]);
            }

            return parametersMap;
        }

        @Override
        public void dispatchRequest(String urlPath) throws Exception {
            internalDispatchRequest(urlPath, false);
        }

        private void internalDispatchRequest(String urlPath, boolean directCall) throws Exception {
            if (context == null) {
                context = new RequestContext(ostream, params, permPrams, outputCookies, tempParams, this);
            }

            if (urlPath.startsWith("/private")) {
                if (directCall) {
                    sendEmptyResponse(ostream, 404, "File Not Found");
                    return;
                }
            }

            if (urlPath.startsWith("/ext/")) {
                Class<?> referenceToClass;
                try {
                    String pathToClass = urlPath.split("/")[2];
                    referenceToClass = this.getClass().getClassLoader().loadClass("hr.fer.zemris.java.webserver.workers." + pathToClass);
                } catch (ClassNotFoundException e) {
                    sendEmptyResponse(ostream, 404, "File Not Found");
                    return;
                }
                Object newObject = referenceToClass.newInstance();
                IWebWorker iww = (IWebWorker) newObject;
                iww.processRequest(context);
                return;
            }

            if (workersMap.containsKey(urlPath)) {
                workersMap.get(urlPath).processRequest(context);
                return;
            }

            Path requestedPath = documentRoot.resolve(urlPath.substring(1)).toAbsolutePath().normalize();

            if (!requestedPath.toString().startsWith(documentRoot.toString())) {
                sendEmptyResponse(ostream, 403, "Forbidden");
                return;
            }

            if (!Files.exists(requestedPath) || !Files.isRegularFile(requestedPath) || !Files.isReadable(requestedPath)) {
                sendEmptyResponse(ostream, 404, "File Not Found");
                return;
            }

            String extension = extractExtension(requestedPath.getFileName().toString());
            String mimeType = mimeTypes.get(extension);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            if (extension.equals("smscr")) {
                String documentBody = null;
                try {
                    documentBody = Files.readString(requestedPath);
                } catch (IOException e) {
                    System.out.println("Unable to read file!");
                    System.exit(-1);
                }

                context.setStatusCode(200);
                new SmartScriptEngine(
                        new SmartScriptParser(documentBody).getDocumentNode(),
                        context
                ).execute();
            } else {
                context.setMimeType(mimeType);
                context.setStatusCode(200);
                context.setContentLength(Files.size(requestedPath));

                byte[] okteti = Files.readAllBytes(requestedPath);
                context.write(okteti);
            }
        }
    }
}
