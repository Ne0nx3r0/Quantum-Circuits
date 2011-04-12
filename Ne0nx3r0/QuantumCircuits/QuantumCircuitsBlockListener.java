package Ne0nx3r0.QuantumCircuits;

import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.Bukkit;

public class QuantumCircuitsBlockListener extends BlockListener {
    private final QuantumCircuits plugin;

    public QuantumCircuitsBlockListener(final QuantumCircuits plugin) {
        this.plugin = plugin;
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

    @Override
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        Block bBlock = event.getBlock();

        BlockFace[] faces = { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST,BlockFace.WEST, BlockFace.UP, BlockFace.DOWN };

        Sign sbBlockSign;
        
        for(int i = 0; i < faces.length; i++){
            if((faces[i] == BlockFace.DOWN && (bBlock.getFace(faces[i],2).getType() == Material.SIGN_POST ||  bBlock.getFace(faces[i],2).getType() == Material.WALL_SIGN)) || bBlock.getFace(faces[i]).getType() == Material.SIGN_POST || bBlock.getFace(faces[i]).getType() == Material.WALL_SIGN){
                if(faces[i] == BlockFace.DOWN){
                    sbBlockSign = (Sign) bBlock.getFace(faces[i],2).getState();
                }else{
                    sbBlockSign = (Sign) bBlock.getFace(faces[i]).getState();
                }
                String[] sBlockLines = sbBlockSign.getLines();

                if(sBlockLines[0].equalsIgnoreCase("quantum") || sBlockLines[0].equalsIgnoreCase("qtoggle") || sBlockLines[0].substring(0,4).equalsIgnoreCase("qlag")){
                    if(sBlockLines[1].equals("") || sBlockLines[2].equals("") || sBlockLines[3].equals("")){
                        return;
                    }
                    
                    int iActivateX = Integer.parseInt(sBlockLines[1]);
                    int iActivateY = Integer.parseInt(sBlockLines[2]);
                    int iActivateZ = Integer.parseInt(sBlockLines[3]);

                    Block bBlockToActivate = bBlock.getWorld().getBlockAt(iActivateX,iActivateY,iActivateZ);
                    if(bBlockToActivate.getType() == Material.LEVER){
                        int iData = (int) bBlockToActivate.getData();

                        if(sBlockLines[0].equalsIgnoreCase("quantum")){
                            if(event.getNewCurrent() > 0 && (iData&0x08) != 0x08){
                                iData|=0x08;//send power on
                                bBlockToActivate.setData((byte) iData);
                            }
                            else if((iData&0x08) == 0x08){
                                iData^=0x08;//send power off
                                bBlockToActivate.setData((byte) iData);
                            }
                        }else if(sBlockLines[0].equalsIgnoreCase("qtoggle")){
                            if(event.getOldCurrent() < event.getNewCurrent()){
                                if((iData&0x08) != 0x08){
                                    iData|=0x08;//send power on
                                    bBlockToActivate.setData((byte) iData);
                                }
                                else{
                                    iData^=0x08;//send power off
                                    bBlockToActivate.setData((byte) iData);
                                }
                            }
                        }else if(sBlockLines[0].substring(0,4).equalsIgnoreCase("qlag")){
                            String[] sLagTimes = sBlockLines[0].split("/");
                            boolean powerOn;
                            int iLagTime;

                            if(event.getNewCurrent() > 0){
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

                            iLagTime = iLagTime * 20;//convert to seconds

                            Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin,new lagSetter(bBlockToActivate,powerOn),iLagTime);
                        }
                    }
                }
            }
        }
    }
}