package kr.co.danal.test2.controller;

import jakarta.validation.constraints.NotNull;
import kr.co.danal.test2.exception.InvalidExcelFormatException;
import kr.co.danal.test2.service.SpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor   // Lombok: 생성자 주입
@Controller
public class UploadController {

    private final SpService spService;      // CSV 파싱 + DB 저장 + SP 호출 전부 맡김

    /* 업로드 폼 페이지 (GET) */
    @GetMapping("/upload")
    public String showUploadForm() {
        return "upload";                    // → templates/upload.html
    }

    /* CSV 업로드 처리 (POST) */
    @PostMapping("/upload")
    public String handleUpload(@RequestParam("file") @NotNull MultipartFile file,
                               Model model) {

        if (file.isEmpty()) {
            model.addAttribute("message", "CSV 파일을 선택하세요.");
            return "upload";
        }

        try {
            String result = spService.uploadCsvAndProcess(file);  // 전체 로직 수행
            model.addAttribute("message", result);                // 성공/실패 메시지
        } catch (InvalidExcelFormatException e) {
            model.addAttribute("message", "CSV 포맷 오류: " + e.getMessage());
        } catch (Exception e) {
            log.error("파일 업로드 중 예외", e);
            model.addAttribute("message", "알 수 없는 오류: " + e.getMessage());
        }
        return "upload";
    }
}