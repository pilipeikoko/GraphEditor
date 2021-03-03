package gui;

import figures.Point;
import gui.figuresJComponents.OrientedArrowJComponent;
import graph.Arc;

public class OrientedArcMakeThread extends NotOrientedArcMakeThread {

    OrientedArcMakeThread(DrawableJPanel panel, Point point) {
        super(panel, point);

    }

    @Override
    public void run() {
        int x = jPanel.getMousePosition().x;
        int y = jPanel.getMousePosition().y;

        Point targetPoint = new Point(x, y);
        OrientedArrowJComponent arrow = new OrientedArrowJComponent(sourcePoint, targetPoint);

        jPanel.add(arrow);

        while (isActive) {
            try {
                x = jPanel.getMousePosition().x;
                y = jPanel.getMousePosition().y;

                targetPoint.x = x;
                targetPoint.y = y;

                jPanel.revalidate();
                jPanel.repaint();
            } catch (NullPointerException e) {
                jPanel.remove(arrow);
                interrupt();
                return;
            }
        }
        Point target = jPanel.findTargetAtComponents();
        if (target == null) {
            jPanel.remove(arrow);
            interrupt();
            return;
        }
        jPanel.remove(jPanel.getComponent(jPanel.getComponentCount() - 1));
        arrow = new OrientedArrowJComponent(sourcePoint, target);
        jPanel.add(arrow);

        arrow.changeTarget(target);

        Arc quadruplet = new Arc(sourcePoint, target, true);
        jPanel.graph.addArc(quadruplet);
        interrupt();
    }


}
