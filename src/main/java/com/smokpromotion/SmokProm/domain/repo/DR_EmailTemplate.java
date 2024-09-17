package com.smokpromotion.SmokProm.domain.repo;

import com.majorana.maj_orm.ORM.MajoranaDBConnectionFactory;
import com.majorana.maj_orm.ORM_ACCESS.DbBean;
import com.majorana.maj_orm.ORM_ACCESS.DbBeanGenericInterface;
import com.smokpromotion.SmokProm.domain.entity.DE_EmailTemplate;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.urcompliant.domain.PortalEnum;
import com.urcompliant.domain.entity.DE_EmailTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Repository for Email Templates
 */
@Lazy
@Repository
public class DR_EmailTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(DR_EmailTemplate.class);

    private static final String TABLE_NAME = "email_templates";

    private DbBeanGenericInterface<DE_EmailTemplate> emailRepo = null;
    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    public DR_EmailTemplate(){
        DbBean dBean = new DbBean();
        try {
            dBean.connect();
            emailRepo = dBean.getTypedBean(DE_EmailTemplate.class);
        } catch (ClassNotFoundException | SQLException e){
            LOGGER.error("Class S_User not found");
        }
    }


    // -----------------------------------------------------------------------------------------------------------------
    // Public Methods
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * List all email templates in the portal
     * @param legacyPortal The portal db on which the practice group exists.
     * @return Optional DE_EmailTemplate the empty template
     */
    public List<DE_EmailTemplate> getAll() {

        Optional<DE_EmailTemplate> opt = Optional.empty();

        List<DE_EmailTemplate> matches =
                emailRepo.getBeans("SELECT * FROM "
                        +DE_EmailTemplate.getTableNameStatic());

        return matches;
    }

    /**
     * Retrieve the Email Template with given id
     * @param legacyPortal The portal db on which the practice group exists.
     * @param id The template id.
     * @return Optional DE_EmailTemplate the empty template
     */
    public Optional<DE_EmailTemplate> getById(PortalEnum legacyPortal, int id) {

        Optional<DE_EmailTemplate> opt = Optional.empty();

        List<DE_EmailTemplate> matches = dbFactory.getJdbcTemplate(legacyPortal).map(template->template.query(
                "SELECT id,name,template,subject,language FROM "+TABLE_NAME+" WHERE id = ?",
                new Object[]{id},
                new EmailTemplateMapper())).orElse(new LinkedList<>());

        if (!matches.isEmpty()) {
            opt = Optional.of(matches.get(0));
        }

        return opt;

    }

    /**
     * Retrieve the email template for given name and language
     * @param legacyPortal The portal db on which the practice group exists.
     * @param name The template group name.
     * @return Optional DE_EmailTemplate the empty template
     */
    public Optional<DE_EmailTemplate> getByNameAndLanguage(PortalEnum legacyPortal, String name, String language) {

        Optional<DE_EmailTemplate> opt = Optional.empty();

        List<DE_EmailTemplate> matches = dbFactory.getJdbcTemplate(legacyPortal).map(template->template.query(
                "SELECT id,name,template,subject,language FROM "+TABLE_NAME+" WHERE name = ? and language=?",

                new Object[]{name, language},
                new EmailTemplateMapper())).orElse(new LinkedList<>());

        if (!matches.isEmpty()) {
            opt = Optional.of(matches.get(0));
        }

        return opt;

    }

    /**
     * Create the email template
     * @param legacyPortal The portal db on which the practice group exists.
     * @param template The template object to insert
     * @return boolean Indication of success or failure.
     */
    public boolean create(PortalEnum legacyPortal, DE_EmailTemplate template) {

        String sql = "INSERT INTO "+TABLE_NAME+" (name, template, subject, language) values (:name, :template, :subject, :language)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource params = generateSqlParameterSource(template,false);
        int changed = dbFactory.getNamedParameterJdbcTemplate(legacyPortal).map(db->db.update(
                sql, params,
                keyHolder)).orElse(0);
        if (keyHolder.getKey()==null){
            LOGGER.error("Could not create email template database row");
            return false;
        }
        template.setId(keyHolder.getKey().intValue());
        return changed==1;
    }
    /**
     * Update the email template
     * @param legacyPortal The portal db on which the practice group exists.
     * @param template The template object to insert
     * @return boolean Indication of success or failure.
     */
    public boolean update(PortalEnum legacyPortal, DE_EmailTemplate template) {

        String sql = "UPDATE "+TABLE_NAME+" SET name=:name, template=:template, subject=:subject, language=:language where id=:id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource params = generateSqlParameterSource(template,true);
        int changed = dbFactory.getNamedParameterJdbcTemplate(legacyPortal).map(db->db.update(
                sql, params,
                keyHolder)).orElse(0);
        return changed==1;
    }




    // -----------------------------------------------------------------------------------------------------------------
    // Protected Methods
    // -----------------------------------------------------------------------------------------------------------------



    // -----------------------------------------------------------------------------------------------------------------
    // Class: EmailTemplateMapper
    // -----------------------------------------------------------------------------------------------------------------

}
