package edu.uob;

import org.junit.jupiter.api.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OtherActionTests {

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
        return  server.handleCommand(command);
    }

  @Test
  void testActionPhrase() {
      // Command without an action phrase
      this.sendCommandToServer("sam: goto forest");
      this.sendCommandToServer("sam: get key");
      this.sendCommandToServer("sam: goto cabin");
      String response1 = this.sendCommandToServer("sam: key");
      response1 = response1.toLowerCase();
      assertTrue(response1.contains("no action found"), "Failed attempt to see the error message after sending a command without an action");

      // Command with an action phrase which doesn't exit in the action file
      String response2 = this.sendCommandToServer("sam: use key");
      response2 = response2.toLowerCase();
      assertTrue(response2.contains("no action found"), "Failed attempt to see the error message after sending an action which doesn't exist");

      // Command with multiple action phrases from the same action
      String response3 = this.sendCommandToServer("sam: open unlock with key");
      response3 = response3.toLowerCase();
      assertTrue(response3.contains("more than one action found"), "Failed attempt to see the error message after sending multiple action phrases");

      // Command with multiple action phrases from different actions
      String response4 = this.sendCommandToServer("sam: open drink with key");
      response4 = response4.toLowerCase();
      assertTrue(response4.contains("more than one action found"), "Failed attempt to see the error message after sending multiple action phrases");

      // Command with multiple action phrases one of them doesn't exit in the action file
      String response5 = this.sendCommandToServer("sam: open use with key");
      response5 = response5.toLowerCase();
      assertTrue(response5.contains("you unlock the door and see steps leading down into a cellar"), "Failed attempt to see the narration after sending command");

      // Command with multiple action phrases all of them don't exit in the action file
      this.sendCommandToServer("sam: get axe");
      this.sendCommandToServer("sam: goto forest");
      String response6 = this.sendCommandToServer("sam: use the axe to snippet the tree");
      response6 = response6.toLowerCase();
      assertTrue(response6.contains("no action found"), "Failed attempt to see the error message after sending actions which don't exist");
  }

  @Test
  void testSubjectPhrase() {
      // Command with a subject which is not in the location nor player's inv
      String response1 = this.sendCommandToServer("sam: open with key");
      response1 = response1.toLowerCase();
      assertTrue(response1.contains("the entities not all available"), "Failed attempt to see the error message after sending a command without all entities are not in the location nor the player's inv");

      // Command with a subject which doesn't exit in the entity file
      String response2 = this.sendCommandToServer("sam: open with can");
      response2 = response2.toLowerCase();
      assertTrue(response2.contains("entity not found"), "Failed attempt to see the error message after sending a command without a valid entity");

      // Command without subjects while the subject is in the player's inv
      this.sendCommandToServer("sam: goto forest");
      this.sendCommandToServer("sam: get key");
      this.sendCommandToServer("sam: goto cabin");
      String response3 = this.sendCommandToServer("sam: open");
      response3 = response3.toLowerCase();
      assertTrue(response3.contains("entity not found"), "Failed attempt to see the error message after sending a command without an entity");

      // Command with multiple subjects one of them isn't in the location nor player's inv
      String response4 = this.sendCommandToServer("sam: cut tree with axe");
      response4 = response4.toLowerCase();
      assertTrue(response4.contains("the entities not all available"), "Failed attempt to see the error message while one of the entities is not in the location nor the player's inv");

      // Command with multiple subjects all of them aren't in the location nor player's inv
      String response5 = this.sendCommandToServer("sam: dig with shovel");
      response5 = response5.toLowerCase();
      assertTrue(response5.contains("the entities not all available"), "Failed attempt to see the error message while the entities are not in the location nor the player's inv");

      // Command with a valid subject
      String response6 = this.sendCommandToServer("sam: drink potion");
      response6 = response6.toLowerCase();
      assertTrue(response6.contains("you drink the potion and your health improves"), "Failed attempt to see the narration while sending the command with valid a subject");

      // Command with multiple valid subjects
      this.sendCommandToServer("sam: get axe");
      this.sendCommandToServer("sam: goto forest");
      String response7 = this.sendCommandToServer("sam: cut tree with axe");
      response7 = response7.toLowerCase();
      assertTrue(response7.contains("you cut down the tree with the axe"), "Failed attempt to see the narration while sending the command with valid subjects");

  }

  @Test
  void testConsumedPhrase() {
      // Check the consumed health decremented by 1
      this.sendCommandToServer("sam: get coin");
      this.sendCommandToServer("sam: get axe");
      this.sendCommandToServer("sam: get potion");
      this.sendCommandToServer("sam: goto forest");
      this.sendCommandToServer("sam: get key");
      this.sendCommandToServer("sam: goto cabin");
      this.sendCommandToServer("sam: open with key");
      this.sendCommandToServer("sam: goto cellar");
      this.sendCommandToServer("sam: attack elf");
      String response1 = this.sendCommandToServer("sam: health");
      assertTrue(response1.contains("2"), "Failed attempt to see correct health after attacking an elf");

      // Check the consumed entity is not in the location or player's inv
      this.sendCommandToServer("sam: pay with coin");
      String response2 = this.sendCommandToServer("sam: look");
      assertFalse(response2.contains("coin"), "Failed allocate the entity after being consumed");
      String response3 = this.sendCommandToServer("sam: inv");
      assertFalse(response3.contains("coin"), "Failed allocate the entity after being consumed");
  }

  @Test
  void testProducedPhrase() {
      // Check health maintains 3 if health is 3
      this.sendCommandToServer("sam: get coin");
      this.sendCommandToServer("sam: get axe");
      this.sendCommandToServer("sam: get potion");
      this.sendCommandToServer("sam: drink potion");
      String response1 = this.sendCommandToServer("sam: health");
      assertTrue(response1.contains("3"), "Failed to see correct health after drinking a potion while health is 3");

      // Check the produced location is created the path from the current one to determine one
      this.sendCommandToServer("sam: goto forest");
      this.sendCommandToServer("sam: get key");
      this.sendCommandToServer("sam: goto cabin");
      this.sendCommandToServer("sam: open with key");
      String response2 = this.sendCommandToServer("sam: look");
      assertTrue(response2.contains("cellar"), "Failed to see the produced path");

      // Check the produced health decremented by 1
      this.sendCommandToServer("sam: goto cellar");
      this.sendCommandToServer("sam: attack elf");
      String response3 = this.sendCommandToServer("sam: health");
      assertTrue(response3.contains("2"), "Failed attempt to see correct health after attacking an elf");

      // Check the produced entity is in the current location
      this.sendCommandToServer("sam: pay with coin");
      String response4 = this.sendCommandToServer("sam: look");
      assertTrue(response4.contains("shovel"), "Failed to see produced entity");

  }

  @Test
  void testNarrationPhrase() {
      // Check the narration is belonged to the action
      this.sendCommandToServer("sam: goto forest");
      this.sendCommandToServer("sam: get key");
      this.sendCommandToServer("sam: goto cabin");
      String response1 = this.sendCommandToServer("sam: open with key");
      response1 = response1.toLowerCase();
      assertTrue(response1.contains("you unlock the door and see steps leading down into a cellar"), "Failed to see produced entity");
  }
}
