package graph;

import figures.Point;

public class Vertex {
    public String identifier;
    public Point point;

    Vertex(Point point) {
        this.point = point;
        this.identifier = "";
    }

    Vertex(Point point, String identifier) {
        this.point = point;
        this.identifier = identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }


}
