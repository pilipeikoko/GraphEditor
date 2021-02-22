package figures;

import javax.swing.*;
import java.awt.*;

public class Circle extends JPanel {
    public final static int radius = 12;
    public Point point;
    private Color color;
    String identifier;

    public Circle(Point point) {
        this.point = point;
        this.color = Color.black;
        this.identifier = new String("");
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(3));
        g2.setColor(color);
        g2.drawOval(point.x-radius, point.y-radius, 2*radius, 2*radius);
        g2.drawString(identifier,point.x+15,point.y+15);
    }

    public void chooseObject(){
        this.color = Color.green;
    }

    public void rejectObject(){
        this.color = Color.black;
    }

    public void setIdentifier(String identifier){
        this.identifier = new String(identifier);
    }


}