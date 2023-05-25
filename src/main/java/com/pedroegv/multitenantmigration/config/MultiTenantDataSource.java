package com.pedroegv.multitenantmigration.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class MultiTenantDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        // Retrieve the current schema from a thread-local variable
        return TenantContext.getCurrentSchema();
    }

    public static class TenantContext {
        private static final ThreadLocal<String> currentSchema = new ThreadLocal<>();

        public static String getCurrentSchema() {
            return currentSchema.get();
        }

        public static void setCurrentSchema(String schema) {
            currentSchema.set(schema);
        }

        public static void clear() {
            currentSchema.remove();
        }
    }
}