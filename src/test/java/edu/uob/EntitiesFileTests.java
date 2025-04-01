package edu.uob;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashSet;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class EntitiesFileTests {

  // Test to make sure that the basic entities file is readable
  @Test
  void testBasicEntitiesFileIsReadable() {
      try {
          Parser parser = new Parser();
          StringBuilder fileName = new StringBuilder("config");
          fileName.append(File.separator).append("basic-entities.dot");
          FileReader reader = new FileReader(fileName.toString());
          parser.parse(reader);
          Graph wholeDocument = parser.getGraphs().get(0);
          LinkedHashSet<Graph> sections = new LinkedHashSet<>(wholeDocument.getSubgraphs());

          LinkedHashSet<Graph> locations = new LinkedHashSet<>();

          if(!sections.isEmpty()) {

              // The locations will always be in the first subgraph
              Iterator<Graph> sectionsIterator = sections.iterator();
              if(sectionsIterator.hasNext()) {
                  Graph section = sectionsIterator.next();
                  locations.addAll(section.getSubgraphs());
              }

              Graph firstLocation = null;
              Iterator<Graph> locationsIterator = locations.iterator();
              if(locationsIterator.hasNext()) {
                  firstLocation = locationsIterator.next();
              }

              Node locationDetails = null;
              if(firstLocation != null) {
                  locationDetails = firstLocation.getNodes(false).get(0);
              }

              // Yes, you do need to get the ID twice !
              String locationName = null;
              if(locationDetails != null){
                  locationName = locationDetails.getId().getId();
              }
              assertEquals("cabin", locationName, "First location should have been 'cabin'");

              // The paths will always be in the second subgraph
              LinkedHashSet<Graph> path = new LinkedHashSet<>();
              int pathIndex = 0;
              for (Graph section : sections) {
                  if (pathIndex == 1) {
                      path.addAll(section.getSubgraphs());
                      break;
                  } else {
                      pathIndex++;
                  }
              }
              Edge firstPath = null;
              Iterator<Graph> pathIterator = path.iterator();
              if(pathIterator.hasNext()) {
                  Graph pathList = pathIterator.next();
                  firstPath = pathList.getEdges().get(0);
              }
              if(firstPath != null) {
                  Node fromLocation = firstPath.getSource().getNode();
                  String fromName = fromLocation.getId().getId();
                  Node toLocation = firstPath.getTarget().getNode();
                  String toName = toLocation.getId().getId();
                  assertEquals("cabin", fromName, "First path should have been from 'cabin'");
                  assertEquals("forest", toName, "First path should have been to 'forest'");
              }
          }
      } catch (FileNotFoundException fnfe) {
          fail("FileNotFoundException was thrown when attempting to read basic entities file");
      } catch (ParseException pe) {
          fail("ParseException was thrown when attempting to read basic entities file");
      }
  }

}
