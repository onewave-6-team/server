package org.example.explog.repository;

import org.example.explog.domain.Folder;
import org.example.explog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findAllByUserOrderByUpdatedAtDesc(User user);
}