package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

public class SumWorker implements IWebWorker {
    private static final int defaultA = 1;
    private static final int defaultB = 2;

    @Override
    public void processRequest(RequestContext context) throws Exception {
        int a;
        try {
            a = Integer.parseInt(context.getParameter("a"));
        } catch (NumberFormatException e) {
            a = defaultA;
        }

        int b;
        try {
            b = Integer.parseInt(context.getParameter("b"));
        } catch (NumberFormatException e) {
            b = defaultB;
        }
        int sum = a + b;
        context.setTemporaryParameter("zbroj", String.valueOf(sum));
        context.setTemporaryParameter("varA", String.valueOf(a));
        context.setTemporaryParameter("varB", String.valueOf(b));
        if (sum % 2 == 0) {
            context.setTemporaryParameter("imgName", "images/fruits.png");
        } else {
            context.setTemporaryParameter("imgName", "images/oranges.jpg");
        }
        context.getDispatcher().dispatchRequest("/private/pages/calc.smscr");
    }
}
