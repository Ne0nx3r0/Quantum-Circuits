package Ne0nx3r0.QuantumCircuits;

import org.bukkit.util.config.Configuration;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.block.Sign;
import org.bukkit.block.Block;
import org.bukkit.Material;

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
       // pm.registerEvent(Event.Type,blockListener,Priority.Normal,this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("[Quantum] "+pdfFile.getName() + " version " + pdfFile.getVersion() + " ENABLED" );
    }

    public void QuantumActivate(Sign activator){
        QuantumActivate(activator,0,14);
    }

    public void QuantumActivate(Sign activator,int iOldCurrent,int iNewCurrent){
        String[] sBlockLines = activator.getLines();

        if(sBlockLines[0].equals("") || sBlockLines[1].equals("") || sBlockLines[2].equals("") || sBlockLines[3].equals("")){
            return;
        }

        if(sBlockLines[0].equalsIgnoreCase("quantum") || sBlockLines[0].equalsIgnoreCase("qtoggle") || sBlockLines[0].equalsIgnoreCase("qon") || sBlockLines[0].equalsIgnoreCase("qoff") || sBlockLines[0].substring(0,4).equalsIgnoreCase("qlag")){
            int iActivateX = Integer.parseInt(sBlockLines[1]);
            int iActivateY = Integer.parseInt(sBlockLines[2]);
            int iActivateZ = Integer.parseInt(sBlockLines[3]);

            Block bReceiver = activator.getWorld().getBlockAt(iActivateX,iActivateY,iActivateZ);
            
            if(bReceiver.getType() == Material.LEVER){
                int iData = (int) bReceiver.getData();

                if(sBlockLines[0].equalsIgnoreCase("quantum")){
                    if(iNewCurrent > 0 && (iData&0x08) != 0x08){
                        iData|=0x08;//send power on
                        bReceiver.setData((byte) iData);
                    }
                    else if((iData&0x08) == 0x08){
                        iData^=0x08;//send power off
                        bReceiver.setData((byte) iData);
                    }
                }else if(sBlockLines[0].equalsIgnoreCase("qtoggle")){
                    if(iNewCurrent > 0){
                        if((iData&0x08) != 0x08){
                            iData|=0x08;//send power on
                            bReceiver.setData((byte) iData);
                        }
                        else{
                            iData^=0x08;//send power off
                            bReceiver.setData((byte) iData);
                        }
                    }
                }else if(sBlockLines[0].equalsIgnoreCase("qon")){
                    if(iNewCurrent > 0 && (iData&0x08) != 0x08){
                        iData|=0x08;//send power on
                        bReceiver.setData((byte) iData);
                    }
                }else if(sBlockLines[0].equalsIgnoreCase("qoff")){
                    if(iNewCurrent > 0 && (iData&0x08) == 0x08){
                        iData^=0x08;//send power off
                        bReceiver.setData((byte) iData);
                    }
                }else if (sBlockLines[0].substring(0, 4).equalsIgnoreCase("qlag")) {
                    String[] sLagTimes = sBlockLines[0].split("/");
                    boolean powerOn;
                    int iLagTime;

                    if(iNewCurrent > 0){
                        iLagTime = Integer.parseInt(sLagTimes[1]);
                        powerOn = true;
                    }else{
                        iLagTime = Integer.parseInt(sLagTimes[2]);
                        powerOn = false;
                    }

                    if(iLagTime < 0){
                        iLagTime = 0;
                    }else if(iLagTime > MAX_LAG_TIME){
                        iLagTime = MAX_LAG_TIME;
                    }

                    iLagTime = iLagTime * 20;//convert to seconds

                    getServer().getScheduler().scheduleAsyncDelayedTask(this,new lagSetter(bReceiver,powerOn),iLagTime);
                }
            }
        }
    }

    private static class lagSetter implements Runnable{
        private final Block blockToChange;
        private final boolean setPositive;

        lagSetter(Block blockToChange,boolean setPositive){
            this.blockToChange = blockToChange;
            this.setPositive = setPositive;
        }
        public void run(){
            if(blockToChange.getType() != Material.LEVER){
                return;
            }

            int iData = (int) blockToChange.getData();

            if(this.setPositive && (iData&0x08) != 0x08){
                iData|=0x08;//send power on
                blockToChange.setData((byte) iData);
            }else if((iData&0x08) == 0x08){
                iData^=0x08;//send power off
                blockToChange.setData((byte) iData);
            }
        }
    }
}