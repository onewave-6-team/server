package org.example.explog.repository;

import org.example.explog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 중복 가입 체크 및 로그인 시 사용
    Optional<User> findByEmail(String email);
}