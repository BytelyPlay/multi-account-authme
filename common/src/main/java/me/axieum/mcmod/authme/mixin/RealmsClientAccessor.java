package me.axieum.mcmod.authme.mixin;

import com.mojang.realmsclient.client.RealmsClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RealmsClient.class)
public interface RealmsClientAccessor {
    @Invoker(value = "<init>")
    static RealmsClient init(String s1, String s2, Minecraft mc) {
        throw new AssertionError();
    };

    @Accessor(value = "realmsClientInstance")
    static void setRealmsClientInstance(RealmsClient client) {
        throw new AssertionError();
    };
}
