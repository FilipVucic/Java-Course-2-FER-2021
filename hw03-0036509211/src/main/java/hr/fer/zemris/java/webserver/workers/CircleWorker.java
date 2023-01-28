package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CircleWorker implements IWebWorker {
    @Override
    public void processRequest(RequestContext context) throws Exception {
        BufferedImage bim = new BufferedImage(200, 200, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = bim.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, bim.getWidth(), bim.getHeight());
        g2d.setColor(Color.BLUE);
        g2d.fillOval(0, 0, bim.getWidth(), bim.getHeight());
        context.setMimeType("image/png");

        g2d.dispose();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bim, "png", bos);
            context.write(bos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
