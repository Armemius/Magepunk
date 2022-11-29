package com.armemius.magepunk.research.tech.callbacks

import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.fluid.Fluid
import net.minecraft.item.ItemStack
import net.minecraft.tag.TagKey
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object CallbackPackages {
    interface CallbackPackage
    class AttackBlock(val world: World, val pos: BlockPos, var state: BlockState) : CallbackPackage
    class UseBlock(val world: World, val hand: Hand, val hitResult: BlockHitResult) : CallbackPackage
    class UseItem(val world: World, val item: ItemStack) : CallbackPackage
    class AttackEntity(val world: World, val hand: Hand, val entity: Entity, val hitResult: EntityHitResult?) : CallbackPackage
    class Movement(val dx: Double, val dy: Double, val dz: Double, val hasVehicle: Boolean, val isClimbing: Boolean, val onGround: Boolean, val isSprinting: Boolean, val isInSneakingPose: Boolean, val isFallFlying: Boolean, val isSwimming: Boolean, val isTouchingWater: Boolean, val fallDistance: Float, val submergedFluidTag: Set<TagKey<Fluid>>?) : CallbackPackage
    class Empty() : CallbackPackage
    val EMPTY = Empty()
}