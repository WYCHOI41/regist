package kr.co.danal.test2.service;

import kr.co.danal.test2.entity.UploadData;
import kr.co.danal.test2.exception.InvalidExcelFormatException;
import kr.co.danal.test2.repository.UploadDataRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvService {

    private final UploadDataRepository uploadDataRepository;
    private static final SimpleDateFormat DATE8 = new SimpleDateFormat("yyyyMMdd");

    public CsvService(UploadDataRepository uploadDataRepository) {
        this.uploadDataRepository = uploadDataRepository;
    }

    /** 기존 데이터 전부 삭제 */
    public void deleteAllUploadData() {
        uploadDataRepository.deleteAll();
    }

    /** 탭 구분자 TXT 파싱 */
    public List<UploadData> parseCsv(MultipartFile file) throws Exception {
        List<UploadData> list = new ArrayList<>();

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.TDF      // 탭 구분자
                     .withFirstRecordAsHeader()  // 첫 줄은 헤더
                     .withTrim()
                     .parse(reader)) {

            if (parser.getHeaderMap().size() < 9) {
                throw new InvalidExcelFormatException("파일 헤더 컬럼 수가 부족합니다.");
            }

            for (CSVRecord record : parser) {
                String tid     = record.get(0).trim();
                String orderid = record.get(1).trim();
                if (tid.isEmpty() || orderid.isEmpty()) {
                    throw new InvalidExcelFormatException("필수 컬럼(tid 또는 orderid)에 빈 값이 있습니다.");
                }

                UploadData data = new UploadData();
                data.setTid(tid);
                data.setOrderid(orderid);

                String tdate = parseDate(record.get(2));
                String cdate = parseDate(record.get(3));
                if (tdate == null) {
                    throw new InvalidExcelFormatException("tdate 값이 올바르지 않습니다. (yyyyMMdd 형식)");
                }
                data.setTdate(tdate);
                data.setCdate((cdate != null && !cdate.isEmpty()) ? cdate : tdate);

                data.setInPdate(parseDate(record.get(4)));
                data.setAmt(parseDecimal(record.get(5)));
                data.setFee(parseDecimal(record.get(6)));
                data.setCpid(record.get(7).trim());
                data.setCpnm(record.get(8).trim());

                list.add(data);
            }
        } catch (InvalidExcelFormatException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidExcelFormatException("파일을 읽는 중 오류 발생: " + e.getMessage());
        }

        return list;
    }

    private String parseDate(String raw) {
        if (raw == null) return null;
        raw = raw.trim();
        if (raw.matches("\\d{8}")) {
            try {
                DATE8.parse(raw);
                return raw;
            } catch (ParseException ignore) {
            }
        }
        return null;
    }

    private BigDecimal parseDecimal(String raw) {
        if (raw == null) return null;
        raw = raw.trim();
        try {
            return new BigDecimal(raw.replaceAll(",", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}