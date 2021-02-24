package gui;

import figures.Circle;
import figures.NonOrientedArrow;
import figures.OrientedArrow;
import graph.Arc;
import graph.Vertex;
import graph.tasks.FindHamiltonsCycles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MainGUI extends JFrame implements ComponentListener {

    //TODO make window in middle
    //TODO handle exception circle move

    final static int radius = Circle.radius;
    final static int dx = 7;
    final static int dy = 30;

    ActionType actionType = ActionType.MAKEVERTEX;

    private DrawableJPanel drawableJPanel;

    private JScrollPane scrollPane;

    private JToolBar toolbar;

    JMenuBar menuBar;

    public MainGUI() {

        super("Graph redactor");

        setLayout(null);
        this.setBounds(300, 60, 1000, 700);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addComponentListener(this);

        addMenuBar();
        addToolBar();
        addDrawableJPanel();
        addScrollPane();
    }

    private void addScrollPane() {
        scrollPane = new JScrollPane();
        scrollPane.setLocation(40, 10);
        scrollPane.setViewportView(drawableJPanel);
        scrollPane.setSize(new Dimension(this.getWidth() - 60, this.getHeight() - 70));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane);
    }

    private void addDrawableJPanel() {
        drawableJPanel = new DrawableJPanel(this);
        drawableJPanel.setPreferredSize(new Dimension(2000, 1500));

        this.add(drawableJPanel, BorderLayout.PAGE_START);
    }

    private void addToolBar() {
        toolbar = new JToolBar("Toolbar", JToolBar.VERTICAL);
        toolbar.setBackground(Color.gray);
        toolbar.setBounds(0, 0, 40, this.getHeight());

        JButton vertexButton = new JButton(new ImageIcon("src\\gui\\pictures\\makeVertex.png"));
        vertexButton.addActionListener(e -> {
            actionType = ActionType.MAKEVERTEX;
            drawableJPanel.grabFocus();
        });
        toolbar.add(vertexButton);

        JButton orientedArcButton = new JButton(new ImageIcon("src\\gui\\pictures\\makeArcOriented.png"));
        orientedArcButton.addActionListener(event -> {
            actionType = ActionType.MAKEORIENTEDARC;
            drawableJPanel.grabFocus();
        });
        toolbar.add(orientedArcButton);

        JButton notOrientedArcButton = new JButton(new ImageIcon("src\\gui\\pictures\\makeArcNotOriented.png"));
        notOrientedArcButton.addActionListener(event -> {
            actionType = ActionType.MAKENOTORIENTEDARC;
            drawableJPanel.grabFocus();
        });
        toolbar.add(notOrientedArcButton);

        this.add(toolbar);
    }

    private void addMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBackground(Color.gray);
        menuBar.setBounds(0, 0, this.getWidth(), 20);

        this.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setPreferredSize(new Dimension(50, 20));
        menuBar.add(fileMenu);

        JMenuItem load = new JMenuItem("Load");

        load.addActionListener(e -> loadFromFile());
        fileMenu.add(load);
        fileMenu.addSeparator();

        JMenuItem save = new JMenuItem("Save");

        save.addActionListener(e -> saveToFile());
        fileMenu.add(save);

        JMenu graphTasksMenu = new JMenu("Graph tasks");
        graphTasksMenu.setPreferredSize(new Dimension(100, 20));
        menuBar.add(graphTasksMenu);

        JMenuItem graphTask = new JMenuItem("Find all possible Hamilton's cycles");

        graphTask.addActionListener(e -> new Thread(this::solveGraphTask).start());
        graphTasksMenu.add(graphTask);
    }

    private void solveGraphTask() {
        drawableJPanel.rejectComponent();

        FindHamiltonsCycles hamiltonsCycle = new FindHamiltonsCycles(drawableJPanel.graph);
        hamiltonsCycle.solveTask();

        for (int i = 0; i < hamiltonsCycle.hamiltonsCycles.size(); ++i) {
            ArrayList<Integer> cycle = hamiltonsCycle.hamiltonsCycles.get(i);

            for (int currentVertex : cycle) {

                int x = drawableJPanel.graph.setOfVertexes.get(currentVertex).point.x;
                int y = drawableJPanel.graph.setOfVertexes.get(currentVertex).point.y;

                Circle circle = drawableJPanel.findCircleTarget(x, y);

                drawableJPanel.chooseComponent(circle);
                drawableJPanel.paintImmediately(0, 0, drawableJPanel.getWidth(), drawableJPanel.getHeight());

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
            drawableJPanel.paintImmediately(0, 0, drawableJPanel.getWidth(), drawableJPanel.getHeight());

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save to");
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {

            try {
                pushAllInfo(fileChooser);
            } catch (IOException ignored) {

            }

        }
    }

    private void pushAllInfo(JFileChooser fileChooser) throws IOException {
        File selectedFile = fileChooser.getSelectedFile();
        FileWriter fileWriter = new FileWriter(selectedFile);

        int amountOfVertexes = drawableJPanel.graph.setOfVertexes.size();
        int amountOfArcs = drawableJPanel.graph.setOfArcs.size();


        StringBuilder stringBuilder;
        stringBuilder = new StringBuilder();
        stringBuilder.append(amountOfVertexes).append(" ");
        stringBuilder.append(amountOfArcs).append("\n");
        fileWriter.write(stringBuilder.toString(), 0, stringBuilder.toString().length());

        pushVertexes(amountOfVertexes,stringBuilder,fileWriter);
        pushArcs(amountOfArcs,stringBuilder,fileWriter);

        fileWriter.close();
    }

    private void pushArcs(int amountOfArcs, StringBuilder stringBuilder, FileWriter fileWriter) throws IOException {
        for (int i = 0; i < amountOfArcs; ++i) {
            Arc arc = drawableJPanel.graph.setOfArcs.get(i);
            stringBuilder.delete(0, stringBuilder.length());

            int sourceX = arc.sourcePoint.x;
            int sourceY = arc.sourcePoint.y;
            int targetX = arc.targetPoint.x;
            int targetY = arc.targetPoint.y;
            int weight = arc.weight;
            boolean isDirected = arc.isDirected;
            int intIsDirected = isDirected ? 1 : 0;

            stringBuilder.append(sourceX).append(" ").append(sourceY).append(" ");
            stringBuilder.append(targetX).append(" ").append(targetY).append(" ");
            stringBuilder.append(weight).append(" ").append(intIsDirected).append("\n");

            fileWriter.write(stringBuilder.toString());
        }
    }

    private void pushVertexes(int amountOfVertexes,StringBuilder stringBuilder,FileWriter fileWriter) throws IOException {
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
    }

    private void loadFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load from");
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

            if (scanner.hasNextInt())
                amountOfVertexes = scanner.nextInt();

            if (scanner.hasNextInt())
                amountOfArcs = scanner.nextInt();

            addALlVertexes(amountOfVertexes,scanner);
            addAllArcs(amountOfArcs,scanner);

            fileReader.close();
            revalidate();
            repaint();
            drawableJPanel.grabFocus();

        } catch (IOException ignored) {

        }
    }

    private void addAllArcs(int amountOfArcs, Scanner scanner) {
        for (int i = 0; i < amountOfArcs; ++i) {
            int sourceX = scanner.nextInt();
            int sourceY = scanner.nextInt();
            int targetX = scanner.nextInt();
            int targetY = scanner.nextInt();
            int weight = scanner.nextInt();

            int intIsDirected = scanner.nextInt();
            boolean isDirected = intIsDirected == 1;

            Point sourcePoint = drawableJPanel.findTarget(sourceX, sourceY);
            Point targetPoint = drawableJPanel.findTarget(targetX, targetY);

            drawableJPanel.graph.addArc(new Arc(sourcePoint, targetPoint, isDirected, weight));

            if (isDirected)
                drawableJPanel.add(new OrientedArrow(sourcePoint, targetPoint, weight));
            else
                drawableJPanel.add(new NonOrientedArrow(sourcePoint, targetPoint, weight));

        }
    }

    private void addALlVertexes(int amountOfVertexes, Scanner scanner) {
        for (int i = 0; i < amountOfVertexes; ++i) {
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            String identifier = scanner.nextLine();

            drawableJPanel.createVertex(x, y, identifier);
        }
    }

    public static void main(String[] args) {
        MainGUI mainGUI = new MainGUI();
        mainGUI.setVisible(true);
    }


    @Override
    public void componentResized(ComponentEvent e) {
        toolbar.setSize(40,this.getHeight());
        scrollPane.setSize(new Dimension(this.getWidth() - 60, this.getHeight() - 70));
        revalidate();
        repaint();
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
