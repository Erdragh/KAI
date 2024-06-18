package dev.erdragh.kai

import dev.erdragh.kai.network.testXOR
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.slf4j.LoggerFactory

@Mod(KAIMod.MODID)
@EventBusSubscriber(modid = KAIMod.MODID, bus = EventBusSubscriber.Bus.MOD)
object KAIMod {
    const val MODID = "kai"
    const val MODNAME = "KAI"
    val LOGGER = LoggerFactory.getLogger(MODNAME)

    @SubscribeEvent
    fun onInitialize(event: FMLCommonSetupEvent) {
        LOGGER.info("Initializing $MODNAME")
        LOGGER.info("${MultiLayerNetwork::class.java.name}")
        testXOR()
    }
}