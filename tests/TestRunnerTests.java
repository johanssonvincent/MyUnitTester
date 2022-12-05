/**
 * @author Vincent Johansson (dv14vjn@cs.umu.se]
 * @since 2022-11-10
 */

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import se.umu.cs.unittest.TestClass;

import javax.swing.*;

public class TestHandlerTests {
    private MyInt var;
    private ProgramGUI testUI;

    @Test
    public void testRunInputTest() {
        testUI.textInput.setText("Test1");
        assertDoesNotThrow(()->testUI.runInput.doClick());
    }

    @Test
    public void testDoesNotExist() {
        testUI.textInput.setText("Test2");
        SwingUtilities.invokeLater(()->testUI.runInput.doClick());
        assertEquals("No test named Test2 found", testUI.textOutput.getText());
    }

    @BeforeEach
    public void setUp() {
        var = new MyInt();

    }

    @AfterEach
    public void tearDOwn() {
        var = null;
        testUI = null;
    }
}
