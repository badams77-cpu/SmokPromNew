package com.smokpromotion.SmokProm.domain.repo;

import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInterface;
import com.majorana.maj_orm.ORM_ACCESS.MultiId;
import com.smokpromotion.SmokProm.domain.entity.DE_SearchResult;
import com.smokpromotion.SmokProm.domain.entity.VPMessage;
import com.smokpromotion.SmokProm.util.MethodPrefixingLogger;
import com.smokpromotion.SmokProm.util.MethodPrefixingLoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class REP_VPMessage {

    private static MethodPrefixingLogger LOGGER = MethodPrefixingLoggerFactory.getLogger(REP_VPMessage.class);

    private DbBeanGenericInterface<VPMessage> vpMessageRepo = null;

//    private static final int changePasswordTimeOut = 10000;


    public REP_VPMessage(){
        DbBean dBean = new DbBean();
        try {
            dBean.connect();
            vpMessageRepo = dBean.getTypedBean(VPMessage.class);
        } catch (ClassNotFoundException | SQLException e){
            LOGGER.error("Class DE_SearchResult not found");
        }
    }


    public List<VPMessage>  findByUser(int userId){
        List<VPMessage> res = vpMessageRepo.getBeansNP("SELECT "+ vpMessageRepo.getFields()+" FROM "+
                DE_SearchResult.getTableNameStatic()
                +" WHERE from=:user_id OR to=:user_id"
                , new String[]{"user_id"}, new Object[]{userId});
        return res;
    }


    public boolean update(VPMessage ts){
        boolean ok = true;
        try {
            vpMessageRepo.updateBean(new MultiId(ts.getId()), ts);
        } catch (Exception e){
            LOGGER.warn("Error updating user",e);
            return false;
        }
        return ok;
    }

    public int  create(VPMessage ts){
        try {
            return vpMessageRepo.storeBean( ts).getId();
        } catch (Exception e){
            LOGGER.warn("Error creating twitter search record",e);
            return 0;
        }
    }

}
