package hr.fer.oprpp1.hw08.plugins;

import hr.fer.oprpp1.custom.collections.SimpleHashtable;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;



public class PluginFactory {

    private static SimpleHashtable<String, ClassLoader> classLoaderMap = new SimpleHashtable<>();
    private static SimpleHashtable<String, URLClassLoader> urlClassLoaderMap = new SimpleHashtable<>();

    /**
     * Returns new Plugin instance of given name
     * @param pluginName plugin name
     * @return Plugin
     */
    public static Plugin newInstance(String pluginName) {
        Plugin plugin = null;
        try {
            ClassLoader parent;
            if (classLoaderMap.containsKey(pluginName)) {
                parent = classLoaderMap.get(pluginName);
            } else {
                parent = PluginFactory.class.getClassLoader();
                classLoaderMap.put(pluginName, parent);
            }

            URLClassLoader newClassLoader;
            if (urlClassLoaderMap.containsKey(pluginName)) {
                newClassLoader = urlClassLoaderMap.get(pluginName);
            } else {
                newClassLoader = new URLClassLoader(
                        new URL[]{
                                // Dodaj jedan direktorij (završava s /)
                                new File("src/main/java/hr/fer/oprpp1/hw08/plugins/")
                                        .toURI().toURL()
                        }, parent);
                urlClassLoaderMap.put(pluginName, newClassLoader);
            }


            Class<Plugin> clazz = (Class<Plugin>) newClassLoader.loadClass("hr.fer.oprpp1.hw08.plugins." + pluginName);
            // Class<Animal> clazz = (Class<Animal>) Class.forName("notepad.plugins." + pluginName);


            Constructor<?> ctr = clazz.getConstructor();
            plugin = (Plugin) ctr.newInstance();

        } catch (Exception e) {
            System.out.println("Neki exception");
            e.printStackTrace();
        }
        return plugin;
    }

}
