package org.example.gui.manager;

import org.example.people.UserInput;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * The {@code DynamicFormLoader} class is responsible for dynamically loading
 * and instantiating form classes based on the provided question number and
 * user responses. The form class is expected to be named in the format
 * "Form{question}" and should have a constructor that accepts a
 * {@link HashMap} of user responses and an integer representing the question.
 */
public class DynamicFormLoader {

    /**
     * Loads and instantiates a form based on the provided question number
     * and user input.
     *
     * <p>The form class is expected to be in the package
     * {@code org.example.gui.pages.quiz} and should follow the naming
     * convention "Form{question}" where {question} is the integer representing
     * the question number. The form class must have a constructor that accepts
     * a {@link UserInput} object and an integer question number.
     *
     * @param question   The question number, which is used to determine
     *                   the form class to load.
     * @param userInput  A {@link UserInput} object containing the user input data
     *                   to be passed to the form constructor.
     * @return An instance of the dynamically loaded form, or {@code null} if
     *         the form could not be loaded due to an error.
     */
    public static Object loadForm(int question, UserInput userInput) {
        String formClassName = "org.example.gui.pages.quiz.Form" + question;
        try {
            // Load the class dynamically
            Class<?> formClass = Class.forName(formClassName);

            // Get the constructor with parameters (UserInput, int)
            Constructor<?> constructor = formClass.getConstructor(UserInput.class, int.class);

            // Create an instance of the form and return it
            return constructor.newInstance(userInput, question);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace(); // Handle exceptions appropriately
            return null;
        }
    }

}