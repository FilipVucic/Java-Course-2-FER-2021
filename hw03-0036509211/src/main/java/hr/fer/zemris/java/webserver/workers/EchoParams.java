package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

public class EchoParams implements IWebWorker {
    @Override
    public void processRequest(RequestContext context) throws Exception {
        context.setMimeType("text/html");
        context.write("<html><body><table><thead><tr><th>Ključ</th><th>Vrijednost</th></tr></thead><tbody>");
        for (String param : context.getParameterNames()) {
            context.write("<tr><td>" + param + "</td><td>" + context.getParameter(param) + "</td></tr>");
        }
        context.write("</tbody></table></body></html>");
    }
}
