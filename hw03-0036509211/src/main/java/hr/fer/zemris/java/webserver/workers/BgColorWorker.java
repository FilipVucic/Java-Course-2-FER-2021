package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

import java.io.IOException;

public class BgColorWorker implements IWebWorker {

    @Override
    public void processRequest(RequestContext context) throws Exception {
        String param = context.getParameter("bgcolor");
        try {
            context.write("<html><head></head><body>");
            context.write("<a href=\"/index2.html\">Povratak na glavnu stranicu</a>");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (param != null && param.length() == 6) {
            context.setPersistentParameter("bgcolor", param);
            try {
                context.write("<p>Boja je promijenjena.</p>");
                context.write("</body></html>");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                context.write("<p>Boja nije promijenjena.</p>");
                context.write("</body></html>");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
