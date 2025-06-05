package com.ecommerce.user.repository;


import com.ecommerce.user.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users,Long> {
}
