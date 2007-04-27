/**
 * 
 */
package fm.last.visualizations.arc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * Used to build an arc diagram and then draw it on an arbitrary-sized
 * canvas.
 * 
 * The interface to add points and arcs assumes that every point is assigned
 * a unique name, and that both points and arcs are assigned a numeric value.
 * 
 * @author martind
 *
 */
public class ArcDiagram {
  
  /**
   * @author mongo
   *
   */
  public class Arc {
    Point from;
    Point to;
    int color;
    float value;
    public Arc(Point from, Point to, int color, float value) {
      this.from = from;
      this.to = to;
      this.color = color;
      this.value = value;
    }
  }

  /**
   * @author mongo
   *
   */
  public class Point {
    String name;
    int color;
    float value;
    public Point(String name, int color, float value) {
      this.name = name;
      this.color = color;
      this.value = value;
    }
  }

  List<Point> points = new ArrayList<Point>();
  List<Arc> arcs = new ArrayList<Arc>();
  
  

  
  /**
   * 
   * @param name
   * @param numItems 
   */
  public void addPoint(String name, int color, float value) {
//    System.out.println("*** add point");
//    System.out.println(name);
//    System.out.println(color);
//    System.out.println(value);
    
    points.add(new Point(name, color, value));
  }
  
  /**
   * 
   * @param name
   * @return
   * @throws IllegalArgumentException
   */
  protected Point findPointByName(String name) {
    for (Point p : points) {
      if (p.name.equals(name)) {
        return p;
      }
    }
    throw new IllegalArgumentException("Unknown point: " + name);
  }
  
  /**
   * 
   * @param point1Name
   * @param point2Name
   * @param numItems 
   * @throws IllegalArgumentException if there are no points with the specified names
   */
  public void addArc(String point1Name, String point2Name, int color, float value) {
    
//    System.out.println("*** add arc");
//    System.out.println(point1Name);
//    System.out.println(point2Name);
//    System.out.println(color);
//    System.out.println(value);
    
    Point p1 = findPointByName(point1Name);
    Point p2 = findPointByName(point2Name);
    arcs.add(new Arc(p1, p2, color, value));
  }
  
  /**
   * 
   * @param canvas
   * @param minPointSize 
   * @param bgcolor
   * @param vertical
   */
  public void draw(PGraphics canvas, float minPointSize, boolean vertical) {
    
    Map<Point, Float> positions = new HashMap<Point, Float>();
    Map<Point, Float> diameters = new HashMap<Point, Float>();

    // point layout
    float sumP = 0;
    float minP = Float.MAX_VALUE;
    float maxP = Float.MIN_VALUE;
    
    for (Point p : points) {
      sumP += p.value;
      minP = Math.min(p.value, minP);
      maxP = Math.max(p.value, maxP);
    }
    
    int numPoints = points.size();
    float winSize = (float)(vertical == true ? canvas.height : canvas.width);
    float pxPerPV = (
        (float)winSize - 
        minPointSize * numPoints -
        minPointSize * 2) / (float)sumP;
    //pxPerPV = 5.3f / 100000f / 5f;
    
    float pos = minPointSize;
    for (Point p : points) {
      float diameter = p.value * pxPerPV + minPointSize;
      diameters.put(p, diameter);
      positions.put(p, pos + diameter/2f);
      pos += diameter;
    }
    
    // arc stats
//  float minA = Float.MAX_VALUE;
//  float maxA = Float.MIN_VALUE;
//
//  for (Arc a : arcs) {
//    minA = Math.min(a.value, minA);
//    maxA = Math.max(a.value, maxA);
//  }
  
    // prepare drawing
    canvas.ellipseMode(PConstants.CENTER);

    // draw points
    canvas.noStroke();
    //canvas.strokeWeight(2f);
    //canvas.stroke(0x0ffffffff);
    
    for (Point p: points) {
      float diameter = diameters.get(p);
      canvas.fill(p.color);
      //canvas.fill(0x066ffffff);
      
      float x, y;
      if (vertical == true) {
        x = canvas.width/2f;
        y = positions.get(p);
        //canvas.rect(canvas.width*0.98f - minPointSize, y-diameter/2f, canvas.width*0.02f, diameter);
      }
      else {
        x = positions.get(p);
        y = canvas.height/2f;
      }
      canvas.ellipse(x, y, diameter, diameter);
    }

    // draw arcs
    canvas.noFill();
    
    for (Arc a : arcs) {
      float fromPos = positions.get(a.from);
      float toPos = positions.get(a.to);
      float center = (fromPos + toPos) / 2f;
      float diameter = Math.abs(toPos - fromPos);
      
      canvas.stroke(a.color);
      canvas.strokeWeight(a.value);
      
      float x, y;
      float angle1, angle2;
      if (vertical == true) {
        x = canvas.width/2f;
        y = center;
        angle1 = (float)Math.PI/2;
        angle2 = (float)(2f * Math.PI - Math.PI/2f);
      }
      else {
        x = center;
        y = canvas.height/2f;
        angle1 = 0;
        angle2 = (float)Math.PI;
      }

      int fromIdx = points.indexOf(a.from);
      int toIdx = points.indexOf(a.to);
      
      if (fromIdx > toIdx) {
        // move up
        canvas.arc(x, y, diameter, diameter, angle1, angle2);          
      }
      else {
        // move down
        canvas.arc(x, y, diameter, diameter, angle2, angle1);
      }
    }
  }

}
