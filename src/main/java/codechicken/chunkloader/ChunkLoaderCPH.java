package codechicken.chunkloader;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;

import codechicken.core.CommonUtils;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.packet.PacketCustom.IClientPacketHandler;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;

public class ChunkLoaderCPH implements IClientPacketHandler {

    public static String channel = "ChickenChunks";

    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc, INetHandlerPlayClient handler) {
        switch (packet.getType()) {
            case 1:
                PlayerChunkViewer.openViewer(
                    (int) mc.thePlayer.posX,
                    (int) mc.thePlayer.posZ,
                    CommonUtils.getDimension(mc.theWorld));
                break;
            case 2:
                PlayerChunkViewer.instance()
                    .loadDimension(packet, mc.theWorld);
                break;
            case 3:
                PlayerChunkViewer.instance()
                    .unloadDimension(packet.readInt());
                break;
            case 4:
                PlayerChunkViewer.instance()
                    .handleChunkChange(
                        packet.readInt(),
                        new ChunkCoordIntPair(packet.readInt(), packet.readInt()),
                        packet.readBoolean());
                break;
            case 5:
                PlayerChunkViewer.instance()
                    .handleTicketChange(
                        packet.readInt(),
                        packet.readInt(),
                        new ChunkCoordIntPair(packet.readInt(), packet.readInt()),
                        packet.readBoolean());
                break;
            case 6:
                PlayerChunkViewer.instance()
                    .handlePlayerUpdate(
                        packet.readString(),
                        packet.readInt(),
                        new Vector3(packet.readFloat(), packet.readFloat(), packet.readFloat()));
                break;
            case 7:
                PlayerChunkViewer.instance()
                    .removePlayer(packet.readString());
                break;
            case 8:
                PlayerChunkViewer.instance()
                    .handleNewTicket(packet, mc.theWorld);
                break;
            case 10:
                TileChunkLoader.handleDescriptionPacket(packet, mc.theWorld);
                break;
            case 11:
                TileSpotLoader.handleDescriptionPacket(packet, mc.theWorld);
                break;
            case 12:
                BlockCoord pos = packet.readCoord();
                TileEntity tile = mc.theWorld.getTileEntity(pos.x, pos.y, pos.z);
                if (tile instanceof TileChunkLoader) mc.displayGuiScreen(new GuiChunkLoader((TileChunkLoader) tile));
                break;

        }
    }

    public static void sendGuiClosing() {
        PacketCustom packet = new PacketCustom(channel, 1);
        packet.sendToServer();
    }

    public static void sendShapeChange(TileChunkLoader tile, ChunkLoaderShape shape, int radius) {
        PacketCustom packet = new PacketCustom(channel, 2);
        packet.writeCoord(tile.xCoord, tile.yCoord, tile.zCoord);
        packet.writeByte(shape.ordinal());
        packet.writeByte(radius);
        packet.sendToServer();
    }

    public static void sendOwnerChange(TileChunkLoader tile, String owner) {
        PacketCustom packet = new PacketCustom(channel, 3);
        packet.writeCoord(tile.xCoord, tile.yCoord, tile.zCoord);
        packet.writeString(owner);
        packet.sendToServer();
    }
}
