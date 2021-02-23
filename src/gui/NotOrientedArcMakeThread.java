package gui;

import figures.NonOrientedArrow;
import graph.Arc;

import java.awt.*;

public class NotOrientedArcMakeThread extends Thread{

    protected DrawableJPanel jPanel;
    protected Point sourcePoint;

    protected boolean isActive;

    public NotOrientedArcMakeThread(DrawableJPanel drawableJPanel, Point point){
        this.jPanel = drawableJPanel;
        this.sourcePoint = point;
        isActive = true;
    }

    @Override
    public void run() {
        int x = jPanel.getMousePosition().x;
        int y = jPanel.getMousePosition().y;
        Point targetPoint = new Point(x, y);
        NonOrientedArrow arrow = new NonOrientedArrow(sourcePoint, targetPoint);
        jPanel.add(arrow);
        while (isActive) {
            try {
                x = jPanel.getMousePosition().x;
                y = jPanel.getMousePosition().y;
                targetPoint.x = x;
                targetPoint.y = y;
                jPanel.revalidate();
                jPanel.repaint();
            }catch (NullPointerException e){
                jPanel.remove(arrow);
                interrupt();
                return;
            }
        }
        Point target = jPanel.findTarget();
        if (target == null) {
            jPanel.remove(arrow);
            interrupt();
        }
        jPanel.remove(jPanel.getComponent(jPanel.getComponentCount()-1));
        arrow = new NonOrientedArrow(sourcePoint,target);
        jPanel.add(arrow);
        arrow.changeTarget(target);
        Arc quadruplet = new Arc(sourcePoint, target, false);
        jPanel.graph.addArc(quadruplet);
        interrupt();
    }

    public void disable(){
        isActive = false;
    }
}
