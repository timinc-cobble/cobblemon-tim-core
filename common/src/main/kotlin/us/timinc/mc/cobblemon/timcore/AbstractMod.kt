package us.timinc.mc.cobblemon.timcore

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.properties.CustomPokemonProperty
import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType
import com.cobblemon.mod.common.api.reactive.EventObservable
import com.cobblemon.mod.common.api.scheduling.afterOnServer
import com.cobblemon.mod.common.api.spawning.BestSpawner.fishingSpawner
import com.cobblemon.mod.common.api.spawning.condition.AppendageCondition
import com.cobblemon.mod.common.api.spawning.condition.SpawningCondition
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence
import com.cobblemon.mod.common.api.spawning.spawner.PlayerSpawnerFactory
import com.cobblemon.mod.common.platform.events.PlatformEvents
import com.mojang.brigadier.arguments.ArgumentType
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import us.timinc.mc.cobblemon.timcore.command.ConfigReloadCommand
import us.timinc.mc.cobblemon.timcore.event.ReloadConfigEvent
import us.timinc.mc.cobblemon.timcore.mixin.helper.PokeSnackBlockSpawningInfluencesHelper

abstract class AbstractMod<T : AbstractConfig>(
    val modId: String,
    private val configClass: Class<T>,
) {
    @Suppress("PropertyName")
    @JvmField
    val RELOAD_CONFIG = EventObservable<ReloadConfigEvent>()

    var debugger: Debugger<T>

    lateinit var config: T

    val commands: MutableList<AbstractCommand<*>> = mutableListOf()

    val commandArguments = mutableMapOf<ResourceLocation,
            CommandArgumentContainer<out ArgumentType<*>, out ArgumentTypeInfo.Template<out ArgumentType<*>>>>()

    fun <A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>>
            registerCommandArgument(container: CommandArgumentContainer<A, T>) {
        commandArguments[container.identifier] = container
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val customPokemonProperties: MutableList<CustomPokemonPropertyType<*>> = mutableListOf()

    val reloadListeners: MutableList<AbstractReloadListener> = mutableListOf()

    val items: MutableMap<ResourceLocation, ItemContainer<out Item>> = mutableMapOf()
    val blocks: MutableMap<ResourceLocation, BlockContainer<out Block>> = mutableMapOf()

    init {
        reloadConfig()
        debugger = Debugger(modId, config)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun <T> registerCommand(command: AbstractCommand<T>): AbstractCommand<T> {
        commands.add(command)
        return command
    }

    fun <R : CustomPokemonProperty, T : CustomPokemonPropertyType<R>> registerCustomPokemonProperty(prop: T): T {
        customPokemonProperties.add(prop)
        return prop
    }

    fun <T : AbstractReloadListener> registerReloadListener(listener: T): T {
        reloadListeners.add(listener)
        return listener
    }

    fun <T : Item> registerItem(name: String, container: ItemContainer<T>): ItemContainer<T> {
        items[modResource(name)] = container
        return container
    }

    fun <T : Block> registerBlock(name: String, container: BlockContainer<T>): BlockContainer<T> {
        blocks[modResource(name)] = container
        return container
    }

    fun <T : AppendageCondition> registerSpawningCondition(appendageClass: Class<T>) {
        AppendageCondition.registerAppendage(SpawningCondition::class.java, appendageClass)
    }

    fun reloadConfig() {
        config = ConfigBuilder.load(configClass, modId)
        RELOAD_CONFIG.post(ReloadConfigEvent())
    }

    fun wrapUp() {
        var initialized = false
        PlatformEvents.SERVER_STARTED.subscribe { evt ->
            if (initialized) return@subscribe
            initialized = true
            afterOnServer(1, evt.server.overworld()) {
                customPokemonProperties.forEach { CustomPokemonProperty.register(it) }
            }
        }
    }

    fun modResource(name: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(modId, name)

    fun registerPlayerSpawnerInfluence(influence: (player: ServerPlayer) -> SpawningInfluence) {
        PlayerSpawnerFactory.influenceBuilders.add(influence)
    }

    fun registerPlayerSpawnerInfluence(influence: SpawningInfluence) {
        PlayerSpawnerFactory.influenceBuilders.add { influence }
    }

    val fishingSpawnerInfluences: MutableList<SpawningInfluence> = mutableListOf()
    fun registerFishingSpawnerInfluence(influence: SpawningInfluence) {
        fishingSpawnerInfluences.add(influence)
    }

    fun registerSnackSpawnerInfluence(influence: SpawningInfluence) {
        PokeSnackBlockSpawningInfluencesHelper.snackSpawnerInfluences.add(influence)
    }

    fun registerGeneralSpawnerInfluence(influence: SpawningInfluence) {
        registerPlayerSpawnerInfluence(influence)
        registerFishingSpawnerInfluence(influence)
        registerSnackSpawnerInfluence(influence)
    }

    init {
        registerCommand(ConfigReloadCommand(this))
        PlatformEvents.SERVER_STARTED.subscribe(Priority.LOWEST) {
            fishingSpawner.influences.addAll(fishingSpawnerInfluences)
        }
    }
}