package com.smokpromotion.SmokProm.domain.repo;

import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInterface;
import com.majorana.maj_orm.ORM_ACCESS.MultiId;
import com.smokpromotion.SmokProm.domain.entity.SalesLeadNote;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class REP_SalesLeadNote {

    private static MethodPrefixingLogger LOGGER = MethodPrefixingLoggerFactory.getLogger(REP_SalesLeadNote.class);

    private DbBeanGenericInterface<SalesLeadNote> salesRepo = null;

//    private static final int changePasswordTimeOut = 10000;


    public REP_SalesLeadNote(){
        DbBean dBean = new DbBean();
        try {
            dBean.connect();
            salesRepo = dBean.getTypedBean(SalesLeadNote.class);
        } catch (ClassNotFoundException | SQLException e){
            LOGGER.error("Class SalesLeadNote not found");
        }
    }


    public List<SalesLeadNote>  findByUserAndEntityId(int userId, int entityId){
        List<SalesLeadNote> res = salesRepo.getBeansNP("SELECT "+ salesRepo.getFields()+" FROM "+
                        SalesLeadNote.getTableNameStatic()
                        +" WHERE user_id=:user_id AND sales_entity_id=:entity_id"
                , new String[]{"user_id","entity_id"}, new Object[]{userId,entityId});
        return res;
    }

    public List<SalesLeadNote>  findByUserAndEntityIdAfterDate(int userId,int entityId, LocalDate df){
        java.sql.Timestamp date = java.sql.Timestamp.valueOf(df.atStartOfDay());
        List<SalesLeadNote> res = salesRepo.getBeansNP("SELECT "+ salesRepo.getFields()+" FROM "+
                        SalesLeadNote.getTableNameStatic()
                        +" WHERE user_id=:user_id AND sales_entity_id=:entity_id" +
                        " and created>:date"
                , new String[]{"user_id", "entity_id","date"},
                new Object[]{userId, entityId, date});
        return res;
    }

    public Optional<SalesLeadNote> getById(int uid, int id) {
        List<SalesLeadNote> res = salesRepo.getBeansNP("SELECT "+ salesRepo.getFields()+" FROM "+SalesLeadNote.getTableNameStatic()
                +" WHERE id=:id AND user_id=:uid", new String[]{"id","uid"}, new Object[]{id, uid});
        return res.stream().findFirst();
    }

    public boolean update(SalesLeadNote ts){
        boolean ok = true;
        try {
            salesRepo.deleteBeanById(new MultiId(ts.getId()));
            salesRepo.storeBean(ts);
        } catch (Exception e){
            LOGGER.warn("Error updating sales Note",e);
            return false;
        }
        return ok;
    }

    public int  create(SalesLeadNote ts){
        try {
            return salesRepo.storeBean( ts).getId();
        } catch (Exception e){
            LOGGER.warn("Error creating twitter search record",e);
            return 0;
        }
    }

}
