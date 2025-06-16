package kr.co.danal.test2.repository;

import kr.co.danal.test2.entity.UploadData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadDataRepository extends JpaRepository<UploadData, Long> {
}