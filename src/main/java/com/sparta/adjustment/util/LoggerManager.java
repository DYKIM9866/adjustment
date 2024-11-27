package com.sparta.adjustment.util;



import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class LoggerManager {
    public static void disableSqlLogging(){
        Logger sqlLogger = (Logger)LoggerFactory.getLogger("org.hibernate.SQL");
        Logger binderLogger = (Logger) LoggerFactory.getLogger("org.hibernate.type.descriptor.sql.BasicBinder");

        sqlLogger.setLevel(Level.OFF);
        binderLogger.setLevel(Level.OFF);
    }

    public static void enableSqlLogging(){
        Logger sqlLogger = (Logger)LoggerFactory.getLogger("org.hibernate.SQL");
        Logger binderLogger = (Logger) LoggerFactory.getLogger("org.hibernate.type.descriptor.sql.BasicBinder");

        sqlLogger.setLevel(Level.DEBUG);
        binderLogger.setLevel(Level.DEBUG);
    }
}
