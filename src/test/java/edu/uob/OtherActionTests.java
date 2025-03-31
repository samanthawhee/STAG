package edu.uob;

import org.junit.jupiter.api.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OtherActionTests {

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
  @Order(1)
  void testActionPhrase() {
      // Command without an action phrase
      // Command with a wrong action phrase
      // Command with an action phrase which doesn't exit in the action file
      // Command with multiple action phrases
      // Command with multiple action phrases one of them doesn't exit in the action file
      // Command with multiple action phrases all of them don't exit in the action file
  }

  @Test
  @Order(2)
  void testSubjectPhrase() {
      // Command without entity
      // Command with an entity which is not in the location nor player's inv
      // Command with an entity which doesn't exit in the entity file
      // Command with multiple entities phrases one of them isn't in the location
      // Command with multiple entities phrases all of them isn't in the location
      // Command with multiple entities phrases one of them doesn't exit in the entity file
      // Command with multiple entities phrases all of them don't exit in the entity file
      // Command with multiple entities phrases one of them isn't in the location, one of them doesn't exit in the entity file
      // Command with a valid entity
      // Command with multiple valid entities
  }

  @Test
  @Order(3)
  void testConsumedPhrase() {
      // Check the consumed health decremented by 1
      // Check resetting player if health is 0
      // Check the consumed location is remove the path from the current one to determine one
      // Check the consumed entity is not in the location or player's inv
  }

  @Test
  @Order(4)
  void testProducedPhrase() {
      // Check the produced health decremented by 1
      // Check health maintains 3 if health is 3
      // Check the produced location is created the path from the current one to determine one
      // Check the produced entity is in the current location
  }

  @Test
  @Order(6)
  void testNarrationPhrase() {
      // Check the narration is belonged to the action
  }
}
