package hr.fer.zemris.java.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RequestContext {

    private final OutputStream outputStream;
    private final Map<String, String> parameters;
    private final Map<String, String> persistentParameters;
    private final List<RCCookie> outputCookies;
    public String encoding = "UTF-8";
    public int statusCode = 200;
    public String statusText = "OK";
    public String mimeType = "text/html";
    public Long contentLength = null;
    private IDispatcher dispatcher;
    private Charset charset;
    private Map<String, String> temporaryParameters;
    private boolean headerGenerated = false;


    public RequestContext(OutputStream outputStream, Map<String, String> parameters,
                          Map<String, String> persistentParameters, List<RCCookie> outputCookies) {
        Objects.requireNonNull(outputStream);
        this.outputStream = outputStream;
        this.parameters = Objects.requireNonNullElseGet(parameters, () -> Collections.unmodifiableMap(new HashMap<>()));
        this.persistentParameters = Objects.requireNonNullElseGet(persistentParameters, HashMap::new);
        this.outputCookies = Objects.requireNonNullElseGet(outputCookies, ArrayList::new);
        this.temporaryParameters = new HashMap<>();
    }

    public RequestContext(OutputStream outputStream, Map<String, String> parameters, Map<String, String> persistentParameters,
                          List<RCCookie> outputCookies, Map<String, String> temporaryParameters, IDispatcher dispatcher) {
        this(outputStream, parameters, persistentParameters, outputCookies);
        this.temporaryParameters = Objects.requireNonNullElseGet(temporaryParameters, HashMap::new);
        this.dispatcher = dispatcher;
    }

    public IDispatcher getDispatcher() {
        return dispatcher;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public Set<String> getParameterNames() {
        return Collections.unmodifiableSet(parameters.keySet());
    }

    public String getPersistentParameter(String name) {
        return persistentParameters.get(name);
    }

    public Set<String> getPersistentParameterNames() {
        return Collections.unmodifiableSet(persistentParameters.keySet());
    }

    public void setPersistentParameter(String name, String value) {
        persistentParameters.put(name, value);
    }

    public void removePersistentParameter(String name) {
        persistentParameters.remove(name);
    }

    public String getTemporaryParameter(String name) {
        return temporaryParameters.get(name);
    }

    public Set<String> getTemporaryParameterNames() {
        return Collections.unmodifiableSet(temporaryParameters.keySet());
    }

    public String getSessionID() {
        return "";
    }

    public void setTemporaryParameter(String name, String value) {
        temporaryParameters.put(name, value);
    }

    public void removeTemporaryParameter(String name) {
        temporaryParameters.remove(name);
    }

    public RequestContext write(byte[] data) throws IOException {
        if (!headerGenerated) {
            writeHeader();
        }
        outputStream.write(data);

        return this;
    }

    public RequestContext write(byte[] data, int offset, int len) throws IOException {
        if (!headerGenerated) {
            writeHeader();
        }
        outputStream.write(data, offset, len);

        return this;
    }

    public RequestContext write(String text) throws IOException {
        if (!headerGenerated) {
            writeHeader();
        }
        outputStream.write(text.getBytes(charset));

        return this;
    }

    private void writeHeader() throws IOException {
        charset = Charset.forName(encoding);
        StringBuilder header = new StringBuilder();

        header.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusText).append("\r\n");

        header.append("Content-Type: ").append(mimeType);
        if (mimeType.startsWith("text/")) {
            header.append("; charset=").append(encoding);
        }
        header.append("\r\n");

        if (contentLength != null) {
            header.append("Content-Length: ").append(contentLength).append("\r\n");
        }

        for (RCCookie cookie : outputCookies) {
            header.append("Set-Cookie: ").append(cookie.name).append("=\"").append(cookie.value).append("\"");
            if (cookie.domain != null) {
                header.append("; ").append("Domain=").append(cookie.domain);
            }
            if (cookie.path != null) {
                header.append("; ").append("Path=").append(cookie.path);
            }
            if (cookie.maxAge != null) {
                header.append("; ").append("Max-Age=").append(cookie.maxAge);
            }
            header.append("\r\n");
        }

        header.append("\r\n");

        outputStream.write(header.toString().getBytes(StandardCharsets.ISO_8859_1));
        headerGenerated = true;
    }

    public void setEncoding(String encoding) {
        if (headerGenerated) {
            throw new RuntimeException("Header already generated!");
        }
        this.encoding = encoding;
    }

    public void setStatusCode(int statusCode) {
        if (headerGenerated) {
            throw new RuntimeException("Header already generated!");
        }
        this.statusCode = statusCode;
    }

    public void setStatusText(String statusText) {
        if (headerGenerated) {
            throw new RuntimeException("Header already generated!");
        }
        this.statusText = statusText;
    }

    public void setMimeType(String mimeType) {
        if (headerGenerated) {
            throw new RuntimeException("Header already generated!");
        }
        this.mimeType = mimeType;
    }

    public void setContentLength(Long contentLength) {
        if (headerGenerated) {
            throw new RuntimeException("Header already generated!");
        }
        this.contentLength = contentLength;
    }

    public void addRCCookie(RCCookie cookie) {
        if (headerGenerated) {
            throw new RuntimeException("Header already generated!");
        }
        this.outputCookies.add(cookie);
    }

    public static class RCCookie {
        private final String name;
        private final String value;
        private final String domain;
        private final String path;
        private final Integer maxAge;

        public RCCookie(String name, String value, Integer maxAge, String domain, String path) {
            this.name = name;
            this.value = value;
            this.domain = domain;
            this.path = path;
            this.maxAge = maxAge;
        }
    }
}
