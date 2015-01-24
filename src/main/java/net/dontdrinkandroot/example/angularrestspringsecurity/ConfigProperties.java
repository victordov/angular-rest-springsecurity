package net.dontdrinkandroot.example.angularrestspringsecurity;

public interface ConfigProperties {
    public final static String PROPERTY_SOURCE = "classpath:application.properties";

    public final static String BASE_PACKAGES = "net.dontdrinkandroot.example.angularrestspringsecurity";
    public final static String ENTITY_PACKAGE = "net.dontdrinkandroot.example.angularrestspringsecurity.entity";

    public final static String DB_DRIVER = "db.driver";
    public final static String DB_URL = "db.url";
    public final static String DB_USER_NAME = "db.username";
    public final static String DB_PASSWORD = "db.password";


    public final static String HIBERNATE_DIALECT = "hibernate.dialect";
    public final static String HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
    public final static String HIBERNATE_EJB_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
    public final static String HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    public final static String HIBERNATE_FORMAT_SQL = "hibernate.format_sql";

}
