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
public class TestHandler extends SwingWorker<List<String>, String> {

    public JTextField input;
    public JTextArea output;
    private List<String> results;
    private String testResult;
    public TestHandler(final JTextField input, final JTextArea output){
        this.input = input;
        this.output = output;
        results = new ArrayList<>();
    }

    @Override
    public List<String> doInBackground() {
        int success = 0;
        int failed = 0;
        Object testObj;
        Method m = null;
        try {
            Class<?> c = Class.forName(input.getText());
            /* Check if class implements se.umu.cs.unittest.TestClass */
            Class<?>[] interfaces = c.getInterfaces();
            boolean hasTestInterface = false;
            for (Class<?> intr : interfaces) {
                if (intr.getName().equals("se.umu.cs.unittest.TestClass")) {
                    hasTestInterface = true;
                    break;
                }
            }
            if (!hasTestInterface) {
                process("Test does not implement se.umu.cs.unittest.TestClass\n");
                return results;
            }

            Constructor<?> constr = c.getConstructor();

            try {
                testObj = constr.newInstance();
            } catch (InstantiationException e) {
                output.append("Class object from" + getClass().getName() + "could not be instantiated\n");
                return results;
            } catch (InvocationTargetException e) {
                output.append(String.valueOf(e.getTargetException()));
                return results;
            }

            /* Get methods from class */
            Method[] methods = c.getDeclaredMethods();
            Method setUp = null;
            Method tearDown = null;

            /* Check for setUp and tearDown methods */
            for (Method method : methods) {
                if (method.getName().equals("setUp")) {
                    setUp = method;
                } else if (method.getName().equals("tearDown")) {
                    tearDown = method;
                }
            }

            /* Run methods from class */
            for (Method method : methods) {
                m = method;
                if (m.getName().equals("setUp") | m.getName().equals("tearDown")){
                    continue;
                }
                try {
                    if (setUp != null && tearDown != null) {
                        Object classObj = setUp.invoke(testObj);
                        if(runTest(testObj, m)){
                            success++;
                        }else{
                            failed++;
                        }
                        classObj = tearDown.invoke(testObj);
                    }else{
                        if(runTest(testObj, m)){
                            success++;
                        }else{
                            failed++;
                        }
                    }
                } catch (InvocationTargetException e) {
                    String[] shortCause = String.valueOf(e.getCause()).split(":");
                    process(m.getName() + ": FAIL Generated a " + shortCause[0] + "\n");
                } catch (NullPointerException e) {
                    process(m.getName() + ": FAIL Generated a java.lang.NullPointerException");
                }
            }
        } catch (ClassNotFoundException e) {
            output.append("No test named " + input.getText() + " found\n");
        } catch (IllegalAccessException e) {
            assert m != null;
            process(m.getName() + ": FAIL Generated a " + e + "\n");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        results.add("\n" + success + " tests succeeded");
        results.add(failed + " tests failed.");
        return results;
    }

    protected void process(String string) {
        output.append(string);
    }

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