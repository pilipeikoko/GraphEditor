package gui;

import figures.NonOrientedArrow;
import figures.OrientedArrow;
import graph.Arc;

import javax.swing.*;
import java.awt.*;

public class OrientedArcMakeThread extends NotOrientedArcMakeThread{

    OrientedArcMakeThread(DrawableJPanel jpanel, Point point){
        super(jpanel,point);
    }

    @Override
    public void run() {
        int x = jPanel.getMousePosition().x;
        int y = jPanel.getMousePosition().y;
        Point targetPoint = new Point(x, y);
        OrientedArrow arrow = new OrientedArrow(sourcePoint, targetPoint);
        jPanel.add(arrow);
        while (isActive) {
            x = jPanel.getMousePosition().x ;
            y = jPanel.getMousePosition().y ;
            targetPoint.x = x;
            targetPoint.y = y;
            jPanel.revalidate();
            jPanel.repaint();
        }
        Point target = jPanel.findTargetAtComponents();
        if (target == null) {
            jPanel.remove(arrow);
            interrupt();
            return;
        }
        jPanel.remove(jPanel.getComponent(jPanel.getComponentCount()-1));
        arrow = new OrientedArrow(sourcePoint,target);
        jPanel.add(arrow);
        arrow.changeTarget(target);
        Arc quadruplet = new Arc(sourcePoint, target, true);
        jPanel.graph.addArc(quadruplet);
        interrupt();
    }

}
