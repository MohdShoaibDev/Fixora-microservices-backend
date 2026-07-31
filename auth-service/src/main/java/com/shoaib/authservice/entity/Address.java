package com.shoaib.authservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "addresses",
        indexes = {
                @Index(
                        name = "idx_address_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_address_user_default",
                        columnList = "user_id, is_default"
                )
        }
)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 15)
    private String mobile;

    @Column(name = "house_number", nullable = false, length = 100)
    private String houseNumber;

    @Column(length = 150)
    private String building;

    @Column(nullable = false, length = 200)
    private String street;

    @Column(length = 200)
    private String landmark;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(nullable = false, length = 10)
    private String pincode;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean defaultAddress = false;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static Address create(
            UUID userId,
            String fullName,
            String mobile,
            String houseNumber,
            String building,
            String street,
            String landmark,
            String city,
            String state,
            String pincode,
            String country,
            Double latitude,
            Double longitude,
            boolean defaultAddress
    ) {
        return Address.builder()
                .userId(userId)
                .fullName(fullName)
                .mobile(mobile)
                .houseNumber(houseNumber)
                .building(building)
                .street(street)
                .landmark(landmark)
                .city(city)
                .state(state)
                .pincode(pincode)
                .country(country)
                .latitude(latitude)
                .longitude(longitude)
                .defaultAddress(defaultAddress)
                .build();
    }

    public void update(
            String fullName,
            String mobile,
            String houseNumber,
            String building,
            String street,
            String landmark,
            String city,
            String state,
            String pincode,
            String country,
            Double latitude,
            Double longitude,
            boolean defaultAddress
    ) {
        this.fullName = fullName;
        this.mobile = mobile;
        this.houseNumber = houseNumber;
        this.building = building;
        this.street = street;
        this.landmark = landmark;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.defaultAddress = defaultAddress;
    }

    public void markAsDefault() {
        this.defaultAddress = true;
    }

    public void removeDefault() {
        this.defaultAddress = false;
    }
}