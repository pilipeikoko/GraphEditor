package gui.figuresJComponents;

import figures.Circle;
import figures.Point;

import javax.swing.*;
import java.awt.*;

public class CircleJComponent extends JComponent {
    public Circle circle;
    Color color;

    public CircleJComponent(Point point) {
        this.circle = new Circle(point);
        this.color = Color.black;
    }

    public void draw(Graphics g) {
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setStroke(new BasicStroke(3));
        graphics2D.setColor(color);
        graphics2D.drawOval(circle.point.x - Circle.radius, circle.point.y - Circle.radius, 2 * Circle.radius, 2 * Circle.radius);
        graphics2D.drawString(circle.identifier, circle.point.x + 15, circle.point.y + 15);
    }

    public void chooseObject() {
        this.color = Color.green;
    }

    public void rejectObject() {
        this.color = Color.black;
    }

    public void setIdentifier(String identifier) {
        this.circle.identifier = identifier;
    }


}