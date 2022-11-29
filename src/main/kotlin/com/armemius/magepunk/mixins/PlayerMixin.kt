package com.armemius.magepunk.mixins

import com.armemius.magepunk.registries.ItemsRegistry
import com.armemius.magepunk.research.tech.Tech
import com.armemius.magepunk.research.tech.callbacks.Callback
import com.armemius.magepunk.research.tech.callbacks.CallbackPackages
import com.armemius.magepunk.research.tech.callbacks.CallbackType
import com.armemius.magepunk.util.PlayerEntityExt
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.Fluid
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.tag.TagKey
import net.minecraft.world.World
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Suppress("unused")
@Mixin(PlayerEntity::class)
abstract class PlayerMixin protected constructor(entityType: EntityType<out LivingEntity?>?, world: World?) :
    LivingEntity(entityType, world), PlayerEntityExt {
    private val submergedFluidTag: Set<TagKey<Fluid>>? = null
    private var researchPts = 0
    private var tickCnt = 0

    override fun getResearchPts(): Int {
        return researchPts
    }

    override fun setResearchPts(value: Int) {
        researchPts = value
    }

    override fun addResearchPts(value: Int) {
        researchPts += value
    }

    override fun removeResearchPts(value: Int): Boolean {
        if (researchPts >= value) {
            researchPts -= value
            return true
        }
        return false
    }

    override fun sendCallback(cb: Callback) {
        if (this is ServerPlayerEntity) {
            val inv = this.inventory
            for (it in 0 until inv.size()) {
                val item = inv.getStack(it)
                if (item.item == ItemsRegistry.RESEARCH) {
                    if (item.hasNbt() && item.nbt != null && item.nbt!!.contains("mgp.tech") && !item.nbt!!.getBoolean("mgp.ready")) {
                        updateResearch(item, cb)
                    }
                }
            }
        }
    }

    private fun updateResearch(item: ItemStack, cb: Callback) {
        var name = item.nbt!!.getString("mgp.tech")
        var stage = item.nbt?.getInt("mgp.stage")
        if (stage == null) {
            item.nbt?.putInt("mgp.stage", 0)
            stage = 0
        }
        var techStage = Tech.TECHS_MAP[name]?.stages?.get(stage) ?: return
        for (it in 0 until techStage.size()) {
            if (techStage.checkType(cb, it)) {
                var old = item.nbt!!.getFloat("mgp.req.slot$it")
                item.nbt!!.putFloat("mgp.req.slot$it", techStage.sendCallback(cb, it, old))
            }
        }
    }

    @Inject(method = ["tick"], at = [At("HEAD")])
    fun injectTick(ci: CallbackInfo?) {
        // Sends info every 4 ticks
        // TODO make send rate changeable by config
        tickCnt++
        if (tickCnt != 4)
            return
        else
            tickCnt = 0
        if (this is ServerPlayerEntity) {
            sendCallback(Callback(this as PlayerEntity, CallbackType.TICK, CallbackPackages.EMPTY))
        }
    }

    @Inject(method = ["increaseTravelMotionStats"], at = [At("HEAD")])
    fun increaseTravelMotionStats(dx: Double, dy: Double, dz: Double, ci: CallbackInfo?) {
        if (this is ServerPlayerEntity)
            this.sendCallback(Callback(this as PlayerEntity, CallbackType.MOVEMENT, CallbackPackages.Movement(dx, dy, dz, this.hasVehicle(), this.isClimbing(), this.onGround, this.isSprinting(), this.isInSneakingPose(), this.isFallFlying(), this.isSwimming(), this.isTouchingWater(), this.fallDistance, this.submergedFluidTag)))
    }
}