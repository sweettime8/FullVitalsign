package com.elcom.rbac.repository;

import com.elcom.rbac.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Admin
 */
@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
}
