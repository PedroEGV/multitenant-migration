package com.pedroegv.multitenantmigration.service;

import com.pedroegv.multitenantmigration.config.MultiTenantDataSource;
import com.pedroegv.multitenantmigration.entity1.CustomerEntity;
import com.pedroegv.multitenantmigration.entity2.CustomerEntity2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.LinkedList;
import java.util.List;

@Service
public class MigrationService {

    @Value("${multitenant.tenant-names}")
    private List<String> tenantNames;
    @Autowired
    private MutiTenantEntityManagerProvider entityManagerProvider;
    @Autowired
    private SecondaryMutiTenantEntityManagerProvider secondaryEntityManagerProvider;

    public List<CustomerEntity> migrate() {
        try {
            List<CustomerEntity> migrated = new LinkedList<>();

            for (String tenantName : tenantNames) {
                EntityManager em = entityManagerProvider.getEntityManager(tenantName);
                TypedQuery<CustomerEntity> query = em.createQuery("SELECT c FROM CustomerEntity c", CustomerEntity.class);
                List<CustomerEntity> customers = query.getResultList();
                saveToSencondary(customers, tenantName);
                migrated.addAll(customers);
            }

            return migrated;
        } finally {
            MultiTenantDataSource.TenantContext.clear();
        }
    }

    private void saveToSencondary(List<CustomerEntity> customers, String tenantName) {
        EntityManager em = secondaryEntityManagerProvider.getEntityManager(tenantName);
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        customers.stream()
                .map(customer -> {
                    CustomerEntity2 customer2 = new CustomerEntity2();
                    customer2.setName(customer.getName());
                    customer2.setEmail(customer.getEmail());
                    return customer2;
                })
                .forEach(em::persist);
        transaction.commit();
    }
}
