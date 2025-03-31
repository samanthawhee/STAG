package edu.uob;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

class BuiltInActionTests {

  private GameServer server;

  // Create a new server _before_ every @Test
  @BeforeEach
  void setup() throws ParserConfigurationException {
      File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
      File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
      server = new GameServer(entitiesFile, actionsFile);
  }

  String sendCommandToServer(String command) {
      // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
      return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
      "Server took too long to respond (probably stuck in an infinite loop)");
  }

  @Test
  void testActionPhrase(){
      // Command without action phrase
      String response1 = sendCommandToServer("sam: potion");
      response1 = response1.toLowerCase();
      assertTrue(response1.contains("no action found"), "Failed to see the error message after attempting a command without an action");

      // Command with one built-in action phrase
      String response2 = sendCommandToServer("sam: get potion");
      response2 = response2.toLowerCase();
      assertTrue(response2.contains("you picked up a potion"), "Failed to see the narration after attempting an action");

      // Command with more than one built-in action phrase
      String response3 = sendCommandToServer("sam: get axe and look");
      response3 = response3.toLowerCase();
      assertTrue(response3.contains("more than one action found"), "Failed to see the error message after attempting a command with more than one action");

      // Command with an action phrase which isn't built-in action and doesn't exist in action file
      String response4 = sendCommandToServer("sam: wipe the table");
      response4 = response4.toLowerCase();
      assertTrue(response4.contains("no action found"), "Failed to see the error message after attempting an action which does not exist");
  }

  @Test
  void testLook() {
      // First look after being in the game
      String response1 = sendCommandToServer("sam: look");
      response1 = response1.toLowerCase();
      System.out.println(response1);

      assertTrue(response1.contains("cabin"), "Did not see the name of the current room in response to look");
      assertTrue(response1.contains("log cabin"), "Did not see a description of the room in response to look");
      assertTrue(response1.contains("magic potion"), "Did not see a description of artifacts in response to look");
      assertTrue(response1.contains("razor sharp axe"), "Did not see a description of artifacts in response to look");
      assertTrue(response1.contains("silver coin"), "Did not see description of artifacts in response to look");
      assertTrue(response1.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
      assertTrue(response1.contains("forest"), "Did not see available paths in response to look");
      assertTrue(response1.contains("sam"), "Did not see existing players to look");

      // Look after going to forest
      String response2 = sendCommandToServer("sam: goto forest");
      response2 = response2.toLowerCase();
      assertTrue(response2.contains("forest"), "Did not see the name of the current room in response to look");
      assertTrue(response2.contains("deep dark forest"), "Did not see a description of the room in response to look");
      assertTrue(response2.contains("rusty old key"), "Did not see a description of artifacts in response to look");
      assertTrue(response2.contains("tall pine tree"), "Did not see description of furniture in response to look");
      assertTrue(response2.contains("cabin"), "Did not see available paths in response to look");
      assertTrue(response2.contains("riverbank"), "Did not see available paths in response to look");
      assertTrue(response2.contains("sam"), "Did not see existing players to look");

      // Look after going to riverbank
      String response3 = sendCommandToServer("sam: goto riverbank");
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
      String response1 = sendCommandToServer("sam: get");
      response1 = response1.toLowerCase();
      assertTrue(response1.contains("artefact not found"), "Did not see the error message after an invalid attempt was made to get it");

      // Get entities in the location
      sendCommandToServer("sam: get potion");
      String response2 = sendCommandToServer("sam: inv");
      response2 = response2.toLowerCase();
      assertTrue(response2.contains("potion"), "Did not see the horn in the inventory after an attempt was made to get it");
      response2 = sendCommandToServer("sam: look");
      response2 = response2.toLowerCase();
      assertFalse(response2.contains("potion"), "Potion is still present in the room after an attempt was made to get it");

      // Get entities in the location
      String response3 = sendCommandToServer("sam: get coin and axe");
      response3 = response3.toLowerCase();
      assertTrue(response3.contains("get one artefact at once"), "Did not see the error message after an invalid attempt was made to get it");

      // Get entities which doesn't exist in the location
      String response4 = sendCommandToServer("sam: get key");
      response4 = response4.toLowerCase();
      assertTrue(response4.contains("artefact not found"), "Did not see the error message after an invalid attempt was made to get it");
  }

  @Test
  void testDrop() {
      // Drop without entity
      String response1 = sendCommandToServer("sam: drop");
      response1 = response1.toLowerCase();
      assertTrue(response1.contains("artefact not found"), "Did not see the error message after an invalid attempt was made to get it");

      // Drop entities in the location
      sendCommandToServer("sam: drop potion");
      String response2 = sendCommandToServer("sam: inv");
      response2 = response2.toLowerCase();
      assertFalse(response2.contains("potion"), "See the horn in the inventory after an attempt was made to drop it");
      response2 = sendCommandToServer("sam: look");
      response2 = response2.toLowerCase();
      assertTrue(response2.contains("potion"), "Potion isn't present in the room after an attempt was made to drop it");

      // Drop over one entity at once
      sendCommandToServer("sam: get coin");
      sendCommandToServer("sam: get axe");
      String response3 = sendCommandToServer("sam: drop coin and axe");
      response3 = response3.toLowerCase();
      assertTrue(response3.contains("drop one artefact at once"), "Did not see the error message after an invalid attempt was made to drop it");

      // Drop entities which doesn't exist in the inv
      String response4 = sendCommandToServer("sam: drop potion");
      response4 = response4.toLowerCase();
      assertTrue(response4.contains("artefact not found"), "Did not see the error message after an invalid attempt was made to drop it");
  }


  @Test
  void testGoto()
  {
      // Go to without destination
      String response1 = sendCommandToServer("sam: goto");
      response1 = response1.toLowerCase();
      assertTrue(response1.contains("location not found"), "Failed to see error message after attempting a command without location");

      // Go to a location which doesn't have a valid path
      String response2 = sendCommandToServer("sam: goto riverbank");
      response2 = response2.toLowerCase();
      assertTrue(response2.contains("path not found"), "Failed to see error message after attempting to a invalid path");

      // Go to a location which doesn't exist in the game
      String response3 = sendCommandToServer("sam: goto playground");
      response3 = response3.toLowerCase();
      assertTrue(response3.contains("location not found"), "Failed to see error message after attempting to a not existing location");

      // Goto the location that you are in
      String response4 = sendCommandToServer("sam: goto cabin");
      response4 = response4.toLowerCase();
      assertTrue(response4.contains("you are already in the location"), "Failed to see error message after attempting to the location which you are currently in");

      // Goto multiple locations
      String response5 = sendCommandToServer("sam: goto riverbank and forest");
      response5 = response5.toLowerCase();
      assertTrue(response5.contains("goto one location at once"), "Failed to see error message after attempting to multiple locations");

      // Go to a location which has a valid path
      sendCommandToServer("sam: goto forest");
      String response6 = sendCommandToServer("sam: look");
      response6 = response6.toLowerCase();
      assertTrue(response6.contains("key"), "Failed attempt to use 'goto' command to move to the forest - there is no key in the current location");
  }

  @Test
  void testInv(){
      sendCommandToServer("sam: get coin");
      sendCommandToServer("sam: get axe");
      String response1 = sendCommandToServer("sam: inv");
      response1 = response1.toLowerCase();
      System.out.println(response1);
      assertTrue(response1.contains("coin"), "Failed to see existing entity in inv");
      assertTrue(response1.contains("axe"), "Failed to see existing entity in inv");

      // Check the entity which is in the player's inv with inventory
      String response2 = sendCommandToServer("sam: inventory");
      response2 = response2.toLowerCase();
      assertTrue(response2.contains("coin"), "Failed to see existing entity in inv");
      assertTrue(response2.contains("axe"), "Failed to see existing entity in inv");

      // Check the entity which is not in the player's inv
      sendCommandToServer("sam: goto forest");
      assertFalse(response2.contains("potion"), "Failed to see the entity which should be in inv");

      // Check with redundant phrase
      String response3 = sendCommandToServer("sam: check inv");
      response3 = response3.toLowerCase();
      assertTrue(response3.contains("execute action with only action phrase"), "Failed to see error message after attempting to check inv with redundant phrase");

      // Check with more than one action phrase
      String response4 = sendCommandToServer("sam: inv inventory");
      response4 = response4.toLowerCase();
      assertTrue(response4.contains("more than one action found"), "Failed to see error message after attempting to check inv with more than one action phrase");
  }
}
