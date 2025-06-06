package com.ecommerce.user.repository;


import com.ecommerce.user.models.Users;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UsersRepository extends MongoRepository<Users,String> {
}
