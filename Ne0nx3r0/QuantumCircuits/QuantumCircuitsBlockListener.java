package Ne0nx3r0.QuantumCircuits;

import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

public class QuantumCircuitsBlockListener extends BlockListener {
    private final QuantumCircuits plugin;

    public QuantumCircuitsBlockListener(final QuantumCircuits plugin) {
        this.plugin = plugin;
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

                if(sBlockLines[0].equalsIgnoreCase("quantum") || sBlockLines[0].equalsIgnoreCase("qtoggle")){
                    int iActivateX = Integer.parseInt(sBlockLines[1]);
                    int iActivateY = Integer.parseInt(sBlockLines[2]);//To account for the player's +1 shown onscreen
                    int iActivateZ = Integer.parseInt(sBlockLines[3]);

                    Block bBlockToActivate = bBlock.getWorld().getBlockAt(iActivateX,iActivateY,iActivateZ);
                    if(bBlockToActivate.getType() == Material.LEVER){
                        int iData = (int) bBlockToActivate.getData();

                        if(sBlockLines[0].equalsIgnoreCase("quantum")){
                                if(event.getNewCurrent() > 0){
                                    if((iData&0x08) != 0x08){
                                        iData|=0x08;
                                        bBlockToActivate.setData((byte) iData);
                                    }
                                }
                                else{
                                    if((iData&0x08) == 0x08){
                                        iData^=0x08;
                                        bBlockToActivate.setData((byte) iData);
                                    }
                                } 
                        }else if(sBlockLines[0].equalsIgnoreCase("qtoggle")){
                                if(event.getOldCurrent() < event.getNewCurrent()){
                                    if((iData&0x08) != 0x08){
                                        iData|=0x08;
                                        bBlockToActivate.setData((byte) iData);
                                    }
                                    else if((iData&0x08) == 0x08){
                                        iData^=0x08;
                                        bBlockToActivate.setData((byte) iData);
                                    }
                                }
                        }
                    }
                }
            }
        }
    }
}