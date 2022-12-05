/**
 * @author Vincent Johansson (dv14vjn@cs.umu.se]
 * @since 2022-11-21
 */

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * TestConfig class
 * Creates a container for all needed info about the test to be run
 */
public class TestConfig {
    public Method[] methods;
    public Method setUp;
    public Method tearDown;
    public Object testObj;
    public boolean testClass;


    /**
     * TestConfig constructor
     * @param className name of the test class
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws InvocationTargetException InvocationTargetException
     * @throws NoSuchMethodException NoSuchMethodException
     * @throws InstantiationException InstantiationException
     * @throws IllegalAccessException IllegalAccessException
     */
    public TestConfig(String className) throws ClassNotFoundException,
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Class<?> c = Class.forName(className);

        /* Check if class implements se.umu.cs.unittest.TestClass */
        if (!validateInterface(c)) {
            testClass = false;
            return;
        } else testClass = true;

        /* Instantiate class object and get methods */
        testObj = setUpClassObject(c);
        methods = getMethods(c);
    }

    /**
     * Validate that the test class implements se.umu.cs.unittest
     * @param c Class<?> object
     * @return  true/false
     */
    private boolean validateInterface(Class<?> c) {
        Class<?>[] interfaces = c.getInterfaces();
        for (Class<?> intr : interfaces) {
            if (intr.getName().equals("se.umu.cs.unittest.TestClass")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Collects the constructor method from a class and instantiates
     * an object of the class
     * @param c Class<?> object
     * @return Object<?>
     * @throws NoSuchMethodException NoSuchMethodException
     * @throws InstantiationException InstantiationException
     * @throws InvocationTargetException InvocationTargetException
     * @throws IllegalAccessException IllegalAccessException
     */
    private Object setUpClassObject(Class<?> c) throws NoSuchMethodException,
            InstantiationException, InvocationTargetException, IllegalAccessException{
        Constructor<?> constr = c.getConstructor();
        return constr.newInstance();
    }

    /**
     * Collect the methods from the specified class
     * @param c Class<?> object
     * @return methods Method[] containing the methods of the class
     */
    private Method[] getMethods(Class<?> c) {
        Method[] methods = c.getDeclaredMethods();

        /* Check for setUp and tearDown methods */
        for (Method method : methods) {
            if (method.getName().equals("setUp")) {
                this.setUp = method;
            } else if (method.getName().equals("tearDown")) {
                this.tearDown = method;
            }
        }
        return methods;
    }
}
