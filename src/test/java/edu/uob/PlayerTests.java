package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PlayerTests {

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
  void testPlayerNameValidation() {
      // Check the player can get into the game while the name contains the element '
      String response1 = this.sendCommandToServer("sam': look");
      response1 = response1.toLowerCase();
      assertTrue(response1.contains("sam'"), "Failed to get into the game while the player's name have '");

      // Check the player can get into the game while the name contains the element -
      String response2 = this.sendCommandToServer("sam-: look");
      response2 = response2.toLowerCase();
      assertTrue(response2.contains("sam-"), "Failed to get into the game while the player's name have '");

      // Check the player can get into the game while the name contains letters
      String response3 = this.sendCommandToServer("sam: look");
      response3 = response3.toLowerCase();
      assertTrue(response3.contains("sam"), "Failed to get into the game while the player's name have '");

      // Check the player can get into the game while the name contains letters
      String response4 = this.sendCommandToServer("s am: look");
      response4 = response4.toLowerCase();
      assertTrue(response4.contains("s am"), "Failed to get into the game while the player's name have '");

      // Check the player can not get into the game while the name contains other symbols
      String response5 = this.sendCommandToServer("sam#@: look");
      response5 = response5.toLowerCase();
      assertTrue(response5.contains("the player name must be letters, space, apostrophes and hyphens."), "Failed to see error message while the player's name is invalid");

      // Check the player can not get into the game while the name contains numbers
      String response6 = this.sendCommandToServer("sam123: look");
      response6 = response6.toLowerCase();
      assertTrue(response6.contains("the player name must be letters, space, apostrophes and hyphens."), "Failed to see error message while the player's name is invalid");

  }

  @Test
  void testMultiplePlayers() {
      // Check can see other players while look
      String response1 = this.sendCommandToServer("sam: look");
      assertTrue(response1.contains("sam"), "Failed to see players while the player is in the location");

      String response2 = this.sendCommandToServer("tim: look");
      assertTrue(response2.contains("sam"), "Failed to see players while the player is in the location");
      assertTrue(response2.contains("tim"), "Failed to see players while the player is in the location");

      // Check show player's name while they goto another location
      this.sendCommandToServer("sam: goto forest");
      String response3 = this.sendCommandToServer("tim: look");
      assertFalse(response3.contains("sam"), "Failed to move players while the player go to forest");
      String response4 = this.sendCommandToServer("sam: look");
      assertFalse(response4.contains("tim"), "Failed to move players while the player go to forest");
  }
}
