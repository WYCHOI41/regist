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

@Slf4j
@Service
public class SpService {

    private final EntityManager entityManager;
    private final UploadDataRepository uploadDataRepository;
    private final ExcelService excelService;

    public SpService(EntityManager entityManager, UploadDataRepository uploadDataRepository, ExcelService excelService) {
        this.entityManager = entityManager;
        this.uploadDataRepository = uploadDataRepository;
        this.excelService = excelService;
    }

    @Transactional
    public void executeDmlProcedure() {
        log.info("[SP 호출] executeDmlProcedure 시작 - dml_procedure_name");
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("dml_procedure_name");
        query.execute();
        log.info("[SP 호출] executeDmlProcedure 완료");
    }

    @Transactional
    public void executeMyProcedure() {
        log.info("[SP 호출] executeMyProcedure 시작 - test2_sp");
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("test2_sp");
        query.execute();
        log.info("[SP 호출] executeMyProcedure 완료");
    }

    @Transactional
    public void executeTwoProcedures() {
        log.info("[SP 호출] executeTwoProcedures 시작");
        executeDmlProcedure();
        executeMyProcedure();
        log.info("[SP 호출] executeTwoProcedures 완료");
    }

    // 엑셀 업로드 -> DB 저장 -> SP 호출까지 한 번에 처리하는 메서드
    @Transactional
    public String uploadExcelAndProcess(MultipartFile file) {
        try {
            // 1. 엑셀 파일 파싱
            List<UploadData> dataList = excelService.parseExcel(file);

            // 2. DB에 저장
            uploadDataRepository.saveAll(dataList);

            // 3. SP 두 개 순차 호출
            executeTwoProcedures();

            return "작업 성공: 엑셀 저장 및 SP 호출 완료";
        } catch (Exception e) {
            log.error("엑셀 처리 중 오류 발생", e);
            return "작업 실패: " + e.getMessage();
        }
    }
}