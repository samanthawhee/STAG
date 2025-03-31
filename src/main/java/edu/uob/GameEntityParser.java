package edu.uob;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;

public class GameEntityParser {
    private final File filePath;
    private final GameStateAccessor gameStateAccessor;

    public GameEntityParser(File entitiesFile, GameStateAccessor gameStateAccessor) {

        this.filePath = entitiesFile;
        this.gameStateAccessor = gameStateAccessor;
    }

    public void parseEntitiesFile() {
        this.constructEntity();
        this.constructPathList();
    }

    private LinkedHashSet<Graph> getLayoutGraph() {
        try{
            String filePath = this.filePath.getAbsolutePath();
            Reader reader = new FileReader(filePath);

            Parser parser = new Parser();
            parser.parse(reader);

            LinkedHashSet<Graph> layoutGraph = new LinkedHashSet<>();
            layoutGraph.addAll(parser.getGraphs());
            return layoutGraph;

        }catch(Exception e) {
            throw new RuntimeException(e);
        }

    }

    private LinkedHashSet<Graph> getCertainGraph(LinkedHashSet<Graph> layoutGraph, String graphType){

        LinkedHashSet<Graph> locationGraph = new LinkedHashSet<>();

        for(Graph graph : layoutGraph){
            Iterator<Graph> subIterator = graph.getSubgraphs().iterator();

            while(subIterator.hasNext()) {
                Graph subGraph = subIterator.next();
                String subgraphName;

                if(subGraph.getId() != null) {
                    subgraphName = subGraph.getId().getId();
                }else{
                    subgraphName = null;
                }

                if(subgraphName.equalsIgnoreCase(graphType)){
                    locationGraph.add(subGraph);
                }
            }
        }
        return locationGraph;
    }

    private LinkedHashSet<Graph> getLocationGraphs(){
        LinkedHashSet<Graph> layoutGraph = this.getLayoutGraph();
        LinkedHashSet<Graph> locationsDiGraph = this.getCertainGraph(layoutGraph, "locations");
        LinkedHashSet<Graph> locationsGraph = new LinkedHashSet<>();

        for(Graph graph : locationsDiGraph){
            locationsGraph.addAll(graph.getSubgraphs());
        }

        return locationsGraph;
    }

    private LinkedHashSet<Graph> getPathGraphs(){
        LinkedHashSet<Graph> layoutGraph = this.getLayoutGraph();
        return this.getCertainGraph(layoutGraph, "paths");
    }

    private void constructPathList(){
        LinkedHashSet<Edge> pathEdges = this.getPathEdges();
        this.getPathSourceAndTarget(pathEdges);
    }

    private LinkedHashSet<Edge> getPathEdges(){
        LinkedHashSet<Graph> pathDiGraph = this.getPathGraphs();
        LinkedHashSet<Edge> pathEdges = new LinkedHashSet<>();

        Iterator<Graph> graphIterator = pathDiGraph.iterator();
        while(graphIterator.hasNext()){
            Iterator<Edge> edgeIterator = graphIterator.next().getEdges().iterator();
            while(edgeIterator.hasNext()){
                Edge currentEdge = edgeIterator.next();
                pathEdges.add(currentEdge);
            }
        }
        return pathEdges;
    }

    private void getPathSourceAndTarget(LinkedHashSet<Edge> pathEdges){
        for (Edge currentEdge : pathEdges) {
            PortNode currentSourceNode = currentEdge.getSource();
            PortNode currentTargetNode = currentEdge.getTarget();
            String sourceLocation = currentSourceNode.getNode().getId().getId();
            String targetLocation = currentTargetNode.getNode().getId().getId();
            this.gameStateAccessor.getPathList().addPath(sourceLocation, targetLocation);
        }
    }

    private void constructEntity(){
        LinkedHashSet<Graph> locationsGraph = this.getLocationGraphs();
        LinkedHashSet<String> locationNames = new LinkedHashSet<>();
        LinkedHashSet<String> locationInfos = new LinkedHashSet<>();

        int locationsCount = locationsGraph.size();
        if(locationsCount != 0){
            this.addLocationAndInfo(locationsGraph, locationNames, locationInfos);
            this.setStartLocation(locationNames);
            this.checkHasStoreroom(locationNames);
            this.addLocations(locationNames);
            this.addLocationInfo(locationNames, locationInfos);
            this.insertEntity(locationsGraph);
        }
    }

    private void addLocationAndInfo(LinkedHashSet<Graph> locationsGraph, LinkedHashSet<String> locationNames, LinkedHashSet<String> locationInfos){
        Iterator<Graph> iterator01 = locationsGraph.iterator();
        while(iterator01.hasNext()){
            Graph location = iterator01.next();

            Iterator<Node> nodeIterator = location.getNodes(true).iterator();
            if(nodeIterator.hasNext()){
                Node locationNode = nodeIterator.next();
                String Name = locationNode.getId().getId();
                locationNames.add(Name);
                String info = locationNode.getAttribute("description");
                locationInfos.add(info);
            }
        }
    }

    private void setStartLocation(LinkedHashSet<String> locationNames){
        if(locationNames.size() != 0){
            Iterator<String> iterator = locationNames.iterator();
            if(iterator.hasNext()){
                String fistLocation = iterator.next();
                this.gameStateAccessor.getLocationList().setStartLocation(fistLocation);
            }
        }

    }

    private void checkHasStoreroom(LinkedHashSet<String> locationNames){
        boolean hasStoreroom = false;

        for(String locationName : locationNames){
            if(locationName.equalsIgnoreCase("storeroom")){
                hasStoreroom = true;
            }
        }

        if(!hasStoreroom){
            locationNames.add("storeroom");
        }
    }

    // Instantiate new objects of locations and store into locationList
    private void addLocations(LinkedHashSet<String> locationNames){
        for(String locationName : locationNames){
            GameLocation location = new GameLocation();
            this.gameStateAccessor.getLocationList().addGameLocation(locationName, location);
            this.gameStateAccessor.getLocationList().addLocation(locationName);
        }
    }

    // Add the info of the locations
    private void addLocationInfo(LinkedHashSet<String> locationNames, LinkedHashSet<String> locationInfos){
        int infosCount = 0;
        int locationCount = 0;
        for(String locationName : locationNames){
            GameLocation location = this.gameStateAccessor.getLocationList().getGameLocation(locationName);
            location.addLocationInfo(locationName);
            Iterator<String> infoIterator = locationInfos.iterator();
            while(infoIterator.hasNext()){
                if(locationCount == infosCount){
                    location.addLocationInfoAttr(locationName, "description", this.getStringFromHasSet(locationInfos, infosCount));
                    infosCount++;
                }else{
                    break;
                }
            }
            locationCount++;
        }
    }

    private String getStringFromHasSet(HashSet<String> hashSet, int index){
        if(index < 0 || index >= hashSet.size()){
            return null;
        }
        Iterator<String> iterator = hashSet.iterator();
        int currentIndex = 0;
        while(iterator.hasNext()){
            String item = iterator.next();
            if(currentIndex == index){
                return item;
            }
            currentIndex++;
        }
        return null;
    }

    private void insertEntity(LinkedHashSet<Graph> locationsGraph){

        for(Graph singleLocation : locationsGraph){
            Iterator<Node> nodeIterator = singleLocation.getNodes(true).iterator();
            String locationName = null;

            if(nodeIterator.hasNext()){
                Node locationNode = nodeIterator.next();
                locationName = locationNode.getId().getId();
            }
            GameLocation location = this.gameStateAccessor.getLocationList().getGameLocation(locationName);
            Iterator<Graph> entityTypesGraph = singleLocation.getSubgraphs().iterator();

            while(entityTypesGraph.hasNext()){
                Graph currentEntityType = entityTypesGraph.next();
                String entityType = currentEntityType.getId().getId();
                Iterator<Node> entityNode = currentEntityType.getNodes(true).iterator();

                if(location != null){
                    while(entityNode.hasNext()){
                        Node currentEntity = entityNode.next();
                        String entityName = currentEntity.getId().getId();
                        String entityInfo = currentEntity.getAttribute("description");
                        location.addEntity(entityName);

                        switch (entityType){
                            case "characters":
                                location.addCharacter(entityName);
                                location.addCharacterAttr(entityName, "description", entityInfo);
                                location.addCharacterAttr(entityName, "property", "character");
                                break;
                            case "furniture":
                                location.addFurniture(entityName);
                                location.addFurnitureAttr(entityName, "description", entityInfo);
                                location.addFurnitureAttr(entityName, "property", "furniture");
                                break;
                            case "artefacts":
                                location.addArtefact(entityName);
                                location.addArtefactAttr(entityName, "description", entityInfo);
                                location.addArtefactAttr(entityName, "property", "artefact");
                                this.gameStateAccessor.getLocationList().addArtefact(entityName);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }
}
