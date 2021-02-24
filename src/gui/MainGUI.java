package gui;

import figures.Circle;
import figures.NonOrientedArrow;
import figures.OrientedArrow;
import graph.Arc;
import graph.Vertex;
import graph.tasks.FindHamiltonsCycles;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MainGUI extends JFrame {

    //TODO made window in middle
    //TODO handle exception circle move

    final static int radius = Circle.radius;
    final static int dx = 7;
    final static int dy = 30;

    ActionType actionType = ActionType.MAKEVERTEX;

    DrawableJPanel drawableJPanel;
    JScrollPane scrollPane;

    JToolBar toolbar;

    JMenuBar menuBar;

    JButton vertexButton;
    JButton orientedArcButton;
    JButton notOrientedArcButton;

    public MainGUI() {

        super("Graph redactor");

        setLayout(null);
        this.setBounds(300, 60, 1000, 700);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addMenuBar();

        toolbar = new JToolBar("Toolbar", JToolBar.VERTICAL);
        toolbar.setBackground(Color.gray);
        toolbar.setBounds(0, 0, 40, this.getHeight());

        vertexButton = new JButton(new ImageIcon("src\\gui\\pictures\\makeVertex.png"));
        vertexButton.addActionListener(e -> {
            actionType = ActionType.MAKEVERTEX;
            drawableJPanel.grabFocus();
        });
        toolbar.add(vertexButton);

        orientedArcButton = new JButton(new ImageIcon("src\\gui\\pictures\\makeArcOriented.png"));
        orientedArcButton.addActionListener(event -> {
            actionType = ActionType.MAKEORIENTEDARC;
            drawableJPanel.grabFocus();
        });
        toolbar.add(orientedArcButton);

        notOrientedArcButton = new JButton(new ImageIcon("src\\gui\\pictures\\makeArcNotOriented.png"));
        notOrientedArcButton.addActionListener(event -> {
            actionType = ActionType.MAKENOTORIENTEDARC;
            drawableJPanel.grabFocus();
        });
        toolbar.add(notOrientedArcButton);

        this.add(toolbar);

        drawableJPanel = new DrawableJPanel(this);
        drawableJPanel.setPreferredSize(new Dimension(2000, 1500));

        this.add(drawableJPanel, BorderLayout.PAGE_START);

        scrollPane = new JScrollPane();
        scrollPane.setLocation(40, 10);
        scrollPane.setViewportView(drawableJPanel);
        scrollPane.setSize(new Dimension(this.getWidth() - 60, this.getHeight() - 70));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane);


    }

    private void addMenuBar() {
        menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        menuBar.setBackground(Color.gray);
        menuBar.setBounds(0, 0, this.getWidth(), 20);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setPreferredSize(new Dimension(86, 20));
        menuBar.add(fileMenu);

        JMenuItem load = new JMenuItem("Load");

        load.addActionListener(e -> loadFromFile());
        fileMenu.add(load);
        fileMenu.addSeparator();

        JMenuItem save = new JMenuItem("Save");

        save.addActionListener(e -> saveToFile());
        fileMenu.add(save);
        fileMenu.addSeparator();

        JMenuItem graphTask = new JMenuItem("Graph task");

        graphTask.addActionListener(e -> new Thread(this::solveGraphTask).start());
        fileMenu.add(graphTask);
    }

    private void solveGraphTask() {
        FindHamiltonsCycles hamiltonsCycle = new FindHamiltonsCycles(drawableJPanel.graph);
        hamiltonsCycle.solveTask();

        for(int i=0;i<hamiltonsCycle.hamiltonsCycles.size();++i){
            ArrayList<Integer> cycle = hamiltonsCycle.hamiltonsCycles.get(i);

            for (int currentVertex : cycle) {
                int x = drawableJPanel.graph.setOfVertexes.get(currentVertex).point.x;
                int y = drawableJPanel.graph.setOfVertexes.get(currentVertex).point.y;
                Circle circle = drawableJPanel.findCircleTarget(x, y);
                drawableJPanel.chooseComponent(circle);
                drawableJPanel.paintImmediately(0,0,drawableJPanel.getWidth(),drawableJPanel.getHeight());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {

                }
            }
            for (int currentVertex : cycle) {
                int x = drawableJPanel.graph.setOfVertexes.get(currentVertex).point.x;
                int y = drawableJPanel.graph.setOfVertexes.get(currentVertex).point.y;
                Circle circle = drawableJPanel.findCircleTarget(x, y);
                circle.rejectObject();
            }
            drawableJPanel.paintImmediately(0,0,drawableJPanel.getWidth(),drawableJPanel.getHeight());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {

            }
        }
    }

    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {

            try {
                File selectedFile = fileChooser.getSelectedFile();
                FileWriter fileWriter = new FileWriter(selectedFile);

                int amountOfVertexes = drawableJPanel.graph.setOfVertexes.size();
                int amountOfArcs = drawableJPanel.graph.setOfArcs.size();


                StringBuilder stringBuilder;
                stringBuilder = new StringBuilder();
                stringBuilder.append(amountOfVertexes).append(" ");
                stringBuilder.append(amountOfArcs).append("\n");
                fileWriter.write(stringBuilder.toString(), 0, stringBuilder.toString().length());

                for (int i = 0; i < amountOfVertexes; ++i) {
                    Vertex vertex = drawableJPanel.graph.setOfVertexes.get(i);
                    stringBuilder.delete(0, stringBuilder.length());

                    Point point = vertex.point;
                    String identifier = vertex.identifier;

                    stringBuilder.append(point.x).append(" ");
                    stringBuilder.append(point.y).append(" ");
                    stringBuilder.append(identifier).append("\n");
                    fileWriter.write(stringBuilder.toString());
                }

                int sourceX, sourceY, targetX, targetY;
                int weight;
                boolean isDirected;
                int intIsDirected;

                for (int i = 0; i < amountOfArcs; ++i) {
                    Arc arc = drawableJPanel.graph.setOfArcs.get(i);
                    stringBuilder.delete(0, stringBuilder.length());

                    sourceX = arc.sourcePoint.x;
                    sourceY = arc.sourcePoint.y;
                    targetX = arc.targetPoint.x;
                    targetY = arc.targetPoint.y;
                    weight = arc.weight;
                    isDirected = arc.isDirected;
                    intIsDirected = isDirected ? 1 : 0;
                    stringBuilder.append(sourceX).append(" ").append(sourceY).append(" ");
                    stringBuilder.append(targetX).append(" ").append(targetY).append(" ");
                    stringBuilder.append(weight).append(" ").append(intIsDirected).append("\n");
                    fileWriter.write(stringBuilder.toString());
                }
                fileWriter.close();
            } catch (IOException ignored) {

            }


        }
    }

    private void loadFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            drawableJPanel.removeAll();
            drawableJPanel.graph.setOfVertexes.clear();
            drawableJPanel.graph.setOfArcs.clear();
            addAllComponents(fileChooser);
        }


    }

    private void addAllComponents(JFileChooser fileChooser) {
        try {
            File selectedFile = fileChooser.getSelectedFile();
            FileReader fileReader = new FileReader(selectedFile);
            Scanner scanner = new Scanner(fileReader);

            int amountOfVertexes = 0;
            int amountOfArcs = 0;

            if (scanner.hasNextInt()) {
                amountOfVertexes = scanner.nextInt();
            }
            if (scanner.hasNextInt()) {
                amountOfArcs = scanner.nextInt();
            }


            int x, y;
            String identifier;
            for (int i = 0; i < amountOfVertexes; ++i) {
                x = scanner.nextInt();
                y = scanner.nextInt();
                identifier = scanner.nextLine();

                Point point = new Point(x, y);

              //  drawableJPanel.graph.addVertex(point);
              //  drawableJPanel.graph.setOfVertexes.get(i).setIdentifier(identifier);

                drawableJPanel.createVertex(x, y, identifier);
            }

            int sourceX, sourceY, targetX, targetY;
            int weight;
            boolean isDirected;
            int intIsDirected;

            for (int i = 0; i < amountOfArcs; ++i) {
                sourceX = scanner.nextInt();
                sourceY = scanner.nextInt();
                targetX = scanner.nextInt();
                targetY = scanner.nextInt();
                weight = scanner.nextInt();

                intIsDirected = scanner.nextInt();
                isDirected = intIsDirected == 1;

                Point sourcePoint = drawableJPanel.findTarget(sourceX, sourceY);
                Point targetPoint = drawableJPanel.findTarget(targetX, targetY);

                drawableJPanel.graph.addArc(new Arc(sourcePoint, targetPoint, isDirected, weight));

                if (isDirected)
                    drawableJPanel.add(new OrientedArrow(sourcePoint, targetPoint, weight));
                else
                    drawableJPanel.add(new NonOrientedArrow(sourcePoint, targetPoint, weight));

            }

            fileReader.close();
            revalidate();
            repaint();
            drawableJPanel.grabFocus();

        } catch (IOException ignored) {

        }
    }

    public static void main(String[] args) {
        MainGUI mainGUI = new MainGUI();
        mainGUI.setVisible(true);
    }


}
