package com.armemius.magepunk
import com.armemius.magepunk.registries.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis

object Magepunk: ModInitializer {
    const val ID = "mgp"

    val LOGGER: Logger = LoggerFactory.getLogger(ID)

    val ITEM_GROUP = FabricItemGroupBuilder.build(
        Identifier(ID)
    ) { ItemStack(Blocks.COBBLESTONE) }

    fun log(info: String) = LOGGER.info(info)

    override fun onInitialize() {
        log("Initializing Magepunk ;)")
        val runtime = measureTimeMillis {
            ItemsRegistry.register()
            TabsRegistry.register()
            NetworkRegistry.registerServer()
            if (FabricLoader.getInstance().environmentType == EnvType.CLIENT)
                NetworkRegistry.registerClient()
            CallbacksRegistry.register()
            CommandsRegistry.register()
        }
        log("Ready in ${runtime}ms")
    }
}

/*
TODO Aspects system
TODO Research system
TODO Think about progression stages ANCIENT ERA -> PROGRESSIVE ERA -> MAGIC RISING -> DIMENSIONAL SHIFT -> MAGE-INDUSTRIAL REVOLUTION -> POST-INDUSTRIAL ERA
TODO Research tabs and directions
 */