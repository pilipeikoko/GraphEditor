package gui;

import java.awt.*;

public class CircleMoveThread extends Thread {

    private final DrawableJPanel jPanel;
    private final Point point;

    boolean isActive;

    public CircleMoveThread(DrawableJPanel jPanel, Point point) {
        this.jPanel = jPanel;
        this.point = point;
        isActive = true;
    }

    @Override
    public void run() {
        while (isActive) {
            point.move(jPanel.getMousePosition().x, jPanel.getMousePosition().y);
            jPanel.revalidate();
            jPanel.repaint();
        }
    }

    public void disable() {
        isActive = false;
        interrupt();
    }
}
