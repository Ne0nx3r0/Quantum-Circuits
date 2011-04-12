package Ne0nx3r0.QuantumCircuits;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.Map;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.ChatColor;

public class QuantumCircuitsPlayerListener extends PlayerListener {
    private final QuantumCircuits plugin;
    private static Map<String, int[]> mLastClicks = new HashMap<String, int[]>();
    
    public QuantumCircuitsPlayerListener(QuantumCircuits instance){
        this.plugin = instance;
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event){
        if(event.getItem() == null || event.getClickedBlock() == null || event.getItem().getType() != Material.REDSTONE){
            return;
        }

        if(event.getClickedBlock().getType() == Material.LEVER){
            Location lClicked = event.getClickedBlock().getLocation();

            mLastClicks.put(event.getPlayer().getName(),new int[] {lClicked.getBlockX(),lClicked.getBlockY(),lClicked.getBlockZ()});

            event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE+"Quantum location stored!");
        }
        else if(mLastClicks.containsKey(event.getPlayer().getName()) && (event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.WALL_SIGN)){
            Sign sbClickedSign = (Sign) event.getClickedBlock().getState();
            String[] sLines = sbClickedSign.getLines();

            if(sLines[0].equalsIgnoreCase("quantum") || sLines[0].equalsIgnoreCase("qtoggle") || sLines[0].substring(0,4).equalsIgnoreCase("qlag")){
                int[] iCoordinates = mLastClicks.get(event.getPlayer().getName());

                sbClickedSign.setLine(1,Integer.toString(iCoordinates[0]));
                sbClickedSign.setLine(2,Integer.toString(iCoordinates[1]));
                sbClickedSign.setLine(3,Integer.toString(iCoordinates[2]));
                sbClickedSign.update(true);
                
                mLastClicks.remove(event.getPlayer().getName());

                event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE+"Quantum coordinates transferred!");
            }
        }
    }
}