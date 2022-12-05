/**
 * @author Vincent Johansson (dv14vjn@cs.umu.se]
 * @since 2022-11-10
 */

import javax.swing.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * TestHandler
 * Handles the execution of specified tests
 */
public class TestRunner extends SwingWorker<List<String>, String> {

    /* Variable declarations */
    public JTextField input;
    public JTextArea output;
    private int success = 0;
    private int failed = 0;
    private final List<String> results;

    /**
     * TestRunner constructor
     * @param input JTextField containing user input
     * @param output jTextArea where program output will be shown
     */
    public TestRunner(final JTextField input, final JTextArea output){
        this.input = input;
        this.output = output;
        results = new ArrayList<>();
    }

    /**
     * Overrides SwingWorkers doInBackground method
     * All logic for background processing of test execution is handled in this method
     * @return
     */
    @Override
    public List<String> doInBackground() {

        /* Check if input is empty*/
        if (Objects.equals(input.getText(), "")){
            output.append("No test was specified, please try again\n");
            return results;
        }

        /* local variable declaration */
        Method m;
        TestConfig testConfig = null;
        Class<?> returnType;

        /* Setup configuration for tests */
        try {
            testConfig = new TestConfig(input.getText());
        } catch (ClassNotFoundException e) {
            output.append("No test named " + input.getText() + " found\n");
            return results;
        } catch (InvocationTargetException e) {
            output.append(String.valueOf(e.getTargetException()));
            return results;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            output.append("Class object from" + getClass().getName() + "could not be instantiated\n");
            return results;
        } catch (IllegalAccessException e) {
            output.append("IllegalAccessException when trying to generate configuration");
        }

        /* Run methods from class */
        assert testConfig != null;
        for (Method method : testConfig.methods) {
            m = method;
            returnType = m.getReturnType();

            if (m.getName().equals("setUp") | m.getName().equals("tearDown")) {
                continue;
            }
            if (returnType.getName().equals("boolean")) {
                try {
                    /* Use setUp & tearDown methods if they exist */
                    if (testConfig.setUp != null && testConfig.tearDown != null) {
                        testConfig.setUp.invoke(testConfig.testObj);
                        if(runTest(testConfig.testObj, m)){
                            success++;
                        }else{
                            failed++;
                        }
                        testConfig.tearDown.invoke(testConfig.testObj);
                    }else{
                        if(runTest(testConfig.testObj, m)){
                            success++;
                        }else{
                            failed++;
                        }
                    }
                } catch (InvocationTargetException e) {
                    String[] shortCause = String.valueOf(e.getCause()).split(":");
                    process(m.getName() + ": FAIL Generated a " + shortCause[0] + "\n");
                    failed++;
                } catch (NullPointerException e) {
                    process(m.getName() + ": FAIL Generated a java.lang.NullPointerException\n");
                    failed++;
                } catch (IllegalAccessException e) {
                    process(m.getName() + ": FAIL Generated a java.lang.IllegalAccessException\n");
                    failed++;
                }
            }
        }

        results.add("\n" + success + " tests succeeded");
        results.add(failed + " tests failed.\n");

        return results;
    }

    /**
     * Appends given string to output after a part of a test is finished
     * @param string message to be appended to output
     */
    protected void process(String string) {
        output.append(string);
    }

    /**
     * Runs specified test method from the main test class
     * @param testObj class objective of current test class
     * @param m method to be invoked
     * @return true/false if test success/fail
     * @throws IllegalAccessException IllegalAccessException
     * @throws InvocationTargetException InvocationTargetException
     */
    private boolean runTest(Object testObj, Method m) throws IllegalAccessException, InvocationTargetException {
        boolean objTest = (boolean) m.invoke(testObj);
        if(objTest) {
            process(m.getName() + ": SUCCESS\n");
            return true;
        }else {
            process(m.getName() + ": FAIL\n");
            return false;
        }
    }
}