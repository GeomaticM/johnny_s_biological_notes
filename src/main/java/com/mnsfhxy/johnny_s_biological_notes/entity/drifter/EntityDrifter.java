package com.mnsfhxy.johnny_s_biological_notes.entity.drifter;

import com.google.common.collect.Maps;
import com.mnsfhxy.johnny_s_biological_notes.Item.ItemKatana;
import com.mnsfhxy.johnny_s_biological_notes.init.RegistrationInit;
import com.mnsfhxy.johnny_s_biological_notes.init.SoundInit;
import com.mnsfhxy.johnny_s_biological_notes.init.TagsInit;
import com.mnsfhxy.johnny_s_biological_notes.util.ModAnimation;
import net.minecraft.Util;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraftforge.common.data.ForgeEntityTypeTagsProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityDrifter extends PathfinderMob {

    private Map<Player, Favorability> favorability = new HashMap<>();
    public AnimationState walkingAnimationState = new AnimationState();
    public AnimationState alert0AnimationState = new AnimationState();
    public AnimationState alert1AnimationState = new AnimationState();
//    public AnimationState fightAnimationState = new AnimationState();
    //    public ModAnimation walkingAnimationState = new ModAnimation();
//    public ModAnimation alert0AnimationState = new ModAnimation();
//    public ModAnimation alert1AnimationState = new ModAnimation();
    public ModAnimation fightAnimationState = new ModAnimation();
    private boolean renderItem = false;
    private boolean haveGift=true;
    public EntityDrifter(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.xpReward = 5;
    }

    public static AttributeSupplier.Builder prepareAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.35F);
        builder = builder.add(Attributes.MAX_HEALTH, 20);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
        builder = builder.add(Attributes.FOLLOW_RANGE, 64);
        return builder;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new FllowPlayerGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, ZombieVillager.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, WitherSkeleton.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Witch.class, true));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Vindicator.class, true));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Vex.class, true));
        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Stray.class, true));
        this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Slime.class, true));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Shulker.class, true));
        this.targetSelector.addGoal(11, new NearestAttackableTargetGoal<>(this, Phantom.class, true));
        this.targetSelector.addGoal(12, new NearestAttackableTargetGoal<>(this, MagmaCube.class, true));
        this.targetSelector.addGoal(13, new NearestAttackableTargetGoal<>(this, Husk.class, true));
        this.targetSelector.addGoal(14, new NearestAttackableTargetGoal<>(this, Hoglin.class, true));
        this.targetSelector.addGoal(15, new NearestAttackableTargetGoal<>(this, Ghast.class, true));
        this.targetSelector.addGoal(16, new NearestAttackableTargetGoal<>(this, Endermite.class, true));
        this.targetSelector.addGoal(17, new NearestAttackableTargetGoal<>(this, ElderGuardian.class, true));
        this.targetSelector.addGoal(18, new NearestAttackableTargetGoal<>(this, Drowned.class, true));
        this.targetSelector.addGoal(19, new NearestAttackableTargetGoal<>(this, Blaze.class, true));
        this.targetSelector.addGoal(20, new NearestAttackableTargetGoal<>(this, PiglinBrute.class, true));
        this.targetSelector.addGoal(21, new  NearestAttackablePlayerGoal(this,true));

    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        RandomSource randomsource = pLevel.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, pDifficulty);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);

        if (this.random.nextInt(0, 100) < 9) {
            ItemStack itemStack=new ItemStack(RegistrationInit.IRON_KATANA.get());
            itemStack.setDamageValue(75);
//            itemStack = new ItemStack(ItemKatana(Tiers.IRON, 0, 0, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).durability(75)));
            this.spawnAtLocation(itemStack);
        }
    }

    protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
        super.populateDefaultEquipmentSlots(pRandom, pDifficulty);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(RegistrationInit.IRON_KATANA.get()));
    }

    public Favorability updateFavorability(Player pPlayer, LivingEntity pTarget) {
        Favorability favorability = null;
        if (this.favorability.containsKey(pPlayer)) {
            favorability = this.favorability.get(pPlayer);
            favorability.update(pTarget);
            this.favorability.replace(pPlayer, favorability);
            if(this.haveGift&&favorability.canReward()&&pPlayer.distanceTo(this)<3){
                this.spawnAtLocation(RegistrationInit.FORGED_PLATE.get());
                this.haveGift=false;
            }
        } else {
            favorability = new Favorability();
            favorability.update(pTarget);
            this.favorability.put(pPlayer, favorability);
        }
        return favorability;
    }

    @Override
    public boolean canAttackType(EntityType<?> pType) {
        return pType.is(TagsInit.Entities.DRIFTER_ATTACKABLE);
    }

    @Override
    public void tick() {
        super.tick();
//        LivingEntity target = getTarget();
        if (this.level.isClientSide) {
//            target = this.level.getNearestEntity(this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(10, 4.0D, 10), (x) -> {
//                return true;
//            }), TargetingConditions.forCombat().range(this.getAttributeValue(Attributes.FOLLOW_RANGE)).selector(null), this, this.getX(), this.getEyeY(), this.getZ());
            List<LivingEntity> entitiesOfClass = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(10, 4.0D, 10), (x) -> {
                return true;
            });
            float minDisToEnemy = Float.MAX_VALUE;
            for (LivingEntity target : entitiesOfClass) {
                if (target.isAlive() && canAttackType(target.getType())) {
                    minDisToEnemy = Math.min(target.distanceTo(this), minDisToEnemy);

                }
            }
            alert0AnimationState.stop();
            alert1AnimationState.stop();
            if (minDisToEnemy < 4) {
                alert1AnimationState.start(this.tickCount);
                if(this.isAggressive()){
                    fightAnimationState.start(this.tickCount);
                }else{
                    fightAnimationState.stop();
                }
                renderItem = true;
            } else if (minDisToEnemy < 10) {
                alert0AnimationState.start(this.tickCount);
                renderItem = true;
            } else {
                renderItem = false;
            }
        }


        Player nearestPlayer = this.level.getNearestPlayer(this, 64F);
        if (nearestPlayer != null && !favorability.containsKey(nearestPlayer))
            favorability.put(nearestPlayer, new Favorability());

    }
    @Override
    public SoundEvent getAmbientSound() {
        return SoundInit.DRIFTERS_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundInit.DRIFTERS_DEATH.get();
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        boolean b = super.doHurtTarget(pEntity);
        if(pEntity instanceof LivingEntity){
            if(((LivingEntity)pEntity).getHealth()<=0){
                playSound(SoundInit.DRIFTERS_VICTORY.get(),this.getSoundVolume(),this.getVoicePitch());
            }
        }
        return b;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundInit.DRIFTERS_HURT.get();
    }

    public boolean isRenderItem() {
        return renderItem;
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    public class Favorability {
        private float value;
        private final float MAX_VALUE = 5F;
        private final float MIN_VALUE = -1F;
        private final float FOLLOW_VALUE = 3F;

        public Favorability() {
            this.value = 0;
        }

        private void add(float add) {
            this.value = Math.min(this.value + add, MAX_VALUE);
        }

        private boolean canFollow() {
//            return true;
//            System.out.println(value);
            return value > FOLLOW_VALUE;
        }

        private boolean canAttack() {
            return value == MIN_VALUE;
        }

        private boolean canReward() {
            return value >= MAX_VALUE;
        }

        private void sub(float sub) {
            this.value = Math.max(this.value - sub, MIN_VALUE);
        }

        public void update(LivingEntity pTarget) {
            if (pTarget.getType().is(TagsInit.Entities.DRIFTER_FAVORABILITY_ADD_QUARTER)) {
                add(0.25F);
            } else if (pTarget instanceof AbstractIllager) {
                add(0.5F);
            } else if (pTarget.getType().is(TagsInit.Entities.DRIFTER_FAVORABILITY_ADD_ONE)) {
                add(1F);
            } else if (pTarget instanceof Villager) {
                if (((Villager) pTarget).isBaby()) sub(2F);
                else sub(1F);
            } else if (pTarget instanceof EntityDrifter) {
                if (value > 3) sub(0.5F);
                else sub(1F);
            }
        }

    }
    private class NearestAttackablePlayerGoal extends NearestAttackableTargetGoal{
        private EntityDrifter entityDrifter;
        public NearestAttackablePlayerGoal(EntityDrifter entityDrifter,  boolean pMustSee) {
            super(entityDrifter, Player.class, pMustSee);
            this.entityDrifter=entityDrifter;
        }

        @Override
        protected void findTarget() {
            this.target = this.mob.level.getNearestPlayer(this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
            Favorability favorability = entityDrifter.favorability.get(this.target);
            if(favorability==null||!favorability.canAttack()){
                this.target=null;
            }
        }
    }
    private class FllowPlayerGoal extends Goal {
        private Player followPlayer;
        private int timeToRecalcPath;
        private final PathNavigation navigation;

        private EntityDrifter entityDrifter;

        public FllowPlayerGoal(EntityDrifter entityDrifter) {
            this.entityDrifter = entityDrifter;
            this.navigation = entityDrifter.getNavigation();

        }

        @Override
        public boolean canUse() {
            for (Player player : entityDrifter.favorability.keySet()) {
                if (entityDrifter.favorability.get(player).canFollow()) {
                    this.followPlayer = player;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.navigation.isDone()) {
                return false;
            } else {
                return super.canContinueToUse() && followPlayer != null && entityDrifter.favorability.get(followPlayer).canFollow();
            }
        }

        @Override
        public void stop() {

            super.stop();
            this.navigation.stop();
        }

        @Override
        public void start() {
            super.start();
            entityDrifter.playSound(SoundInit.DRIFTERS_ADMIRE.get(),entityDrifter.getSoundVolume(),entityDrifter.getVoicePitch());
            this.timeToRecalcPath = 0;

        }

        private int randomIntInclusive(int pMin, int pMax) {
            return this.entityDrifter.getRandom().nextInt(pMax - pMin + 1) + pMin;
        }

        private boolean canTeleportTo(BlockPos pPos) {
            BlockPathTypes blockpathtypes = WalkNodeEvaluator.getBlockPathTypeStatic(this.entityDrifter.level, pPos.mutable());
            if (blockpathtypes != BlockPathTypes.WALKABLE) {
                return false;
            } else {
                BlockState blockstate = this.entityDrifter.level.getBlockState(pPos.below());
                if (blockstate.getBlock() instanceof LeavesBlock) {
                    return false;
                } else {
                    BlockPos blockpos = pPos.subtract(this.entityDrifter.blockPosition());
                    return this.entityDrifter.level.noCollision(this.entityDrifter, this.entityDrifter.getBoundingBox().move(blockpos));
                }
            }
        }

        private boolean maybeTeleportTo(int pX, int pY, int pZ) {
            if (Math.abs((double) pX - this.entityDrifter.getX()) < 2.0D && Math.abs((double) pZ - this.entityDrifter.getZ()) < 2.0D) {
                return false;
            } else if (!this.canTeleportTo(new BlockPos(pX, pY, pZ))) {
                return false;
            } else {
                this.entityDrifter.moveTo((double) pX + 0.5D, (double) pY, (double) pZ + 0.5D, this.entityDrifter.getYRot(), this.entityDrifter.getXRot());
                this.navigation.stop();
                return true;
            }
        }

        private void teleportToOwner() {
            BlockPos blockpos = this.followPlayer.blockPosition();

            for (int i = 0; i < 10; ++i) {
                int j = this.randomIntInclusive(-3, 3);
                int k = this.randomIntInclusive(-1, 1);
                int l = this.randomIntInclusive(-3, 3);
                boolean flag = this.maybeTeleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
                if (flag) {
                    return;
                }
            }

        }

        @Override
        public void tick() {
            super.tick();
            if (this.followPlayer != null) {
                this.entityDrifter.getLookControl().setLookAt(this.followPlayer, 10.0F, (float) this.entityDrifter.getMaxHeadXRot());
                if (--this.timeToRecalcPath <= 0) {
                    this.timeToRecalcPath = this.adjustedTickDelay(10);
                    if (!this.entityDrifter.isLeashed() && !this.entityDrifter.isPassenger()) {
                        if (this.entityDrifter.distanceToSqr(this.followPlayer) >= 144.0D) {
                            this.teleportToOwner();
                        } else {
                            this.navigation.moveTo(this.followPlayer, 0.8F);
                            this.navigation.moveTo(this.followPlayer, 0.8F);
                        }

                    }
                }
            }
        }
    }
}
