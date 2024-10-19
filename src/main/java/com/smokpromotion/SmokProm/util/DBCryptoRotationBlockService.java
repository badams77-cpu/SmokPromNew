package com.smokpromotion.SmokProm.util;

import com.majorana.maj_orm.ORM.AbstractRotatableKey;
import com.smokpromotion.SmokProm.config.portal.PortalSecurityPrinciple;
import com.smokpromotion.SmokProm.domain.entity.IdAndKeyId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

//@Service
public class DBCryptoRotationBlockService {

    private Logger LOGGER = LoggerFactory.getLogger(DBCryptoRotationBlockService.class);

    @Transactional
    public int updateKeyBlock(AbstractRotatableKey repository, int startId, int newId, int BLOCK_SIZE, int oldKeyId, int newKeyId, boolean reverse) {
        List<IdAndKeyId> rows = repository.getRows(startId, BLOCK_SIZE, reverse? newKeyId:oldKeyId);
        List<Object> newRows = rows.stream().map(row -> {
            PortalSecurityPrinciple principle = new PortalSecurityPrinciple(0, new UUID(0,0), "");
            Map<Integer,String> practiceIdToName = new HashMap<>();
//            practiceIdToName.put(row.getPracticeId(),"Decryption Temp");
//            practiceIdToName.put(4,"Test Other Practice");
//            principle.setPracticeIdToName(practiceIdToName);
            return repository.encrypt(repository.decrypt( row, !reverse), reverse);
        }).collect(Collectors.toList());
        boolean hasRows = !rows.isEmpty();
        if (!hasRows) return startId;
        startId = 1 + rows.get(rows.size() - 1).getId();
        int updated = repository.putRows(newRows);
        LOGGER.info("UpdateKeyBlock: Updated "+updated+" rows");
        return startId;
    }
}
