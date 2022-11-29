package com.armemius.magepunk.objects.block

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.FacingBlock
import net.minecraft.block.Material
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.Items
import net.minecraft.particle.ParticleTypes
import net.minecraft.state.StateManager
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.explosion.Explosion

class ExplosiveBarrelBlock() : FacingBlock(FabricBlockSettings.of(Material.WOOD).strength(1.0f)) {
    init {
        this.defaultState = this.stateManager.defaultState.with(FACING, Direction.NORTH);
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        return defaultState.with(FACING, ctx!!.playerLookDirection.opposite)
    }

    override fun onUse(
        state: BlockState?,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand?,
        hit: BlockHitResult?
    ): ActionResult {
        if (!player.getStackInHand(hand).isOf(Items.FLINT_AND_STEEL) && !player.getStackInHand(hand).isOf(Items.FIRE_CHARGE)) {
            return super.onUse(state, world, pos, player, hand, hit)
        }
        if (!world.isClient) {
            world.createExplosion(null, pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, 10.0F, true, Explosion.DestructionType.BREAK);
        } else {
            world.addParticle(ParticleTypes.SMOKE, pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, 0.0, 0.0, 0.0)
        }
        return super.onUse(state, world, pos, player, hand, hit)
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        if (!player.isCreative) {
            if (!world.isClient) {
                world.createExplosion(null, pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, 10.0F, true, Explosion.DestructionType.BREAK);
            } else {
                world.addParticle(ParticleTypes.SMOKE, pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, 0.0, 0.0, 0.0)
            }
        }
        super.onBreak(world, pos, state, player)
    }

    override fun onDestroyedByExplosion(world: World, pos: BlockPos, explosion: Explosion) {
        if (!world.isClient) {
            world.createExplosion(explosion.causingEntity, pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, 10.0F, true, Explosion.DestructionType.BREAK);
        } else {
            world.addParticle(ParticleTypes.SMOKE, pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, 0.0, 0.0, 0.0)
        }
        super.onDestroyedByExplosion(world, pos, explosion)
    }
}