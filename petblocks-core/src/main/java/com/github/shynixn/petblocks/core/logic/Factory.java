package com.github.shynixn.petblocks.core.logic;

import com.github.shynixn.petblocks.api.business.controller.PetBlockController;
import com.github.shynixn.petblocks.api.persistence.controller.*;

public class Factory {

   // private static ExtensionHikariConnectionContext connectionContext;

    public static PlayerMetaController createPlayerDataController() {
       // return new PlayerDataRepository(connectionContext);
        return null;
    }

    public static ParticleEffectMetaController createParticleEffectController() {
       // return new ParticleEffectDataRepository(connectionContext);
        return null;
    }

    public static EngineController createEngineController() {
        //return new EngineConfiguration(JavaPlugin.getPlugin(PetBlocksPlugin.class));
        return null;
    }

    public static PetBlockController createPetBlockController() {
       // return new PetBlockRepository();
        return null;
    }

    public static CostumeController createCostumesController(String category) {
       // return new CostumeConfiguration(category, JavaPlugin.getPlugin(PetBlocksPlugin.class));
        return null;
    }

    public static CostumeController createMinecraftHeadsCostumesController() {
      //  return new MinecraftHeadConfiguration(JavaPlugin.getPlugin(PetBlocksPlugin.class));
        return null;
    }

    public static PetMetaController createPetDataController() {
       // return new PetDataRepository(connectionContext);
        return null;
    }

    public static OtherGUIItemsController createGUIItemsController() {
      //  return new FixedItemConfiguration(JavaPlugin.getPlugin(PetBlocksPlugin.class));
        return null;
    }

    public static ParticleController createParticleConfiguration() {
//        return new ParticleConfiguration(JavaPlugin.getPlugin(PetBlocksPlugin.class));
        return null;
    }

    public static void disable() {
     //   if (connectionContext == null)
            return;
      //  connectionContext.close();
    //    connectionContext = null;
    }

    public synchronized static void initialize(Object plugin) {

    }

}
