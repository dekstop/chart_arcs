/**
 * 
 */
package fm.last.visualizations.arc;

import java.util.Arrays;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

/**
 * TODO: add timespan caption to each graph
 * TODO: smaller history window size
 * 
 * @author martind
 *
 */
public class ChartArcs extends PApplet {
  
  private static final long serialVersionUID = 2934250604260381835L;
  
  //static final int HISTORY_SIZE = 26;
  static final int CHART_SIZE = 100;

  static final int IMAGE_WIDTH = 4000;
  static final int IMAGE_HEIGHT = IMAGE_WIDTH;
  
  static final float SPACE = IMAGE_WIDTH / 80;
  
  static final float CHARTINFO_XPOS = SPACE + IMAGE_WIDTH / 160;
  static final float CHARTINFO_YPOS = SPACE + IMAGE_WIDTH / 160;

  static final float MIN_POINT_SIZE = IMAGE_WIDTH / (CHART_SIZE * 2f);
  static final float PX_PER_REACH = MIN_POINT_SIZE / 100000f;    // reach of 100,000 = MIN_POINT_SIZE pixels
    
  private static final int HEADER_FONTSIZE = IMAGE_WIDTH / 55;
  private static final int FONTSIZE = IMAGE_WIDTH / 80;
  
  private static final float LOGO_DISPLAY_WIDTH = IMAGE_WIDTH / 10;
  
  ChartData cd = null;
  ArcDiagram ad = null;
  
  // typo
  PFont headerFont = null;
  PFont font = null;
  
  // etc
  PImage logo = null;

  
  public void setup() {
    
    cd = ChartData.loadFromXMLFile(args[0]);
    System.out.println("Loaded " + args[0]);
    System.out.println("history size: " + cd.history.size() + " charts.");
    
    ad = generateDiagram(cd);
    System.out.println(ad.points.size() + " points.");
    System.out.println(ad.arcs.size() + " arcs.");

    //System.out.println("Available fonts: " + Arrays.toString(PFont.list()));
    
    headerFont = createFont("TrebuchetMS", HEADER_FONTSIZE);
    font = createFont("Monaco", FONTSIZE);
    
    logo = loadImage("logo_big.png");
    
    //textMode(SCREEN);

    size(IMAGE_WIDTH, IMAGE_HEIGHT);
    smooth();
    noLoop();
  }

  public void draw() {
    
    background(0);
    
    float logoWidth = LOGO_DISPLAY_WIDTH;
    float logoHeight = logo.height * (LOGO_DISPLAY_WIDTH / logo.width); 
    image(logo, 
        width - logoWidth - SPACE, 
        height - logoHeight - SPACE,
        logoWidth, logoHeight);
    
    textFont(headerFont);
    fill(0x0ffffffff);
    //fill(0x0ffC72F2D);
    float baseline = headerFont.ascent;
    text(cd.name, CHARTINFO_XPOS, CHARTINFO_YPOS + baseline);

    textFont(font);
    fill(0x099ffffff);
    baseline += headerFont.descent + (font.ascent*2 + font.descent);
    String infoText = 
      cd.history.size() + " weeks \n" +
      cd.getNumUniqueNames() + " artists";
      
    text(infoText, CHARTINFO_XPOS, CHARTINFO_YPOS + baseline);
    
    ad.draw(g, MIN_POINT_SIZE, true);
    
    //save("images/res_" + cd.resType + "_" + cd.id + ".tga");
    save("images/" + cd.name + ".tga");
    System.out.println("Saved images/" + cd.name + ".tga");
    
    if (args.length > 1 && args[1].equals("-batch")) {
      exit();
    }
  }
  
  /**
   * TODO: implement an exitCounter
   * 
   * @param uc
   * @return
   */
  public ArcDiagram generateDiagram(ChartData uc) {
    ArcDiagram arcd = new ArcDiagram();
    int[] inertiaCounter = new int[CHART_SIZE];
    int[] popularityCounter = new int[CHART_SIZE];
    int[] entryCounter = new int[CHART_SIZE];
    int[] exitCounter = new int[CHART_SIZE];
    int[][] pathCounter = new int[CHART_SIZE][CHART_SIZE];
    int[][] reachCounter = new int[CHART_SIZE][CHART_SIZE];
    
    Arrays.fill(inertiaCounter, 1);
    Arrays.fill(popularityCounter, 1);
    
    if (cd.history.size() <= 1) {
      return arcd;
    }
    
    // generate movement data from chart history    
    for (int historyIdx=1; historyIdx<cd.history.size(); historyIdx++) {
      ChartData.Chart chart = cd.history.get(historyIdx);

      ChartData.Chart prevChart = cd.history.get(historyIdx-1);
      ChartData.Chart nextChart = null;
      if (historyIdx<cd.history.size()-1) {
        nextChart = cd.history.get(historyIdx+1);
      }

      if (chart.entries.size() > 0) {

        for (int chartIdx=0; chartIdx<chart.entries.size() && chartIdx<CHART_SIZE; chartIdx++) {

          ChartData.ChartEntry entry = chart.entries.get(chartIdx);

          // combined reach for position 
          popularityCounter[chartIdx] += entry.reach;
          
          // check for chart position changes
          int prevIdx = prevChart.getIdxByName(entry.name);
          if (prevIdx == -1 || prevIdx >= CHART_SIZE) {
            // mark new entry in charts
            entryCounter[chartIdx]++;
          }
          else {
            // existing entry
            // no change? increase position counter
            if (prevIdx == chartIdx) {
              inertiaCounter[chartIdx]++;
            }
            // moved since last chart? add path
            else {
              pathCounter[prevIdx][chartIdx]++;
              reachCounter[prevIdx][chartIdx] += entry.reach;
            }
          }
          
          // check if entry will vanish in the next chart
          if (nextChart != null) {
            int nextIdx = nextChart.getIdxByName(entry.name);
            if (nextIdx == -1 || nextIdx >= CHART_SIZE) {
              // mark exit point
              exitCounter[chartIdx]++;
            }
          }
        }
      }
    }

    // entry+exit point stats
    float maxEntry = Float.MIN_VALUE;  
    for (int ec : entryCounter) {
      maxEntry = Math.max(ec, maxEntry);
    }
    float entryColMultiplier = 0x0ff / (float)maxEntry;
    
    float maxExit = Float.MIN_VALUE;  
    for (int ec : exitCounter) {
      maxExit = Math.max(ec, maxExit);
    }
    float exitColMultiplier = 0x0ff / (float)maxExit;
    
    // great yellow tone: 255, 204, 0 -- 0x0ffffcc00
    //   static final int COLOR = 0x0ffccccff;
    
    // add points & arcs
    for (int idx=0; idx<CHART_SIZE; idx++) {
//      if (popularityCounter[idx] == 1) {
//        // no entries for this chart position
//        arcd.addPoint("" + idx, 0x0ff000033, 1);
//      }
//      else {
        //arcd.addPoint("" + idx, 0x0ffffcc00, inertiaCounter[idx]);
        arcd.addPoint(
            "" + idx, 
            0x0ff000000 +
            // entry counter
            (((int)(entryCounter[idx] * entryColMultiplier) & 0x0ff) << 8) +
            // exit counter
            (((int)(exitCounter[idx] * exitColMultiplier) & 0x0ff) << 16), 
            //inertiaCounter[idx]);
            popularityCounter[idx]);
//      }
    }
    
    for (int fromIdx=0; fromIdx<CHART_SIZE; fromIdx++) {
      for (int toIdx=0; toIdx<CHART_SIZE; toIdx++) {
        int numPaths = pathCounter[fromIdx][toIdx];
        int reach = reachCounter[fromIdx][toIdx];
        float value = (float)reach * PX_PER_REACH + 1f;
        if (numPaths > 0) {
          if (fromIdx > toIdx) {
            // move up
            arcd.addArc("" + fromIdx, "" + toIdx, 0x06600ff00, value);
          }
          else {
            // move down
            arcd.addArc("" + fromIdx, "" + toIdx, 0x066ff0000, value);            
          }
        }
      }
    }
    
    return arcd;
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Parameters: <usercharts xml file> [-batch]");
      return;
    }
    // stupid java and its stupid nonexistent array features -- why is there no Collection.fromArray?!!!
    String[] myargs = new String[args.length+1];
    myargs[0] = ChartArcs.class.getCanonicalName();
    System.arraycopy(args, 0, myargs, 1, args.length);
    PApplet.main(myargs);
  }
}
