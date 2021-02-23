package graph;

import java.awt.*;

public class Arc {

    public Point sourcePoint;
    public Point targetPoint;
    public boolean isDirected;
    public int weight = 1;

    public Arc(){

    }

    public Arc(Point sourcePoint, Point targetPoint, boolean isDirected){
        this.isDirected = isDirected;
        this.sourcePoint = sourcePoint;
        this.targetPoint = targetPoint;
    }
    public Arc(Point sourcePoint, Point targetPoint, boolean isDirected, int weight){
        this.isDirected = isDirected;
        this.sourcePoint = sourcePoint;
        this.targetPoint = targetPoint;
        this.weight = weight;
    }




    public void changeWeight(int newWeight){
        this.weight=newWeight;
    }

    public double getWeight(){
        return this.weight;
    }



}
