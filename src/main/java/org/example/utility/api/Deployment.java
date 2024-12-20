package org.example.utility.api;

/**
 * The {@code Deployment} interface defines a contract for running an API-related task.
 * It contains a method {@code runAPI} that can be implemented by any class that
 * needs to provide specific behavior for running an API request.
 *
 * <p>This interface is currently used to provide a method for initiating an API connection,
 * but the actual behavior of the method is left to be defined by the implementing classes.</p>
 */
public interface Deployment {

    /**
     * Runs an API-related task.
     * <p>
     * This method is a placeholder for the API task and should be implemented by classes
     * that define the specific behavior for connecting to and interacting with the API.
     * </p>
     * <p>
     * For example, the method might involve sending an HTTP request to an endpoint,
     * processing the response, or handling any related exceptions.
     * </p>
     */
    static void runAPI() {
        // The method body is currently empty. Classes implementing this interface
        // should provide the appropriate logic.
    }
}