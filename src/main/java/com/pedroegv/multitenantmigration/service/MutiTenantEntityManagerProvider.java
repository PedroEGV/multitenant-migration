package com.pedroegv.multitenantmigration.service;

import com.pedroegv.multitenantmigration.config.MultiTenantDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@Component
public class MutiTenantEntityManagerProvider {

    @Autowired
    private EntityManagerFactory emf;

    public synchronized EntityManager getEntityManager(String tenantName) {
        MultiTenantDataSource.TenantContext.setCurrentSchema(tenantName);
        EntityManager em = emf.createEntityManager();
        return em;
    }
}
