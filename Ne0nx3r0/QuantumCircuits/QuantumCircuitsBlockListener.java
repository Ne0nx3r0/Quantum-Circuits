package Ne0nx3r0.QuantumCircuits;

import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Block;
import org.bukkit.Material;

public class QuantumCircuitsBlockListener extends BlockListener {
    private final QuantumCircuits plugin;

    public QuantumCircuitsBlockListener(final QuantumCircuits plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        Block bBlock = event.getBlock();

        BlockFace[] faces = { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST,BlockFace.WEST, BlockFace.UP, BlockFace.DOWN };

        for(int i = 0; i < faces.length; i++){
            if((faces[i] == BlockFace.DOWN && (bBlock.getFace(faces[i],2).getType() == Material.SIGN_POST ||  bBlock.getFace(faces[i],2).getType() == Material.WALL_SIGN)) || bBlock.getFace(faces[i]).getType() == Material.SIGN_POST || bBlock.getFace(faces[i]).getType() == Material.WALL_SIGN){
                if(faces[i] == BlockFace.DOWN){
                    QuantumActivate((Sign) bBlock.getFace(faces[i],2).getState(),event.getOldCurrent(),event.getNewCurrent());
                }else{
                    QuantumActivate((Sign) bBlock.getFace(faces[i]).getState(),event.getOldCurrent(),event.getNewCurrent());
                }  
            }
        }
    }

    private static boolean isOn(Block activate){
        int iData = (int) activate.getData();

        if((iData&0x08) == 0x08){
            return true;
        }
        return false;
    }

    private static void setOn(Block block){
        setReceiver(block,true);
    }
    private static void setOff(Block block){
        setReceiver(block,false);
    }
    private static void setReceiver(Block block,boolean on){
        if(block.getType() != Material.LEVER){
            return;
        }

        int iData = (int) block.getData();

        if(on && (iData&0x08) != 0x08){
            iData|=0x08;//send power on
            block.setData((byte) iData);
        }else if(!on && (iData&0x08) == 0x08){
            iData^=0x08;//send power off
            block.setData((byte) iData);
        }
    }

    private void QuantumActivate(Sign activator,int iOldCurrent,int iNewCurrent){
        String[] sBlockLines = activator.getLines();

        if(sBlockLines[0].equals("")|| sBlockLines[1].equals("") || sBlockLines[2].equals("") || sBlockLines[3].equals("")){
            return;
        }

        Block bReceiver;

        try{
            bReceiver = activator.getWorld().getBlockAt(Integer.parseInt(sBlockLines[1]),Integer.parseInt(sBlockLines[2]),Integer.parseInt(sBlockLines[3]));
        }
        catch(Exception e){
            return;
        }
        
        // This check runs again in seton/off, but we do it here to filter out broken links
        if(bReceiver.getType() == Material.LEVER){
            if(sBlockLines[0].equalsIgnoreCase("quantum")){
                //makes receiver match source status
                if(iNewCurrent > 0){
                    setOn(bReceiver);
                }else{
                    setOff(bReceiver);
                }
            }else if(sBlockLines[0].equalsIgnoreCase("qtoggle")){
                //toggles receiver when powered
                if(iNewCurrent > 0){
                    if(isOn(bReceiver)){
                        setOff(bReceiver);
                    }
                    else{
                        setOn(bReceiver);
                    }
                }
            }else if(sBlockLines[0].equalsIgnoreCase("qon")){
                //always set on when powered
                if(iNewCurrent > 0){
                    setOn(bReceiver);
                }
            }else if(sBlockLines[0].equalsIgnoreCase("qoff")){
                //always set off when powered
                if(iNewCurrent > 0){
                    setOff(bReceiver);
                }
            }else if (sBlockLines[0].substring(0,4).equalsIgnoreCase("qlag")){
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
                }else if(iLagTime > plugin.MAX_LAG_TIME){
                    iLagTime = plugin.MAX_LAG_TIME;
                }

                //convert to seconds
                iLagTime = iLagTime * 20;

                plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin,new lagSetter(bReceiver,powerOn),iLagTime);
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
            if(this.setPositive){
                setOn(this.blockToChange);
            }else{
                setOff(this.blockToChange);
            }
        }
    }
}
