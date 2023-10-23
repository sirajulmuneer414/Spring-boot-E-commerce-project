package com.sirajul.lenscraft.Repository;

import com.sirajul.lenscraft.entity.user.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserInformation, UUID> {

    boolean ExistsByEmailId(String emailId);
}
