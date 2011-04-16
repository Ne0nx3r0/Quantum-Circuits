package Ne0nx3r0.QuantumCircuits;

import org.bukkit.util.config.Configuration;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

public class QuantumCircuits extends JavaPlugin{
    private final QuantumCircuitsBlockListener blockListener = new QuantumCircuitsBlockListener(this);
    private final QuantumCircuitsPlayerListener playerListener = new QuantumCircuitsPlayerListener(this);
    public int MAX_LAG_TIME = 300;

    public void onDisable(){
        System.out.println("[Quantum] Quantum Circuits Disabled");
    }

    public void onEnable(){
        Configuration config = getConfiguration();

        int iMaxLagTime = config.getInt("maxlagtime",-1);

        //set default values if necessary
        if(iMaxLagTime == -1){
            System.out.println("[Quantum] Creating config file...");
            config.setProperty("maxlagtime",300);
            iMaxLagTime = 300;
            config.save();
        }

        MAX_LAG_TIME = iMaxLagTime;

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.REDSTONE_CHANGE, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("[Quantum] "+pdfFile.getName() + " version " + pdfFile.getVersion() + " ENABLED" );
    }
}