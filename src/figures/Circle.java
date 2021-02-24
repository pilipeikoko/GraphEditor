package figures;

import javax.swing.*;
import java.awt.*;

public class Circle extends JPanel {
    public final static int radius = 12;
    public Point point;
    private Color color;
    private String identifier;

    public Circle(Point point) {
        this.point = point;
        this.color = Color.black;
        this.identifier = "";
    }

    public void draw(Graphics g) {
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setStroke(new BasicStroke(3));
        graphics2D.setColor(color);
        graphics2D.drawOval(point.x - radius, point.y - radius, 2 * radius, 2 * radius);
        graphics2D.drawString(identifier, point.x + 15, point.y + 15);
    }

    public void chooseObject() {
        this.color = Color.green;
    }

    public void rejectObject() {
        this.color = Color.black;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }


}