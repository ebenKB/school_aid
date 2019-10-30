package com.hub.schoolAid;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.io.Serializable;

public class PhysicalNamingStrategyImpl extends PhysicalNamingStrategyStandardImpl implements Serializable {
    public static final PhysicalNamingStrategyImpl INSTANCE = new PhysicalNamingStrategyImpl();

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
//        return super.toPhysicalTableName(name, context);
        return new Identifier(toLower(name.getText()), name.isQuoted());
    }


    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {

        return super.toPhysicalColumnName(name, context);
    }

    protected static String toLower(String name) {
        return name.toLowerCase();
    }
}

