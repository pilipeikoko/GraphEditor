package gui;

import figures.Circle;
import figures.NonOrientedArrow;
import graph.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;

import static java.lang.Thread.sleep;

public class DrawableJPanel extends JPanel implements MouseListener {

    final static int radius = Circle.radius;

    private final MainGUI gui;

    final Graph graph = new Graph();

    private ActionType actionType;

    private CircleMoveThread circleMoveThread;
    private NotOrientedArcMakeThread notOrientedArcMakeThread;
    private OrientedArcMakeThread orientedArcMakeThread;
    ComponentMenuBarThread componentMenuBarThread;

    Component chosenComponent;
    private boolean isComponentChosen = false;

    public DrawableJPanel(MainGUI gui) {

        this.gui = gui;
        this.setLayout(null);
        this.addMouseListener(this);

        addKeyActionMap();

        actionType = gui.actionType;

        setBorder(BorderFactory.createLineBorder(Color.gray));
    }

    private void addKeyActionMap() {

        Object delete = new Object();
        this.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), delete);
        this.getActionMap().put(delete, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                killThreads();
                killMenuThread();
                deleteChosenComponent();
            }
        });

        Object click1 = new Object();
        this.getInputMap().put(KeyStroke.getKeyStroke("1"), click1);
        this.getActionMap().put(click1, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                killThreads();
                gui.actionType = ActionType.MAKEVERTEX;
            }
        });

        Object click2 = new Object();
        this.getInputMap().put(KeyStroke.getKeyStroke("2"), click2);
        this.getActionMap().put(click2, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                killThreads();
                gui.actionType = ActionType.MAKEORIENTEDARC;
            }
        });


        Object click3 = new Object();
        this.getInputMap().put(KeyStroke.getKeyStroke("3"), click3);
        this.getActionMap().put(click3, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                killThreads();
                gui.actionType = ActionType.MAKENOTORIENTEDARC;
            }
        });

        Object clickI = new Object();
        this.getInputMap().put(KeyStroke.getKeyStroke("I"), clickI);
        this.getActionMap().put(clickI, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                killThreads();
                killMenuThread();
                changeChosenComponent();
                showMenuForChosenComponent();
                revalidate();
                repaint();
            }
        });

        Object clickESC = new Object();
        this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), clickESC);
        this.getActionMap().put(clickESC, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                killThreads();
                killMenuThread();
            }
        });
    }


    private void moveVertex() {
        Point point = findTarget();
        if (point != null) {
            circleMoveThread = new CircleMoveThread(this, point);
            circleMoveThread.start();
        }
    }

    private void changeChosenComponent() {
        int indexOfTarget = findIndexOfTarget();
        rejectComponent();
        if (indexOfTarget != -1) {
            chooseComponent(getComponent(indexOfTarget));
        } else {
            if (componentMenuBarThread != null)
                componentMenuBarThread.disable();
            killThreads();
        }
    }

    void deleteChosenComponent() {
        if (isComponentChosen) {
            int index = findIndexOfChosenTarget();
            this.remove(index);

            rejectComponent();

            isComponentChosen = false;

            if (chosenComponent instanceof Circle) {
                Point point = ((Circle) chosenComponent).point;
                graph.removeVertex(point);
                removeIncidentalArcs(point);
            } else if (chosenComponent instanceof NonOrientedArrow) {
                Point sourcePoint = ((NonOrientedArrow) chosenComponent).sourcePoint;
                Point targetPoint = ((NonOrientedArrow) chosenComponent).targetPoint;
                graph.removeArc(sourcePoint, targetPoint);
            }
        }
    }

    private void removeIncidentalArcs(Point point) {
        for (int i = 0; i < this.getComponentCount(); ++i) {
            Component component = this.getComponent(i);
            if (component instanceof NonOrientedArrow
                    && (((NonOrientedArrow) component).sourcePoint.equals(point)
                    || ((NonOrientedArrow) component).targetPoint.equals(point))) {
                this.remove(i--);
            }
        }
    }

    private int findIndexOfChosenTarget() {
        Component chosenComponent = this.chosenComponent;
        for (int i = 0; i < this.getComponentCount(); i++) {
            Component component = this.getComponent(i);
            if (component.equals(chosenComponent)) {
                return i;
            }
        }

        return -1;
    }

    Point findTarget() {
        Point currentPoint = new Point(getMousePosition().x, getMousePosition().y);
        for (var vertex : graph.setOfVertexes) {
            Point point = vertex.point;
            if (Math.abs(point.x - currentPoint.x) <= radius && Math.abs(point.y - currentPoint.y) <= radius) {
                return point;
            }
        }
        return null;
    }

    Point findTargetAtComponents() {
        Point currentPoint = new Point(getMousePosition().x, getMousePosition().y);
        for (int i = 0; i < this.getComponentCount(); ++i) {
            Component component = this.getComponent(i);
            if (component instanceof Circle) {
                Point point = ((Circle) component).point;
                if (Math.abs(point.x - currentPoint.x) <= radius && Math.abs(point.y - currentPoint.y) <= radius) {
                    return point;
                }
            }
        }
        return null;
    }

    private int findIndexOfTarget() {
        Point currentPoint = new Point(getMousePosition().x, getMousePosition().y);
        for (int i = 0; i < getComponentCount(); i++) {
            Component component = getComponent(i);
            if (component instanceof Circle) {
                Point point = ((Circle) component).point;
                if (Math.abs(point.x - currentPoint.x) <= radius
                        && Math.abs(point.y - currentPoint.y) <= radius) {
                    return i;
                }
            } else if (component instanceof NonOrientedArrow) {
                Line2D line = ((NonOrientedArrow) component).line;
                if (line.ptSegDist(currentPoint) < 10) {
                    return i;
                }
            }
        }

        return -1;
    }

    void createVertex(int x, int y, String identifier) {
        this.grabFocus();

        Point point = new Point(x, y);
        Circle circle = new Circle(point);
        circle.setIdentifier(identifier);

        this.add(circle);
        graph.addVertex(point, identifier);

        rejectComponent();
        chooseComponent(circle);
    }

    private void createVertex() {
        int x = getMousePosition().x;
        int y = getMousePosition().y;

        this.grabFocus();

        Point point = new Point(x, y);
        Circle circle = new Circle(point);

        this.add(circle);
        graph.addVertex(point);

        rejectComponent();
        chooseComponent(circle);
    }


    void rejectComponent() {
        isComponentChosen = false;
        if (chosenComponent instanceof Circle) {
            Circle circle = (Circle) chosenComponent;
            circle.rejectObject();
        } else if (chosenComponent instanceof NonOrientedArrow) {
            NonOrientedArrow nonOrientedArrow = (NonOrientedArrow) chosenComponent;
            nonOrientedArrow.rejectObject();
        }
        revalidate();
        repaint();
    }

    void chooseComponent(Component component) {
        isComponentChosen = true;
        if (component instanceof Circle) {
            Circle circle = (Circle) component;
            circle.chooseObject();
            chosenComponent = circle;
        } else if (component instanceof NonOrientedArrow) {
            NonOrientedArrow nonOrientedArrow = (NonOrientedArrow) component;
            nonOrientedArrow.chooseObject();
            chosenComponent = nonOrientedArrow;
        }
        revalidate();
        repaint();

    }

    void killThreads() {
        if (circleMoveThread != null && circleMoveThread.isAlive()) {
            circleMoveThread.disable();
        } else if (notOrientedArcMakeThread != null && notOrientedArcMakeThread.isAlive()) {
            notOrientedArcMakeThread.disable();
            actionType = ActionType.MAKEVERTEX;
        } else if (orientedArcMakeThread != null && orientedArcMakeThread.isAlive()) {
            orientedArcMakeThread.disable();
            actionType = ActionType.MAKEVERTEX;
        }
    }

    private void showMenuForChosenComponent() {
        if (isComponentChosen) {
            componentMenuBarThread = new ComponentMenuBarThread(this);
            componentMenuBarThread.start();

            try {
                sleep(20);

            } catch (InterruptedException ignored) {

            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < this.getComponentCount(); i++) {
            if (this.getComponent(i) instanceof Circle) {
                Circle component = (Circle) this.getComponent(i);
                component.draw(g);
            } else if (this.getComponent(i) instanceof NonOrientedArrow) {
                NonOrientedArrow component = (NonOrientedArrow) this.getComponent(i);
                component.draw(g);
            }
        }

    }

    private void createNotOrientedArrow() {
        Point sourcePoint = findTarget();
        if (sourcePoint != null) {
            notOrientedArcMakeThread = new NotOrientedArcMakeThread(this, sourcePoint);
            notOrientedArcMakeThread.start();
        }
    }

    private void createOrientedArrow() {
        Point sourcePoint = findTarget();
        if (sourcePoint != null) {
            orientedArcMakeThread = new OrientedArcMakeThread(this, sourcePoint);
            orientedArcMakeThread.start();
        }
    }

    private void killMenuThread() {
        if (componentMenuBarThread != null)
            componentMenuBarThread.disable();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch (actionType) {
            case MAKEVERTEX -> {
                if (e.getClickCount() == 2) {
                    createVertex();
                }
            }
            case MAKENOTORIENTEDARC -> createNotOrientedArrow();
            case MAKEORIENTEDARC -> createOrientedArrow();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        actionType = gui.actionType;
        killMenuThread();

        if (actionType == ActionType.MAKEVERTEX) {
            moveVertex();
            changeChosenComponent();

            //Right mouse clicked
            if (e.getButton() == 3)
                showMenuForChosenComponent();
        }
    }

    Point findTarget(int x, int y) {
        Point point = new Point(x, y);
        for (int i = 0; i < this.getComponentCount(); i++) {
            if (this.getComponent(i) instanceof Circle) {
                Circle circle = (Circle) this.getComponent(i);
                if (circle.point.equals(point)) {
                    return circle.point;
                }
            }
        }
        return null;
    }

    Circle findCircleTarget(int x, int y) {
        Point point = new Point(x, y);
        for (int i = 0; i < this.getComponentCount(); i++) {
            if (this.getComponent(i) instanceof Circle) {
                Circle circle = (Circle) this.getComponent(i);
                if (circle.point.equals(point)) {
                    return circle;
                }
            }
        }
        return null;
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        killThreads();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
        killThreads();
    }

}


