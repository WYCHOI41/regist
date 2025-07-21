package kr.co.danal.test2.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import kr.co.danal.test2.entity.UploadData;
import kr.co.danal.test2.repository.UploadDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 *  ▷ CSV 업로드 → DB 저장 → 두 개의 SP 호출을 담당하는 서비스
 */
@Slf4j
@Service
public class SpService {

    private final EntityManager entityManager;
    private final UploadDataRepository uploadDataRepository;
    private final CsvService csvService;          // ← 소문자로 교정

    public SpService(EntityManager entityManager,
                     UploadDataRepository uploadDataRepository,
                     CsvService csvService) {     // ← 파라미터 이름도 교정
        this.entityManager = entityManager;
        this.uploadDataRepository = uploadDataRepository;
        this.csvService = csvService;
    }

    /* ------------------------- 단일 SP ------------------------- */

    @Transactional
    public void executeDmlProcedure() {
        log.info("[SP 호출] dml_procedure_name 시작");
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("dml_procedure_name");
        query.execute();
        log.info("[SP 호출] dml_procedure_name 완료");
    }

    @Transactional
    public void executeMyProcedure() {
        log.info("[SP 호출] test2_sp 시작");
        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("test2_sp");
        query.execute();
        log.info("[SP 호출] test2_sp 완료");
    }

    /* ------------------------- 두 개 SP 연속 실행 ------------------------- */

    @Transactional
    public void executeTwoProcedures() {
        log.info("[SP 호출] 두 프로시저 순차 실행 시작");
        executeDmlProcedure();
        executeMyProcedure();
        log.info("[SP 호출] 두 프로시저 순차 실행 완료");
    }

    /* ------------------------- CSV 업로드 + SP ------------------------- */

    /**
     * CSV 파일 한 개를 업로드 → DB 저장 → 두 개의 SP를 순차 호출
     *
     * @param file MultipartFile(텍스트 CSV, UTF‑8)
     * @return 처리 결과 메시지
     */
    @Transactional
    public String uploadCsvAndProcess(MultipartFile file) {
        try {
            // 1. CSV 파싱
            List<UploadData> dataList = csvService.parseCsv(file);

            // 2. 기존 데이터 비우고 새 데이터 저장
            uploadDataRepository.deleteAll();
            uploadDataRepository.saveAll(dataList);

            // 3. SP 두 개 실행
            executeTwoProcedures();

            return "작업 성공: CSV 저장 및 SP 호출 완료";
        } catch (Exception e) {
            log.error("CSV 처리 중 오류 발생", e);
            return "작업 실패: " + e.getMessage();
        }
    }
}