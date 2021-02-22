package graph;

import java.awt.*;

public class Vertex {
    public String identifier;
    public Point point;

    Vertex(Point point) {
        this.point = point;
        this.identifier = new String("");
    }

    public void setIdentifier(String identifier){
        this.identifier = new String(identifier);
    }



}
