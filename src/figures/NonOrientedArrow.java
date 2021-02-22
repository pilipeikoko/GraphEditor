package figures;

import com.sun.jdi.PrimitiveValue;

import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

public class NonOrientedArrow extends JPanel {
    public Line2D line;
    public Point sourcePoint;
    public Point targetPoint;
    protected Color color;
    protected static int radius = Circle.radius;
    int weight;
    protected boolean isFinish = false;

    private Point coordinatesToPrint;

    public NonOrientedArrow() {

    }

    public NonOrientedArrow(Point sourcePoint, Point targetPoint) {
        this.sourcePoint = sourcePoint;
        this.targetPoint = targetPoint;
        this.color = Color.black;
        this.weight = 1;
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));
        g2.setColor(color);

        double k = (double) (targetPoint.y - sourcePoint.y) / (targetPoint.x - sourcePoint.x);

        double a = Math.sqrt(Math.pow(radius, 2) / (Math.pow(k, 2) + 1));
        double b = a * k;
        int sourceDy, sourceDx, targetDy, targetDx;

        if (targetPoint.x > sourcePoint.x) {
            sourceDy = (int) (sourcePoint.y + b);
            sourceDx = (int) (sourcePoint.x + a);
            targetDy = (int) (targetPoint.y - b);
            targetDx = (int) (targetPoint.x - a);
        } else if (targetPoint.x < sourcePoint.x) {
            sourceDy = (int) (sourcePoint.y - b);
            sourceDx = (int) (sourcePoint.x - a);
            targetDy = (int) (targetPoint.y + b);
            targetDx = (int) (targetPoint.x + a);
        } else {
            sourceDy = targetPoint.y > sourcePoint.y ? sourcePoint.y + radius : sourcePoint.y - radius;
            sourceDx = sourcePoint.x;
            targetDy = sourcePoint.y > targetPoint.y ? targetPoint.y + radius : targetPoint.y - radius;
            targetDx = targetPoint.x;
        }
        if(isFinish)
            line = new Line2D.Double(sourceDx, sourceDy,targetDx,targetDy);
        else
            line = new Line2D.Double(sourceDx, sourceDy, targetPoint.x, targetPoint.y);

        g2.draw(line);
        g2.setColor(Color.red);

        int diffirenceY = sourcePoint.y > targetPoint.y ? 10 : -10;
        int diffirenceX = sourcePoint.x > targetPoint.x ? -10 : 10;

        int midX = (targetPoint.x + sourcePoint.x) / 2 + diffirenceX;
        int midY = (targetPoint.y + sourcePoint.y) / 2 + diffirenceY;

        if (weight != 1)
            g2.drawString(String.valueOf(weight), midX, midY);

    }

    public void changeTarget(Point point) {
        isFinish=true;
        this.targetPoint = point;
    }

    public void chooseObject() {
        this.color = Color.green;
    }

    public void rejectObject() {
        this.color = Color.black;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
