package Ne0nx3r0.QuantumCircuits;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

public class QuantumCircuits extends JavaPlugin {
    private final QuantumCircuitsBlockListener blockListener = new QuantumCircuitsBlockListener(this);
    private final QuantumCircuitsPlayerListener playerListener = new QuantumCircuitsPlayerListener(this);

    public void onDisable() {
        System.out.println("Quantum Circuits Disabled");
    }

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.REDSTONE_CHANGE, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " ENABLED" );
    }
}