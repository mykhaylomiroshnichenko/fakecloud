package com.fakecloud.repository.impl;

import com.fakecloud.model.Machine;
import com.fakecloud.model.MachineStatus;
import com.fakecloud.model.SearchMachineFilter;
import com.fakecloud.repository.MachineRepositoryCustom;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MachineRepositoryCustomImpl implements MachineRepositoryCustom {

    @PersistenceContext
    protected EntityManager entityManager;

    @Override
    public List<Machine> search(SearchMachineFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Machine> criteriaQuery = cb.createQuery(Machine.class);
        Root<Machine> root = criteriaQuery.from(Machine.class);
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getStatus() != null && filter.getStatus().size() > 0) {
            Expression<MachineStatus> parentExpression = root.get("status");
            predicates.add(parentExpression.in(filter.getStatus()));
        }

        if (filter.getDateFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), (Comparable) filter.getDateFrom()));
        }

        if (filter.getDateTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), (Comparable) filter.getDateTo()));
        }

        criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()]))
                .orderBy(cb.desc(root.get("createdAt")));;

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
