package net.ronm19.sculky.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UseAbilityPayload() implements CustomPacketPayload {

    public static final Type<UseAbilityPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "use_ability"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UseAbilityPayload> STREAM_CODEC =
            StreamCodec.unit(new UseAbilityPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
