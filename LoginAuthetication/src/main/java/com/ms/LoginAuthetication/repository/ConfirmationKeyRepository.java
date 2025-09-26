package com.ms.LoginAuthetication.repository;

import com.ms.LoginAuthetication.model.ConfirmationKey;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ConfirmationKeyRepository extends MongoRepository<ConfirmationKey, String> {
    Optional<ConfirmationKey> findByKey(String key);
}
