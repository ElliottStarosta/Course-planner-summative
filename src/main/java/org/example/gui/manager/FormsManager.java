package org.example.gui.manager;

import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import org.example.gui.pages.Application;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class FormsManager {
    private Application application; // Reference to the main application frame
    private static FormsManager instance; // Singleton instance

    // Cache to store form instances
    private final Map<Class<? extends JComponent>, JComponent> formCache = new HashMap<>();

    private FormsManager() {}

    public static FormsManager getInstance() {
        if (instance == null) {
            instance = new FormsManager();
        }
        return instance;
    }

    public void initApplication(Application application) {
        this.application = application;
    }

    /**
     * Displays the given form by reusing a cached instance if it exists.
     * If the form doesn't exist in the cache, create a new one and cache it,
     * unless the form's class name contains "Form#" (in which case it will not be cached).
     *
     * @param form The form instance to display.
     */
    public void showForm(JComponent form) {
        // Check if the form class name contains "Form#"
        boolean shouldCache = !(Pattern.matches("Form\\d+", form.getClass().getSimpleName()));

        // If caching is enabled and form is not already cached, add to the cache
        JComponent cachedForm = null;
        if (shouldCache) {
            cachedForm = formCache.get(form.getClass());
            if (cachedForm == null) {
                // If form is not in cache, cache the new form instance
                formCache.put(form.getClass(), form);
                cachedForm = form;
            }
        } else {
            // If "Form #" is in the class name, don't cache it, use the fresh instance
            cachedForm = form;
        }

        // Display the form (whether from cache or newly created)
        JComponent finalCachedForm = cachedForm;
        EventQueue.invokeLater(() -> {
            FlatAnimatedLafChange.showSnapshot();
            application.setContentPane(finalCachedForm);
            application.revalidate();
            application.repaint();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        });
    }

    /**
     * Deletes a specific form from the cache based on its class type.
     *
     * @param formClass The class of the form to be deleted from the cache.
     */
    public void deleteCache(Class<? extends JComponent> formClass) {
        formCache.remove(formClass);
    }

}
