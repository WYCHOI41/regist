package kr.co.danal.test2.service;

import kr.co.danal.test2.entity.UploadData;
import kr.co.danal.test2.exception.InvalidExcelFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExcelService {

    public List<UploadData> parseExcel(MultipartFile file) throws Exception {
        List<UploadData> list = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            // 최소한 9개 컬럼 존재하는지 체크
            Row headerRow = sheet.getRow(0);
            if (headerRow == null || headerRow.getLastCellNum() < 9) {
                throw new InvalidExcelFormatException("엑셀 파일 헤더 컬럼 수가 부족합니다.");
            }

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                // 필수 컬럼 중 하나라도 비었으면 예외 발생
                if (getCellString(row.getCell(0)) == null || getCellString(row.getCell(1)) == null) {
                    throw new InvalidExcelFormatException("필수 컬럼에 빈 값이 있습니다. (tid 또는 orderid)");
                }

                UploadData data = new UploadData();

                data.setTid(getCellString(row.getCell(0)));
                data.setOrderid(getCellString(row.getCell(1)));

                String tdate = getCellDateString(row.getCell(2));
                String cdate = getCellDateString(row.getCell(3));
                if (tdate == null) {
                    throw new InvalidExcelFormatException("tdate 값이 올바르지 않습니다. (yyyyMMdd 형식)");
                }

                data.setTdate(tdate);
                data.setCdate((cdate != null && !cdate.isEmpty()) ? cdate : tdate);

                data.setInPdate(getCellDateString(row.getCell(4)));

                data.setAmt(getCellDecimal(row.getCell(5)));
                data.setFee(getCellDecimal(row.getCell(6)));
                data.setCpid(getCellString(row.getCell(7)));
                data.setCpnm(getCellString(row.getCell(8)));

                list.add(data);
            }
        } catch (InvalidExcelFormatException e) {
            // 커스텀 예외는 그대로 던지기
            throw e;
        } catch (Exception e) {
            // 다른 모든 예외는 포맷 문제로 간주하여 커스텀 예외로 변환
            throw new InvalidExcelFormatException("엑셀 파일을 읽는 중 오류 발생: " + e.getMessage());
        }

        return list;
    }

    private String getCellString(Cell cell) {
        return (cell != null) ? cell.toString().trim() : null;
    }

    private String getCellDateString(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            Date date = cell.getDateCellValue();
            return new SimpleDateFormat("yyyyMMdd").format(date);
        } else if (cell.getCellType() == CellType.STRING) {
            String raw = cell.getStringCellValue().trim();
            if (raw.matches("\\d{8}")) return raw;
        }
        return null;
    }

    private BigDecimal getCellDecimal(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }
        try {
            return new BigDecimal(cell.toString().trim());
        } catch (Exception e) {
            return null;
        }
    }
}