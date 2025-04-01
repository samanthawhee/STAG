package edu.uob;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.nio.file.Paths;


class BuiltInActionTests {

  private GameServer server;

  // Create a new server _before_ every @Test
  @BeforeEach
  void setup() throws ParserConfigurationException {
      StringBuilder dotFilePath = new StringBuilder("config");
      dotFilePath.append(File.separator).append("extended-entities.dot");
      StringBuilder xmlFilePath = new StringBuilder("config");
      xmlFilePath.append(File.separator).append("extended-actions.xml");
      File entitiesFile = Paths.get(dotFilePath.toString()).toAbsolutePath().toFile();
      File actionsFile = Paths.get(xmlFilePath.toString()).toAbsolutePath().toFile();
      server = new GameServer(entitiesFile, actionsFile);
  }

  String sendCommandToServer(String command) {
      return server.handleCommand(command);
  }

  @Test
  void testActionPhrase(){
      // Command without action phrase
      String response1 = this.sendCommandToServer("sam: potion");
      response1 = response1.toLowerCase();
      assertTrue(response1.contains("no action found"), "Failed to see the error message after attempting a command without an action");

      // Command with one built-in action phrase
      String response2 = this.sendCommandToServer("sam: get potion");
      response2 = response2.toLowerCase();
      assertTrue(response2.contains("you picked up a potion"), "Failed to see the narration after attempting an action");

      // Command with more than one built-in action phrase
      String response3 = this.sendCommandToServer("sam: get axe and look");
      response3 = response3.toLowerCase();
      assertTrue(response3.contains("more than one action found"), "Failed to see the error message after attempting a command with more than one action");

      // Command with an action phrase which isn't built-in action and doesn't exist in action file
      String response4 = this.sendCommandToServer("sam: wipe the table");
      response4 = response4.toLowerCase();
      assertTrue(response4.contains("no action found"), "Failed to see the error message after attempting an action which does not exist");
  }

  @Test
  void testLook() {
      // First look after being in the game
      String response1 = this.sendCommandToServer("sam: look");
      response1 = response1.toLowerCase();

      assertTrue(response1.contains("cabin"), "Did not see the name of the current room in response to look");
      assertTrue(response1.contains("log cabin"), "Did not see a description of the room in response to look");
      assertTrue(response1.contains("magic potion"), "Did not see a description of artifacts in response to look");
      assertTrue(response1.contains("razor sharp axe"), "Did not see a description of artifacts in response to look");
      assertTrue(response1.contains("silver coin"), "Did not see description of artifacts in response to look");
      assertTrue(response1.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
      assertTrue(response1.contains("forest"), "Did not see available paths in response to look");
      assertTrue(response1.contains("sam"), "Did not see existing players to look");

      // Look after going to forest
      String response2 = this.sendCommandToServer("sam: goto forest");
      response2 = response2.toLowerCase();
      assertTrue(response2.contains("forest"), "Did not see the name of the current room in response to look");
      assertTrue(response2.contains("deep dark forest"), "Did not see a description of the room in response to look");
      assertTrue(response2.contains("rusty old key"), "Did not see a description of artifacts in response to look");
      assertTrue(response2.contains("tall pine tree"), "Did not see description of furniture in response to look");
      assertTrue(response2.contains("cabin"), "Did not see available paths in response to look");
      assertTrue(response2.contains("riverbank"), "Did not see available paths in response to look");
      assertTrue(response2.contains("sam"), "Did not see existing players to look");

      // Look after going to riverbank
      String response3 = this.sendCommandToServer("sam: goto riverbank");
      response3 = response3.toLowerCase();
      assertTrue(response3.contains("riverbank"), "Did not see the name of the current room in response to look");
      assertTrue(response3.contains("grassy riverbank"), "Did not see a description of the room in response to look");
      assertTrue(response3.contains("old brass horn"), "Did not see a description of artifacts in response to look");
      assertTrue(response3.contains("fast flowing river"), "Did not see description of furniture in response to look");
      assertTrue(response3.contains("forest"), "Did not see available paths in response to look");
      assertTrue(response3.contains("sam"), "Did not see existing players to look");
  }

  @Test
  void testGet() {
      // Get without entity
      String response1 = this.sendCommandToServer("sam: get");
      response1 = response1.toLowerCase();
      assertTrue(response1.contains("artefact not found"), "Did not see the error message after an invalid attempt was made to get it");

      // Get entities in the location
      this.sendCommandToServer("sam: get potion");
      String response2 = this.sendCommandToServer("sam: inv");
      response2 = response2.toLowerCase();
      assertTrue(response2.contains("potion"), "Did not see the horn in the inventory after an attempt was made to get it");
      response2 = this.sendCommandToServer("sam: look");
      response2 = response2.toLowerCase();
      assertFalse(response2.contains("potion"), "Potion is still present in the room after an attempt was made to get it");

      // Get entities in the location
      String response3 = this.sendCommandToServer("sam: get coin and axe");
      response3 = response3.toLowerCase();
      assertTrue(response3.contains("get one artefact at once"), "Did not see the error message after an invalid attempt was made to get it");

      // Get entities which doesn't exist in the location
      String response4 = this.sendCommandToServer("sam: get key");
      response4 = response4.toLowerCase();
      assertTrue(response4.contains("artefact not found"), "Did not see the error message after an invalid attempt was made to get it");
  }

  @Test
  void testDrop() {
      // Drop without entity
      String response1 = this.sendCommandToServer("sam: drop");
      response1 = response1.toLowerCase();
      assertTrue(response1.contains("artefact not found"), "Did not see the error message after an invalid attempt was made to get it");

      // Drop entities in the location
      this.sendCommandToServer("sam: drop potion");
      String response2 = this.sendCommandToServer("sam: inv");
      response2 = response2.toLowerCase();
      assertFalse(response2.contains("potion"), "See the horn in the inventory after an attempt was made to drop it");
      response2 = this.sendCommandToServer("sam: look");
      response2 = response2.toLowerCase();
      assertTrue(response2.contains("potion"), "Potion isn't present in the room after an attempt was made to drop it");

      // Drop over one entity at once
      this.sendCommandToServer("sam: get coin");
      this.sendCommandToServer("sam: get axe");
      String response3 = this.sendCommandToServer("sam: drop coin and axe");
      response3 = response3.toLowerCase();
      assertTrue(response3.contains("drop one artefact at once"), "Did not see the error message after an invalid attempt was made to drop it");

      // Drop entities which doesn't exist in the inv
      String response4 = this.sendCommandToServer("sam: drop potion");
      response4 = response4.toLowerCase();
      assertTrue(response4.contains("artefact not found"), "Did not see the error message after an invalid attempt was made to drop it");
  }


  @Test
  void testGoto()
  {
      // Go to without destination
      String response1 = this.sendCommandToServer("sam: goto");
      response1 = response1.toLowerCase();
      assertTrue(response1.contains("location not found"), "Failed to see error message after attempting a command without location");

      // Go to a location which doesn't have a valid path
      String response2 = this.sendCommandToServer("sam: goto riverbank");
      response2 = response2.toLowerCase();
      assertTrue(response2.contains("path not found"), "Failed to see error message after attempting to a invalid path");

      // Go to a location which doesn't exist in the game
      String response3 = this.sendCommandToServer("sam: goto playground");
      response3 = response3.toLowerCase();
      assertTrue(response3.contains("location not found"), "Failed to see error message after attempting to a not existing location");

      // Goto the location that you are in
      String response4 = this.sendCommandToServer("sam: goto cabin");
      response4 = response4.toLowerCase();
      assertTrue(response4.contains("you are already in the location"), "Failed to see error message after attempting to the location which you are currently in");

      // Goto multiple locations
      String response5 = this.sendCommandToServer("sam: goto riverbank and forest");
      response5 = response5.toLowerCase();
      assertTrue(response5.contains("goto one location at once"), "Failed to see error message after attempting to multiple locations");

      // Go to a location which has a valid path
      this.sendCommandToServer("sam: goto forest");
      String response6 = this.sendCommandToServer("sam: look");
      response6 = response6.toLowerCase();
      assertTrue(response6.contains("key"), "Failed attempt to use 'goto' command to move to the forest - there is no key in the current location");

      // Go to more than 1 location and there is 1 which doesn't exist in the location list
      this.sendCommandToServer("sam: goto forest and playground");
      String response7 = this.sendCommandToServer("sam: look");
      response7 = response7.toLowerCase();
      assertTrue(response7.contains("key"), "Failed attempt to use 'goto' command to move to the forest - there is no key in the current location");
  }

  @Test
  void testInv(){
      this.sendCommandToServer("sam: get coin");
      this.sendCommandToServer("sam: get axe");
      String response1 = this.sendCommandToServer("sam: inv");
      response1 = response1.toLowerCase();
      assertTrue(response1.contains("coin"), "Failed to see existing entity in inv");
      assertTrue(response1.contains("axe"), "Failed to see existing entity in inv");

      // Check the entity which is in the player's inv with inventory
      String response2 = this.sendCommandToServer("sam: inventory");
      response2 = response2.toLowerCase();
      assertTrue(response2.contains("coin"), "Failed to see existing entity in inv");
      assertTrue(response2.contains("axe"), "Failed to see existing entity in inv");

      // Check the entity which is not in the player's inv
      this.sendCommandToServer("sam: goto forest");
      assertFalse(response2.contains("potion"), "Failed to see the entity which should be in inv");

      // Check with redundant phrase
      String response3 = this.sendCommandToServer("sam: check inv");
      response3 = response3.toLowerCase();
      assertTrue(response3.contains("execute action with only action phrase"), "Failed to see error message after attempting to check inv with redundant phrase");

      // Check with more than one action phrase
      String response4 = this.sendCommandToServer("sam: inv inventory");
      response4 = response4.toLowerCase();
      assertTrue(response4.contains("more than one action found"), "Failed to see error message after attempting to check inv with more than one action phrase");
  }

  @Test
    void TestHealth(){
      // Check health decrement 1 after being attacked
      this.sendCommandToServer("sam: get coin");
      this.sendCommandToServer("sam: get potion");
      this.sendCommandToServer("sam: goto forest");
      this.sendCommandToServer("sam: get key");
      this.sendCommandToServer("sam: goto cabin");
      this.sendCommandToServer("sam: open with key");
      this.sendCommandToServer("sam: goto cellar");
      this.sendCommandToServer("sam: attack elf");
      String response1 = this.sendCommandToServer("sam: health");
      assertTrue(response1.contains("2"), "Failed attempt to see correct health after attacking an elf");

      // Check the reset message is sent after the player is dead
      this.sendCommandToServer("sam: attack elf");
      String response2 = this.sendCommandToServer("sam: attack elf");
      assertTrue(response2.contains("died"), "Failed attempt to see correct health after the player died");

      // Check health is 3 after the player is dead
      String response3 = this.sendCommandToServer("sam: health");
      assertTrue(response3.contains("3"), "Failed attempt to see correct health after the player died");

      // Check the player is sent to the start location after the player is dead
      String response4 = this.sendCommandToServer("sam: look");
      assertTrue(response4.contains("cabin"), "Failed attempt to see correct start location after the player died");

      // Check the entities is located in the location where the player died
      this.sendCommandToServer("sam: goto cellar");
      String response5 = this.sendCommandToServer("sam: look");
      assertTrue(response5.contains("coin"), "Failed attempt to see the entities in the location after the player died");

      // Check the inv is empty after the player died
      String response6 = this.sendCommandToServer("sam: inv");
      assertFalse(response6.contains("coin"), "Failed attempt to see that in player's inv after the player died");

      //Check health increment 1 after being attacked
      this.sendCommandToServer("sam: attack elf");
      this.sendCommandToServer("sam: drink potion");
      String response7 = this.sendCommandToServer("sam: health");
      assertTrue(response7.contains("3"), "Failed attempt to see correct health after drinking potion");
  }
}
