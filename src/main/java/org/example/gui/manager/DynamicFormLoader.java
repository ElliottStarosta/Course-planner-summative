package org.example.gui.manager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class DynamicFormLoader {

    public static Object loadForm(int question, HashMap<String, String> userResponses) {
        String formClassName = "org.example.gui.pages.quiz.Form" + question;
        try {
            // Load the class dynamically
            Class<?> formClass = Class.forName(formClassName);

            // Get the constructor with parameters (List<String>, String)
            Constructor<?> constructor = formClass.getConstructor(HashMap.class, int.class);

            // Create an instance of the form
            return constructor.newInstance(userResponses, question);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace(); // Handle exceptions appropriately
            return null; // Or handle error appropriately
        }
    }
}