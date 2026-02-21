package com.cmm.mit.repo;

import com.cmm.mit.domain.entity.UserSettings;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSettingsRepo extends JpaRepository<UserSettings, UUID> {}
