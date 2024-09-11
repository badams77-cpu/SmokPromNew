package com.smokpromotion.SmokProm.util;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Profile("smok_app")
@Service()
public class DBCryptoRotationService {

    private Logger LOGGER = LoggerFactory.getLogger(DBCryptoRotationService.class);

//  private final ApplicationContext applicationContext;

    private static Class[] REPOSITORIES = {};

    private final static int BLOCK_SIZE = 100;
    private final DBCryptoRotationBlockService blockService;
    private final CryptoKeyIds cryptoKeyIds;
    private final Environment environment;
    private final boolean rotateKeysOnStartup;

    @Autowired
    public DBCryptoRotationService(

            //ApplicationContext applicationContext,
                                   CryptoKeyIds cryptoKeyIds,
                                   DBCryptoRotationBlockService blockService,
                                   Environment environment,
                                   @Value("${MPC_CRYPTO_ROTATE_KEYS_ON_STARTUP:false}") boolean rotateKeysOnStartup
    ){
//        this.applicationContext = applicationContext;
        this.cryptoKeyIds = cryptoKeyIds;
        this.blockService = blockService;
        this.environment = environment;
        this.rotateKeysOnStartup = rotateKeysOnStartup;
    }

    /**
     * This block start rotating keys on start up, but only if oldKeyId is not newKeyId
     */
    @PostConstruct
    public void onStartUpdateKeys(){
        if (this.rotateKeysOnStartup) {
            updateKeys(true);
        }
    }


    public void updateKeys(boolean reverse){
        if (cryptoKeyIds.getNewKeyId()==cryptoKeyIds.getOldKeyId()) {
            return;
        }
        boolean isTest = Arrays.stream(environment.getActiveProfiles()).anyMatch(x->x.equals("test"));
//        for(Class repo : REPOSITORIES) {
//                AbstractRotatableKey repository = (AbstractRotatableKey) applicationContext.getBean(repo);
//                int startId = 0;
//                int newId = 1;
//                while(newId!=startId) {
//                    startId = newId;
//                    newId = blockService.updateKeyBlock(repository, startId, newId, BLOCK_SIZE, cryptoKeyIds.getOldKeyId(), cryptoKeyIds.getNewKeyId(), reverse);
//                    if (newId==startId){
//                        LOGGER.info("updatedKeys, completed on repo "+repo);
//                    } else {
//                        LOGGER.info("updatedKeys, Updated keys on " + repo + " to id" + (newId - 1));
//                    }
//                }
//            }
//        }
    }




}
