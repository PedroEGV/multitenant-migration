package com.pedroegv.multitenantmigration.controller;

import com.pedroegv.multitenantmigration.entity1.CustomerEntity;
import com.pedroegv.multitenantmigration.entity2.CustomerEntity2;
import com.pedroegv.multitenantmigration.service.MigrationService;
import com.pedroegv.multitenantmigration.service.MutiTenantEntityManagerProvider;
import com.pedroegv.multitenantmigration.service.SecondaryMutiTenantEntityManagerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.LinkedList;
import java.util.List;

@RestController
public class MyController {

    @Value("${multitenant.tenant-names}")
    private List<String> tenantNames;
    @Autowired
    private MutiTenantEntityManagerProvider entityManagerProvider;
    @Autowired
    private MigrationService migrationService;
    @Autowired
    private SecondaryMutiTenantEntityManagerProvider secondaryEntityManagerProvider;

    @GetMapping("/customer")
    public List<CustomerEntity> getData() {
        List<CustomerEntity> response = new LinkedList<>();

        for (String tenantName : tenantNames) {
            EntityManager em = entityManagerProvider.getEntityManager(tenantName);
            TypedQuery<CustomerEntity> query = em.createQuery("SELECT c FROM CustomerEntity c", CustomerEntity.class);
            response.addAll(query.getResultList());
        }

        return response;
    }

    @GetMapping("/customer2")
    public List<CustomerEntity2> getData2() {
        List<CustomerEntity2> response = new LinkedList<>();

        for (String tenantName : tenantNames) {
            EntityManager em = secondaryEntityManagerProvider.getEntityManager(tenantName);
            TypedQuery<CustomerEntity2> query = em.createQuery("SELECT c FROM CustomerEntity2 c", CustomerEntity2.class);
            response.addAll(query.getResultList());
        }

        return response;
    }

    @PostMapping("/customer/migrate")
    public List<CustomerEntity> migrate() {
        return migrationService.migrate();
    }
}
