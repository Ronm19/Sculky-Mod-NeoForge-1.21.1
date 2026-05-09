package net.ronm19.sculky.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;

public record SculkDolphinInputPayload(
        int entityId,
        float strafe,
        float forward,
        boolean jump
) implements CustomPacketPayload {

    public static final Type<SculkDolphinInputPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "sculk_dolphin_input"));

    public static final StreamCodec<ByteBuf, SculkDolphinInputPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SculkDolphinInputPayload::entityId,
            ByteBufCodecs.FLOAT,
            SculkDolphinInputPayload::strafe,
            ByteBufCodecs.FLOAT,
            SculkDolphinInputPayload::forward,
            ByteBufCodecs.BOOL,
            SculkDolphinInputPayload::jump,
            SculkDolphinInputPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}