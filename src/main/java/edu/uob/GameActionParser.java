package edu.uob;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;

public class GameActionParser {
    private final File filePath;
    private final GameStateAccessor gameStateAccessor;

    public GameActionParser(File actionsFile, GameStateAccessor gameStateAccessor) {
        this.gameStateAccessor = gameStateAccessor;
        this.filePath = actionsFile;
    }

    public void parseActionFile() throws ParserConfigurationException {
        if(this.filePath.exists() && this.filePath.isFile()
                && this.filePath.canRead() && this.filePath != null) {
            Document document = this.parseXmlFile();
            this.constructActions(document);
        }
    }

    private Document parseXmlFile() throws ParserConfigurationException {
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);

            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(this.filePath);
            document.getDocumentElement().normalize();
            return document;

        } catch (IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }



    private void constructActions(Document document) {
        Element actions = document.getDocumentElement();
        NodeList actionList = actions.getElementsByTagName("action");

        for(int i = 0; i < actionList.getLength(); i++){

            Node currentAction = actionList.item(i);

            if(currentAction.getNodeType() == Node.ELEMENT_NODE
                    && currentAction.getNodeName().equals("action")){
                Element actionDetails = (Element) currentAction;

                GameAction action = this.addActions (getNarration(actionDetails));

                this.insertActionTrigger(actionDetails, action);
                this.insertActionOthers("subjects", actionDetails, action);
                this.insertActionOthers("consumed", actionDetails, action);
                this.insertActionOthers("produced", actionDetails, action);
            }
        }
    }

    private void insertActionTrigger(Element actionDetails, GameAction action){
        NodeList attribute = actionDetails.getElementsByTagName("triggers");
        Node attributeNode = attribute.item(0);
        if(attributeNode.getNodeType() == Node.ELEMENT_NODE){
            Element attributeItems = (Element) attributeNode;
            this.addTrigger(attributeItems, action);
        }
    }

    private void insertActionOthers(String attributeName, Element actionDetails, GameAction action){
        NodeList attribute = actionDetails.getElementsByTagName(attributeName);
        Node attributeNode = attribute.item(0);
        if(attributeNode.getNodeType() == Node.ELEMENT_NODE){
            Element attributeItems = (Element) attributeNode;
            this.addOthers(attributeName, attributeItems, action);
        }
    }

    private void addTrigger(Element attributeItems, GameAction action){
        NodeList keyphraseList = attributeItems.getElementsByTagName("keyphrase");

        for(int i = 0; i < keyphraseList.getLength(); i++){
            String currentKeyphrase = keyphraseList.item(i).getTextContent();
            action.addTrigger(currentKeyphrase);
            this.gameStateAccessor.getActionList().addKeyPhrase(currentKeyphrase);
        }
    }

    private void addOthers(String attributeName, Element attributeItems, GameAction action){
        NodeList entityList = attributeItems.getElementsByTagName("entity");

        for(int i = 0; i < entityList.getLength(); i++){
            String currentEntity = entityList.item(i).getTextContent();
            switch(attributeName){
                case "subjects":
                    action.addSubjects(currentEntity);
                    break;

                case "consumed":
                    action.addConsumed(currentEntity);
                    break;

                case "produced":
                    action.addProduced(currentEntity);
                    break;

                default:
                    break;
            }
        }
    }

    private String getNarration(Element actionDetails){
        NodeList narration = actionDetails.getElementsByTagName("narration");
        if(narration.getLength() > 0){
            Node firstNode = narration.item(0);
            Node firstChild = firstNode.getFirstChild();
            return firstChild.getNodeValue();
        }
        return "";
    }

    private GameAction addActions (String narration) {
        GameAction action = new GameAction(narration);
        this.gameStateAccessor.getActionList().addGameAction(action);
        return action;
    }

}
