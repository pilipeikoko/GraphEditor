package gui;

import figures.Circle;
import figures.Point;
import graph.Vertex;
import gui.figuresJComponents.CircleJComponent;
import gui.figuresJComponents.NotOrientedArrowJComponent;
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
                gui.actionType = ActionType.MAKE_VERTEX;
            }
        });

        Object click2 = new Object();
        this.getInputMap().put(KeyStroke.getKeyStroke("2"), click2);
        this.getActionMap().put(click2, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                killThreads();
                gui.actionType = ActionType.MAKE_ORIENTED_ARC;
            }
        });


        Object click3 = new Object();
        this.getInputMap().put(KeyStroke.getKeyStroke("3"), click3);
        this.getActionMap().put(click3, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                killThreads();
                gui.actionType = ActionType.MAKE_NOT_ORIENTED_ARC;
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

            if (chosenComponent instanceof CircleJComponent) {
                Point point = ((CircleJComponent) chosenComponent).circle.point;
                graph.removeVertex(point);
                removeIncidentalArcs(point);
            } else if (chosenComponent instanceof NotOrientedArrowJComponent) {
                Point sourcePoint = ((NotOrientedArrowJComponent) chosenComponent).arrow.sourcePoint;
                Point targetPoint = ((NotOrientedArrowJComponent) chosenComponent).arrow.targetPoint;
                graph.removeArc(sourcePoint, targetPoint);
            }
        }
    }

    private void removeIncidentalArcs(Point point) {
        for (int i = 0; i < this.getComponentCount(); ++i) {
            Component component = this.getComponent(i);
            if (component instanceof NotOrientedArrowJComponent
                    && (((NotOrientedArrowJComponent) component).arrow.sourcePoint.equals(point)
                    || ((NotOrientedArrowJComponent) component).arrow.targetPoint.equals(point))) {
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
        java.awt.Point mousePosition = getMousePosition();
        Point currentPoint = new Point(mousePosition.x, mousePosition.y);
        for (Vertex vertex : graph.setOfVertexes) {
            Point point = vertex.point;
            if (Math.abs(point.x - currentPoint.x) <= radius && Math.abs(point.y - currentPoint.y) <= radius) {
                return point;
            }
        }
        return null;
    }

    Point findTargetAtComponents() {
        java.awt.Point mousePosition = new java.awt.Point(getMousePosition().x, getMousePosition().y);

        Point currentPoint = new Point(mousePosition.x, mousePosition.y);
        for (int i = 0; i < this.getComponentCount(); ++i) {
            Component component = this.getComponent(i);
            if (component instanceof CircleJComponent) {
                Point point = ((CircleJComponent) component).circle.point;
                if (Math.abs(point.x - currentPoint.x) <= radius && Math.abs(point.y - currentPoint.y) <= radius) {
                    return point;
                }
            }
        }
        return null;
    }

    private int findIndexOfTarget() {
        java.awt.Point currentPoint = new java.awt.Point(getMousePosition().x, getMousePosition().y);
        for (int i = 0; i < getComponentCount(); i++) {
            Component component = getComponent(i);
            if (component instanceof CircleJComponent) {
                Point point = ((CircleJComponent) component).circle.point;
                if (Math.abs(point.x - currentPoint.x) <= radius
                        && Math.abs(point.y - currentPoint.y) <= radius) {
                    return i;
                }
            } else if (component instanceof NotOrientedArrowJComponent) {
                Line2D line = ((NotOrientedArrowJComponent) component).line;
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
        CircleJComponent circleJComponent = new CircleJComponent(point);
        circleJComponent.setIdentifier(identifier);

        this.add(circleJComponent);
        graph.addVertex(point, identifier);

        rejectComponent();
        chooseComponent(circleJComponent);
    }

    private void createVertex() {
        int x = getMousePosition().x;
        int y = getMousePosition().y;

        this.grabFocus();

        Point point = new Point(x, y);
        CircleJComponent circleJComponent = new CircleJComponent(point);

        this.add(circleJComponent);
        graph.addVertex(point);

        rejectComponent();
        chooseComponent(circleJComponent);
    }


    void rejectComponent() {
        isComponentChosen = false;
        if (chosenComponent instanceof CircleJComponent) {
            CircleJComponent circleJComponent = (CircleJComponent) chosenComponent;
            circleJComponent.rejectObject();
        } else if (chosenComponent instanceof NotOrientedArrowJComponent) {
            NotOrientedArrowJComponent notOrientedArrowJComponent = (NotOrientedArrowJComponent) chosenComponent;
            notOrientedArrowJComponent.rejectObject();
        }
        revalidate();
        repaint();
    }

    void chooseComponent(Component component) {
        isComponentChosen = true;
        if (component instanceof CircleJComponent) {
            CircleJComponent circleJComponent = (CircleJComponent) component;
            circleJComponent.chooseObject();
            chosenComponent = circleJComponent;
        } else if (component instanceof NotOrientedArrowJComponent) {
            NotOrientedArrowJComponent notOrientedArrowJComponent = (NotOrientedArrowJComponent) component;
            notOrientedArrowJComponent.chooseObject();
            chosenComponent = notOrientedArrowJComponent;
        }
        revalidate();
        repaint();

    }

    void killThreads() {
        if (circleMoveThread != null && circleMoveThread.isAlive()) {
            circleMoveThread.disable();
        } else if (notOrientedArcMakeThread != null && notOrientedArcMakeThread.isAlive()) {
            notOrientedArcMakeThread.disable();
            actionType = ActionType.MAKE_VERTEX;
        } else if (orientedArcMakeThread != null && orientedArcMakeThread.isAlive()) {
            orientedArcMakeThread.disable();
            actionType = ActionType.MAKE_VERTEX;
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
            if (this.getComponent(i) instanceof CircleJComponent) {
                CircleJComponent component = (CircleJComponent) this.getComponent(i);
                component.draw(g);
            } else if (this.getComponent(i) instanceof NotOrientedArrowJComponent) {
                NotOrientedArrowJComponent component = (NotOrientedArrowJComponent) this.getComponent(i);
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
            case MAKE_VERTEX -> {
                if (e.getClickCount() == 2) {
                    createVertex();
                }
            }
            case MAKE_NOT_ORIENTED_ARC -> createNotOrientedArrow();
            case MAKE_ORIENTED_ARC -> createOrientedArrow();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        actionType = gui.actionType;
        killMenuThread();

        if (actionType == ActionType.MAKE_VERTEX) {
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
            if (this.getComponent(i) instanceof CircleJComponent) {
                CircleJComponent circleJComponent = (CircleJComponent) this.getComponent(i);
                if (circleJComponent.circle.point.x == point.x && circleJComponent.circle.point.y == point.y) {
                    return circleJComponent.circle.point;
                }
            }
        }
        grabFocus();
        return null;
    }

    CircleJComponent findCircleTarget(int x, int y) {
        Point point = new Point(x, y);
        for (int i = 0; i < this.getComponentCount(); i++) {
            if (this.getComponent(i) instanceof CircleJComponent) {
                CircleJComponent circleJComponent = (CircleJComponent) this.getComponent(i);
                if (circleJComponent.circle.point.x == point.x && circleJComponent.circle.point.y == point.y) {
                    return circleJComponent;
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


