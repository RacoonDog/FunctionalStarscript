package io.github.racoondog.functionalstarscript;

import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.meteorclient.utils.misc.Names;
import meteordevelopment.starscript.Starscript;
import meteordevelopment.starscript.value.Value;
import meteordevelopment.starscript.value.ValueMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public final class StarscriptWrappers {
    public static void init(Starscript ss) {
        ss.set("identifier", new ValueMap()
            .set("new", StarscriptWrappers::identifierNew)
            .set("minecraft", StarscriptWrappers::identifierMinecraft)
            .set("meteor", StarscriptWrappers::identifierMeteor)
        );

        ss.set("blockPos", new ValueMap()
            .set("new", StarscriptWrappers::blockPosNew)
        );

        function("registry.block.get", str -> wrapBlock(Registries.BLOCK.get(Identifier.tryParse(str))));
        function("registry.item.get", str -> wrapItem(Registries.ITEM.get(Identifier.tryParse(str))));
    }

    private static void function(String location, Function<String, Value> function) {
        MeteorStarscript.ss.set(location, (ss, argCount) -> {
            if (argCount < 1) ss.error(location + "() requires 1 argument, got %d.", argCount);
            return function.apply(ss.popString("First argument to " + location + "() needs to be a string."));
        });
    }

    /* Identifier */

    private static Value identifierNew(Starscript ss, int argCount) {
        if (argCount < 2) ss.error("identifier.new() requires 2 arguments, got %d.", argCount);
        return wrapIdentifier(
            ss.popString("First argument to identifier.new() needs to be a string."),
            ss.popString("Second argument to identifier.new() needs to be a string.")
        );
    }

    private static Value identifierMinecraft(Starscript ss, int argCount) {
        if (argCount < 1) ss.error("identifier.minecraft() requires 1 argument, got %d.", argCount);
        return wrapIdentifier("minecraft",
            ss.popString("First argument to identifier.minecraft() needs to be a string.")
        );
    }

    private static Value identifierMeteor(Starscript ss, int argCount) {
        if (argCount < 1) ss.error("identifier.meteor() requires 1 argument, got %d.", argCount);
        return wrapIdentifier("meteor",
            ss.popString("First argument to identifier.meteor() needs to be a string.")
        );
    }

    public static Value wrapIdentifier(String namespace, String path) {
        return Value.map(new ValueMap()
            .set("_toString", () -> Value.string(namespace + ":" + path))
            .set("namespace", () -> Value.string(namespace))
            .set("path", () -> Value.string(path))
        );
    }

    public static Value wrapIdentifier(Identifier identifier) {
        return wrapIdentifier(identifier.getNamespace(), identifier.getPath());
    }

    /* Block */

    public static Value wrapBlock(Block block) {
        return Value.map(new ValueMap()
            .set("_toString", () -> Value.string(Names.get(block)))
            .set("getDefaultState", () -> wrapBlockState(block.getDefaultState()))
            .set("id", () -> wrapIdentifier(Registries.BLOCK.getId(block)))
            .set("asItem", () -> wrapItem(block.asItem()))
        );
    }

    /* BlockState */

    public static Value wrapBlockState(BlockState blockState) {
        return Value.map(new ValueMap()
            .set("getBlock", () -> wrapBlock(blockState.getBlock()))
        );
    }

    /* BlockPos */

    public static Value blockPosNew(Starscript ss, int argCount) {
        if (argCount < 3) return wrapBlockPos(0, 0, 0);
        else return wrapBlockPos(
            (int) ss.popNumber("First argument to blockPos.new() needs to be a number."),
            (int) ss.popNumber("Second argument to blockPos.new() needs to be a number."),
            (int) ss.popNumber("Third argument to blockPos.new() needs to be a number.")
        );
    }

    public static Value wrapBlockPos(int x, int y, int z) {
        return Value.map(new ValueMap()
            .set("x", Value.number(x))
            .set("y", Value.number(y))
            .set("z", Value.number(z))
        );
    }

    public static Value wrapBlockPos(BlockPos blockPos) {
        return wrapBlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    /* Item */

    public static Value wrapItem(Item item) {
        return Value.map(new ValueMap()
            .set("_toString", () -> Value.string(Names.get(item)))
            .set("id", () -> wrapIdentifier(Registries.ITEM.getId(item)))
            .set("getDefaultStack", () -> wrapItemStack(item.getDefaultStack()))
            .set("asBlock", () -> wrapBlock(Block.getBlockFromItem(item)))
        );
    }

    /* ItemStack */

    public static Value wrapItemStack(ItemStack itemStack) {
        return Value.map(new ValueMap()
            .set("getItem", () -> wrapItem(itemStack.getItem()))
            .set("count", Value.number(itemStack.getCount()))
        );
    }
}
