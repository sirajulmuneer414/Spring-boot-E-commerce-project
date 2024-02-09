package com.sirajul.lenscraft.Repository;

import com.sirajul.lenscraft.DTO.UserInformationDto;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.entity.user.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserInformation, UUID> {

    boolean existsByEmailId(String emailId);

    UserInformation findByEmailId(String emailId);

    List<UserInformation> findByRole(Role role);

    List<UserInformationDto> findAllByFirstNameContaining(String keyword);

    Long countByRole(Role role);

    boolean existsByReferralCodeIgnoreCase(String code);

    UserInformation findByReferralCodeIgnoreCase(String referralCode);
}
