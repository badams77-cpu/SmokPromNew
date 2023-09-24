package com.smokpromotion.SmokProm.domain.repository;

import com.smokpromotion.SmokProm.config.DBs.SmokDatasourceName;
import com.smokpromotion.SmokProm.domain.entity.BaseSmokEntity;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.util.SQLHelper;

import jakarta.persistence.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import jakarta.persistence.Column;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class MajoranaAnnotationRepository<T extends BaseSmokEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MajoranaAnnotationRepository.class);

    private static final String PACKAGE_BASE = "com.smokpromotion.SmokProm";

    protected Class<T> clazz;

    protected MajoranaDBConnectionFactory dbFactory;

    protected List<MajoranaRepositoryField> repoFields = new LinkedList<>();

    protected MajoranaAnnotationRepository(MajoranaDBConnectionFactory dbFactory, Class<T> clazz){
        this.dbFactory = dbFactory;
        this.clazz =clazz;
        setFieldsByReflection(clazz);
    }

    protected String getCreateString(T sUser){
        StringBuffer buffy =  new StringBuffer();
        SqlParameterSource params = getSqlParameterSource(sUser);
        buffy.append("("+ repoFields.stream().map(x->x.getField().getName()).collect(Collectors.joining(",") )+ ")");
        buffy.append(" VALUES ("+ repoFields.stream()
                .map(x->x.isPopulatedCreated() || x.isPopulatedUpdated()? "now()": x.getField().getName())
                .collect(Collectors.joining(",") )+ ");");
        return buffy.toString();
    }

    protected String getUpdateString(T sUser){
        StringBuffer buffy =  new StringBuffer();
        buffy.append(" SET "+ repoFields.stream().filter(x->x.isUpdateable())
                .map(x->x.getField().getName() + ":" + ((x.isPopulatedUpdated())?"now() " : ":"+x.getField().getName()))
                .collect(Collectors.joining(",") )+ " WHERE id=:id");
        return buffy.toString();
    }

    private void setFieldsByReflection(Class clazz){
        List<Field> fields = getClassFields(clazz);
        List<Method> methods = getClassMethods(clazz);
        for(Field field : fields){
            Annotation[] annotations = field.getDeclaredAnnotations();
            boolean toAdd = false;
            MajoranaRepositoryField majoranaField = new MajoranaRepositoryField();
            majoranaField.setField(field);
            majoranaField.setValueType(field.getType());
            majoranaField.setName(field.getName());
            majoranaField.setDbColumn(field.getName());
            boolean updateable = false;
            boolean popCreated = false;
            boolean popUpdated = false;
            for(Annotation ann : annotations){
                if (ann.annotationType().equals(Updateable.class)){
                    updateable = true;
                }
                if (ann.annotationType().equals(Updateable.class)){
                    popCreated = true;
                }
                if (ann.annotationType().equals(Updateable.class)){
                    popUpdated = true;
                }
                if (ann.annotationType().equals(jakarta.persistence.Column.class)){
                    toAdd=true;
                    Column column = field.getAnnotation(Column.class);
                    String dbField = column.name();
                    if (dbField!=null){
                        majoranaField.setDbColumn(dbField);
                    }
                    majoranaField.setColumnAnnotation(column);
                }
            }
            majoranaField.setUpdateable(updateable);
            majoranaField.setPopulatedCreated(popCreated);
            majoranaField.setPopulatedUpdated(popUpdated);
            for(Method method: methods){
                if (isGetter(method) && method.getName().equalsIgnoreCase("GET"+field.getName())
                        || method.getName().equalsIgnoreCase("IS"+field.getName().toUpperCase())
                        || ( method.getName().equalsIgnoreCase(field.getName().toUpperCase()) )
                ){
                    majoranaField.setGetter(method);
                } else if (isSetter(method) && method.getName().equalsIgnoreCase("SET"+field.getName())) {
                    majoranaField.setSetter(method);
                }
            }
            boolean haveRequiredData = majoranaField.checkFields();
            if (toAdd && haveRequiredData) {
                repoFields.add(majoranaField);
            } else if (!haveRequiredData){
                LOGGER.warn("setFieldsByReflection: Class: "+clazz.getName()+" Field: "+majoranaField.getName()+" missing info "+majoranaField.getMissing());
            }
        }
    }



    protected SqlParameterSource getSqlParameterSource(T entity){
        return new MapSqlParameterSource(getParameterMap(entity));
    }

    protected SqlParameterSource getSqlParameterSourceWithDeletedAt(SmokDatasourceName sDn,T entity){
        Map<String, Object> sourceMap =getParameterMap(entity);
        sourceMap.put("deleted_at", (Boolean) sourceMap.getOrDefault("deleted",false)?  dbFactory.getDBTime(sDn) : SQLHelper.BLANK_TIMESTAMP);
        return new MapSqlParameterSource(sourceMap);
    }

    protected java.sql.Timestamp getDeletedA(BaseSmokEntity bse){
        return java.sql.Timestamp.valueOf (bse.isDeleted() ? bse.getDeletedAt() : SQLHelper.BLANK_TIMESTAMP);
    }

    private Map<String, Object> getParameterMap(T entity){
        Map<String, Object> sourceMap = new HashMap<>();
        for(MajoranaRepositoryField field : repoFields){
            try {
                Object ob = invokeGetter(entity, field.getGetter());
                if (ob!=null) {
                    if (ob instanceof LocalDate) {
                        ob = java.sql.Date.valueOf((LocalDate) ob);
                    } else if (ob instanceof LocalTime) {
                        ob = java.sql.Time.valueOf((LocalTime) ob);
                    } else if (ob instanceof LocalDateTime) {
                        ob = java.sql.Timestamp.valueOf((LocalDateTime) ob);
                    } else if (field.getValueType().isEnum()) {
                        ob = ((Enum) ob).name();
                    } else {
                        // Default
                    }
                }
                sourceMap.put(field.getDbColumn(), ob);
            } catch (Exception e){
                Exception f=e;
                LOGGER.warn("getSqlParametersource: Error Serializing field "+field.getName()+" "+field.getDbColumn(),f);

            }
        }
        return sourceMap;
    }


    public RowMapper<T> getMapper(){
        return new RepositoryFieldMapper();
    }

    public RowMapper<Integer> getIntegerMapper(){
        return new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(1);
            }
        };
    }

    protected class RepositoryFieldMapper implements RowMapper<T> {

        @Override
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                T entity = null;
                try {
                  entity = clazz.newInstance();
                } catch (IllegalAccessException e) {
                 LOGGER.warn("New Instance "+clazz+"IllegalAccess: ",e);
                  } catch (InstantiationException e) {
                    LOGGER.warn("New Instance "+clazz+"InstantiationException ", e);
                }
                for(MajoranaRepositoryField field : repoFields){
                    Method setter = field.getSetter();
                    String col = field.getDbColumn();
                    try {
                        if (field.getValueType().isEnum()) {
                            try {
                                Method valueOf = field.getValueType().getMethod("valueOf", String.class);
                                Object value = valueOf.invoke(null, rs.getString(col));
                                invokeSetter(entity, value, setter);
                            } catch (Exception e){
                                Method valueOf = field.getValueType().getMethod("fromString", String.class);
                                Object value = valueOf.invoke(null, rs.getString(col));
                                invokeSetter(entity, value, setter);
                            }
                        } else {
                            switch( field.getValueType().getName()) {
                                case "int":
                                    invokeSetter(entity, rs.getInt(col), setter);
                                    break;
                                case "long":
                                    invokeSetter(entity, rs.getLong(col), setter);
                                    break;
                                case "float":
                                    invokeSetter(entity, rs.getFloat(col), setter);
                                    break;
                                case "double":
                                    invokeSetter(entity, rs.getDouble(col), setter);
                                    break;
                                case "boolean":
                                    invokeSetter(entity, rs.getBoolean(col), setter);
                                    break;
                                case "java.lang.Integer":
                                    invokeSetter(entity, rs.getInt(col), setter);
                                    if (rs.wasNull()){ invokeSetter(entity, null, setter); }
                                    break;
                                case "java.lang.Long":
                                    invokeSetter(entity, rs.getLong(col), setter);
                                    if (rs.wasNull()){ invokeSetter(entity, null, setter); }
                                    break;
                                case "java.lang.Float":
                                    invokeSetter(entity, rs.getFloat(col), setter);
                                    if (rs.wasNull()){ invokeSetter(entity, null, setter); }
                                    break;
                                case "java.lang.Double":
                                    invokeSetter(entity, rs.getDouble(col), setter);
                                    if (rs.wasNull()){ invokeSetter(entity, null, setter); }
                                    break;
                                case "java.lang.Boolean":
                                    invokeSetter(entity, rs.getBoolean(col), setter);
                                    if (rs.wasNull()){ invokeSetter(entity, null, setter); }
                                    break;
                                case "java.lang.String":
                                    invokeSetter(entity, rs.getString(col), setter);
                                    break;
                                case "java.time.LocalDate":
                                    java.sql.Date date = rs.getDate(col);
                                    LocalDate ld = date != null ? date.toLocalDate() : null;
                                    invokeSetter(entity, ld, setter);
                                    break;
                                case "java.time.LocalTime":
                                    java.sql.Time time = rs.getTime(col);
                                    LocalTime lt = time != null ? time.toLocalTime() : null;
                                    invokeSetter(entity, lt, setter);
                                    break;
                                case "java.time.LocalDateTime":
                                    java.sql.Timestamp timestamp = rs.getTimestamp(col);
                                    LocalDateTime ldt = timestamp != null ? timestamp.toLocalDateTime() : null;
                                    invokeSetter(entity, ldt, setter);
                                    break;
                                default:
                                    LOGGER.warn("mapRow: Unknown column type" + field.getValueType().getName());
                                    break;
                            }

                        }

                    } catch (Exception e){
                        LOGGER.warn("mapRow: Error deserializing field "+field.getName()+" "+field.getDbColumn(),e);
                    }
                }
                return entity;

        }
    }

    private static boolean isGetter(Method method){
        // check for getter methods
        if((method.getName().startsWith("get") || method.getName().startsWith("is"))
                && method.getParameterCount() == 0 && !method.getReturnType().equals(void.class)){
            return true;
        }
        return false;
    }

    private static boolean isSetter(Method method){
        // check for setter methods
        if(method.getName().startsWith("set") && method.getParameterCount() == 1
                && method.getReturnType().equals(void.class)){
            return true;
        }
        return false;
    }

    public void invokeSetter(Object obj,Object variableValue,Method setter) throws IllegalAccessException, IllegalArgumentException , InvocationTargetException
    {
            setter.invoke(obj,variableValue);
    }

    public Object invokeGetter(Object obj,Method getter) throws IllegalAccessException, IllegalArgumentException , InvocationTargetException
    {
            Object f = getter.invoke(obj);
            return f;
    }

    private List<Field> getClassFields(Class clazz){
        Field[] fields = clazz.getDeclaredFields();
        List<Field> out = new LinkedList<Field>();
        for(Field field: fields){
            out.add(field);
        }
        Class supClass = clazz.getSuperclass();
        while (supClass.getPackage().getName().contains(PACKAGE_BASE)){
            Field[] supFields = supClass.getDeclaredFields();
            for(Field field: supFields){
                out.add(field);
            }
            supClass = supClass.getSuperclass();
        }
        return out;
    }

    private List<Method> getClassMethods(Class clazz){
        Method[] methods = clazz.getDeclaredMethods();
        List<Method> out = new LinkedList<Method>();
        for(Method method : methods){
            out.add(method);
        }
        Class supClass = clazz.getSuperclass();
        while (supClass.getPackage().getName().contains(PACKAGE_BASE)){
            Method[] supMethods = supClass.getDeclaredMethods();
            for(Method method : supMethods){
                out.add(method);
            }
            supClass = supClass.getSuperclass();
        }
        return out;
    }

}
