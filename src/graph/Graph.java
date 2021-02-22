package graph;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Graph {
    public ArrayList<Vertex> setOfVertexes = new ArrayList<>();
    public ArrayList<Arc> setOfArcs = new ArrayList<>();

    public Graph() {

    }

    public void addVertex(Point point) {
        setOfVertexes.add(new Vertex(point));
    }

    public void removeVertex(Point point) {
        Vertex vertex;
        for (int i = 0; i < setOfVertexes.size(); ++i) {
            vertex = setOfVertexes.get(i);
            if (vertex.point.equals(point)) {
                setOfVertexes.remove(vertex);
                removeAllIncidentalArcs(vertex.point);
                break;
            }
        }

    }

    private void removeAllIncidentalArcs(Point point) {
        for (int i = 0; i < setOfArcs.size(); i++) {
            Arc currentArc = setOfArcs.get(i);
            if (currentArc.targetPoint != null && currentArc.sourcePoint != null
                    && (currentArc.sourcePoint.equals(point) || currentArc.targetPoint.equals(point))) {
                setOfArcs.remove(currentArc);
                --i;
            }
        }
    }


    public void addArc(Arc triplet) {
        setOfArcs.add(triplet);
    }

    public void removeArc(Point sourcePoint, Point targetPoint) {
        for (int i = 0; i < setOfArcs.size(); ++i) {
            if (setOfArcs.get(i).sourcePoint.equals(sourcePoint)
                    && setOfArcs.get(i).targetPoint.equals(targetPoint)) {
                setOfArcs.remove(i);
                break;
            }
        }
    }
}
