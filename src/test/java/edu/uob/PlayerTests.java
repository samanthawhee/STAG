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
  void testPlayerNameValidation() {
      // Check the player can get into the game while the name contains the element '
      String response1 = sendCommandToServer("sam': look");
      response1 = response1.toLowerCase();
      assertTrue(response1.contains("sam'"), "Failed to get into the game while the player's name have '");

      // Check the player can get into the game while the name contains the element -
      String response2 = sendCommandToServer("sam-: look");
      response2 = response2.toLowerCase();
      assertTrue(response2.contains("sam-"), "Failed to get into the game while the player's name have '");

      // Check the player can get into the game while the name contains letters
      String response3 = sendCommandToServer("sam: look");
      response3 = response3.toLowerCase();
      assertTrue(response3.contains("sam"), "Failed to get into the game while the player's name have '");

      // Check the player can get into the game while the name contains letters
      String response4 = sendCommandToServer("s am: look");
      response4 = response4.toLowerCase();
      assertTrue(response4.contains("s am"), "Failed to get into the game while the player's name have '");

      // Check the player can not get into the game while the name contains other symbols
      String response5 = sendCommandToServer("sam#@: look");
      response5 = response5.toLowerCase();
      assertTrue(response5.contains("the player name must be letters, space, apostrophes and hyphens."), "Failed to see error message while the player's name is invalid");

      // Check the player can not get into the game while the name contains numbers
      String response6 = sendCommandToServer("sam123: look");
      response6 = response6.toLowerCase();
      assertTrue(response6.contains("the player name must be letters, space, apostrophes and hyphens."), "Failed to see error message while the player's name is invalid");

  }

  @Test
  void testMultiplePlayers() {
      // Check can see other players while look
      String response1 = sendCommandToServer("sam: look");
      assertTrue(response1.contains("sam"), "Failed to see players while the player is in the location");

      String response2 = sendCommandToServer("tim: look");
      assertTrue(response2.contains("sam"), "Failed to see players while the player is in the location");
      assertTrue(response2.contains("tim"), "Failed to see players while the player is in the location");

      // Check show player's name while they goto another location
      sendCommandToServer("sam: goto forest");
      String response3 = sendCommandToServer("tim: look");
      assertFalse(response3.contains("sam"), "Failed to move players while the player go to forest");
      String response4 = sendCommandToServer("sam: look");
      assertFalse(response4.contains("tim"), "Failed to move players while the player go to forest");
  }
}
