/**
 * 
 */
package fm.last.visualizations.arc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author martind
 *
 */
public class ChartData {

  static class ChartEntry {
    String name;
    int reach;
    int counter;
    public ChartEntry(String name, int reach, int counter) {
      this.name = name;
      this.reach = reach;
      this.counter = counter;
    }
  }

  static class Chart {
    List<ChartEntry> entries = new ArrayList<ChartEntry>();
    int startTimestamp;
    int endTimestamp;
    
    int getIdxByName(String name) {
      for (ChartEntry entry: entries) {
        if (entry.name.equals(name)) {
          return entries.indexOf(entry);
        }
      }
      return -1;
    }
  }

  public List<Chart> history = new ArrayList<Chart>();
  public String name;
  public int resType;
  public int id;

  public int getNumUniqueNames() {
    List<String> names = new ArrayList<String>();
    for (Chart chart : history) {
      for (ChartEntry entry : chart.entries) {
        if (names.contains(entry.name) == false) {
          names.add(entry.name);
        }
      }
    }
    return names.size();
  }

  /**
   * 
   * @param doc
   * @return
   */
  public static ChartData loadFromXMLDocument(Document doc) {
    ChartData cd = new ChartData();

    Node rootNode = doc.getDocumentElement();
    cd.name = rootNode.getAttributes().getNamedItem("name").getNodeValue();
    cd.resType = Integer.parseInt(rootNode.getAttributes().getNamedItem("restype").getNodeValue());
    cd.id = Integer.parseInt(rootNode.getAttributes().getNamedItem("id").getNodeValue());

    NodeList chartNodes = rootNode.getChildNodes();
    for (int idx = 0; idx < chartNodes.getLength(); idx++) {

      Node chartNode = chartNodes.item(idx);

      if (chartNode.getNodeType() == Node.ELEMENT_NODE &&
          chartNode.getNodeName().equals("chart")) {

        Chart chart = new Chart();
        chart.startTimestamp = Integer.parseInt(chartNode.getAttributes().getNamedItem("from").getNodeValue());
        chart.endTimestamp = Integer.parseInt(chartNode.getAttributes().getNamedItem("to").getNodeValue());

        cd.history.add(chart);

        NodeList entryNodes = chartNode.getChildNodes();
        for (int idx2 = 0; idx2 < entryNodes.getLength(); idx2++) {

          Node entryNode = entryNodes.item(idx2);

          if (entryNode.getNodeType() == Node.ELEMENT_NODE &&
              entryNode.getNodeName().equals("entry")) {

            chart.entries.add(new ChartEntry(
                entryNode.getAttributes().getNamedItem("name").getNodeValue(),
                Integer.parseInt(entryNode.getAttributes().getNamedItem("reach").getNodeValue()),
                Integer.parseInt(entryNode.getAttributes().getNamedItem("counter").getNodeValue())
            ));
          }
        }
      }
    }
    return cd;
  }

  /**
   * 
   * @param filename
   * @return
   */
  public static ChartData loadFromXMLFile(String filename) {
    Document doc = null;
    File docFile = new File(filename);

    // Parse the file
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      doc = db.parse(docFile);
    } catch (Exception e) {
      throw new RuntimeException("Problem parsing the file [" + filename + "]", e);
    }   

    return loadFromXMLDocument(doc);
  }
}
