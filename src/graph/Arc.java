package graph;

import java.awt.*;

public class Arc {

    public Point sourcePoint;
    public Point targetPoint;
    public boolean isDirected;
    public double weight = 1;

    public Arc(){

    }

    public Arc(Point sourcePoint, Point targetPoint, boolean isDirected){
        this.isDirected = isDirected;
        this.sourcePoint = sourcePoint;
        this.targetPoint = targetPoint;
//        this.sourcePoint = new Point(sourcePoint);
//        this.targetPoint = new Point(targetPoint);
    }


    public void changeWeight(double newWeight){
        this.weight=newWeight;
    }

    public double getWeight(){
        return this.weight;
    }



}
