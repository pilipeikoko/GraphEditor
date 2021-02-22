package gui;

import figures.Circle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainGUI extends JFrame {

    final static int radius = Circle.radius;
    final static int dx = 7;
    final static int dy = 30;

    DrawableJPanel drawableJPanel;

    ActionType actionType = ActionType.MAKEVERTEX;

    JToolBar toolbar;

    JScrollPane scrollPane;

    JMenuBar menuBar;

    JButton vertexButton;

    JButton orientedArcButton;
    JButton notOrientedArcButton;

    public MainGUI() {

        super("Graph redactor");

        setLayout(null);
        this.setBounds(300, 60, 1000, 700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       // this.setResizable(false);

        menuBar = new  JMenuBar();
        this.setJMenuBar(menuBar);
        menuBar.setBounds(0,0,this.getWidth(),20);
        JMenu fileMenu = new  JMenu("Файл");
        menuBar.add(fileMenu);


        toolbar = new  JToolBar("Toolbar", JToolBar.VERTICAL);
        toolbar.setBackground(Color.gray);
        toolbar.setBounds(0, 0, 40, this.getHeight());

        vertexButton = new  JButton(new  ImageIcon("src\\gui\\pictures\\makeVertex.png"));
        vertexButton.addActionListener(e -> {
            actionType = actionType.MAKEVERTEX;
            drawableJPanel.grabFocus();
        });
        toolbar.add(vertexButton);

        orientedArcButton = new JButton(new  ImageIcon("src\\gui\\pictures\\makeArcOriented.png"));
        orientedArcButton.addActionListener(event ->{
            actionType = ActionType.MAKEORIENTEDARC;
            drawableJPanel.grabFocus();
        });
        toolbar.add(orientedArcButton);

        notOrientedArcButton = new  JButton(new  ImageIcon("src\\gui\\pictures\\makeArcNotOriented.png"));
        notOrientedArcButton.addActionListener(event -> {
            actionType = ActionType.MAKENOTORIENTEDARC;
            drawableJPanel.grabFocus();
        });
        toolbar.add(notOrientedArcButton);

        this.add(toolbar);

        drawableJPanel = new DrawableJPanel(this);
        drawableJPanel.setPreferredSize(new Dimension(this.getWidth()-60,this.getHeight()-25));

        this.add(drawableJPanel,BorderLayout.PAGE_START);

        scrollPane = new JScrollPane();
        scrollPane.setLocation(40, 10);
        scrollPane.setViewportView(drawableJPanel);
        scrollPane.setSize(800, 500);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane);


    }

    public static void main(String[] args) {
        MainGUI mainGUI = new MainGUI();
        mainGUI.setVisible(true);
    }


}
