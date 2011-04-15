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

        for(int i = 0; i < faces.length; i++){
            if((faces[i] == BlockFace.DOWN && (bBlock.getFace(faces[i],2).getType() == Material.SIGN_POST ||  bBlock.getFace(faces[i],2).getType() == Material.WALL_SIGN)) || bBlock.getFace(faces[i]).getType() == Material.SIGN_POST || bBlock.getFace(faces[i]).getType() == Material.WALL_SIGN){
                if(faces[i] == BlockFace.DOWN){
                    plugin.QuantumActivate((Sign) bBlock.getFace(faces[i],2).getState(),event.getOldCurrent(),event.getNewCurrent());
                }else{
                    plugin.QuantumActivate((Sign) bBlock.getFace(faces[i]).getState(),event.getOldCurrent(),event.getNewCurrent());
                }  
            }
        }
    }
}