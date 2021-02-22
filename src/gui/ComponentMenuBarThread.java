package gui;

import figures.Circle;
import figures.NonOrientedArrow;
import graph.Vertex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ComponentMenuBarThread extends Thread {

    JMenuBar componentMenuBar;
    DrawableJPanel drawableJPanel;
    static boolean isActive = false;
    JFrame getIdentifierFrame;

    public ComponentMenuBarThread(DrawableJPanel panel) {
        this.drawableJPanel = panel;
    }

    @Override
    public void run() {

        isActive = true;

        componentMenuBar = new JMenuBar();

        componentMenuBar.setFont(new Font("TimesRoman", Font.BOLD, 12));

        LayoutManager grid = new GridLayout(2, 0);
        componentMenuBar.setLayout(grid);
        componentMenuBar.setBounds(drawableJPanel.getMousePosition().x + DrawableJPanel.radius, drawableJPanel.getMousePosition().y, 140, 50);

        if (drawableJPanel.chosenComponent instanceof Circle) {
            JButton changeIdentifierButton = new JButton("Change identifier");
            JButton removeObjectButton = new JButton("Remove object");

            changeIdentifierButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    getIdentifierFrame = new JFrame("Set identifier");
                    getIdentifierFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    getIdentifierFrame.setResizable(false);
                    getIdentifierFrame.setLayout(new GridLayout(2,0));
                    getIdentifierFrame.setPreferredSize(new Dimension(200,100));
                    getIdentifierFrame.setVisible(true);

                    JButton OKButton = new JButton("Ok");


                    JTextField textField = new JTextField("");
                    textField.setSize(180,80);
                    getIdentifierFrame.add(textField);
                    getIdentifierFrame.add(OKButton);
                    getIdentifierFrame.pack();

                    OKButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            drawableJPanel.componentMenuBarThread.disable();
                            getIdentifierFrame.dispose();

                            Point point = ((Circle) drawableJPanel.chosenComponent).point;

                            Vertex vertex;
                            for(int i=0;i<drawableJPanel.graph.setOfVertexes.size();++i){
                                vertex = drawableJPanel.graph.setOfVertexes.get(i);
                                if(vertex.point.equals(point)){
                                    drawableJPanel.graph.setOfVertexes.get(i).setIdentifier(textField.getText());
                                    ((Circle) drawableJPanel.chosenComponent).setIdentifier(textField.getText());
                                    break;
                                }
                            }
                            drawableJPanel.revalidate();
                            drawableJPanel.repaint();
                        }
                    });

                }
            });

            removeObjectButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    drawableJPanel.deleteChosenComponent();
                    drawableJPanel.componentMenuBarThread.disable();
                }
            });

            componentMenuBar.add(changeIdentifierButton);
            componentMenuBar.add(removeObjectButton);
        } else if (drawableJPanel.chosenComponent instanceof NonOrientedArrow) {

            JButton changeWeightButton = new JButton("Change weight");
            JButton removeObjectButton = new JButton("Remove object");

            changeWeightButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFrame getWeightFrame = new JFrame("Set identifier");
                    getWeightFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    getWeightFrame.setResizable(false);
                    getWeightFrame.setLayout(new GridLayout(2,0));
                    getWeightFrame.setPreferredSize(new Dimension(200,100));
                    getWeightFrame.setVisible(true);

                    JButton OKButton = new JButton("Ok");

                    JTextField textField = new JTextField("");
                    textField.setSize(180,80);
                    getWeightFrame.add(textField);
                    getWeightFrame.add(OKButton);
                    getWeightFrame.pack();

                    OKButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            drawableJPanel.componentMenuBarThread.disable();

                            Point sourcePoint = ((NonOrientedArrow) drawableJPanel.chosenComponent).sourcePoint;
                            Point targetPoint = ((NonOrientedArrow) drawableJPanel.chosenComponent).targetPoint;

                            for (int i = 0; i < drawableJPanel.graph.setOfArcs.size(); ++i) {
                                if (drawableJPanel.graph.setOfArcs.get(i).sourcePoint == sourcePoint
                                        && drawableJPanel.graph.setOfArcs.get(i).targetPoint == targetPoint){
                                    String a = new String(textField.getText());
                                    try {
                                        drawableJPanel.graph.setOfArcs.get(i).weight = Integer.parseInt(textField.getText());
                                        ((NonOrientedArrow) drawableJPanel.chosenComponent).setWeight(Integer.parseInt(textField.getText()));
                                    } catch (NumberFormatException exception){
                                        drawableJPanel.graph.setOfArcs.get(i).weight = 1;
                                        ((NonOrientedArrow) drawableJPanel.chosenComponent).setWeight(1);
                                    }
                                    break;
                                }
                            }

                            getWeightFrame.dispose();
                            drawableJPanel.revalidate();
                            drawableJPanel.repaint();
                        }
                    });

                }
            });

            removeObjectButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    drawableJPanel.deleteChosenComponent();
                    drawableJPanel.componentMenuBarThread.disable();
                }
            });


            componentMenuBar.add(changeWeightButton);
            componentMenuBar.add(removeObjectButton);
        }
        drawableJPanel.add(componentMenuBar);

    }

    public void disable() {
        isActive = false;
        drawableJPanel.remove(componentMenuBar);
        interrupt();
    }
}
