package com.shoaib.authservice.repository;

import com.shoaib.authservice.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    @Modifying
    @Query("""
    UPDATE Address a
    SET a.defaultAddress = false
    WHERE a.userId = :userId
    AND a.defaultAddress = true
    """)
    int clearDefaultAddress(UUID userId);

    List<Address> findByUserId(UUID userId);

    Optional<Address> findByIdAndUserId(UUID addressId, UUID userId);

    @Query(value = """
    SELECT *
    FROM addresses
    WHERE user_id = :userId
      AND is_default = true
    """, nativeQuery = true)
    Optional<Address> findByUserIdAndDefaultAddress(
            @Param("userId") UUID userId
    );
}
