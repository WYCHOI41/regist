package kr.co.danal.test2.controller;

import kr.co.danal.test2.entity.UploadData;
import kr.co.danal.test2.exception.InvalidExcelFormatException;
import kr.co.danal.test2.repository.UploadDataRepository;
import kr.co.danal.test2.service.ExcelService;
import kr.co.danal.test2.service.SpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class UploadController {

    private final ExcelService excelService;
    private final UploadDataRepository uploadDataRepository;
    private final SpService spService;

    @Autowired
    public UploadController(ExcelService excelService,
                            UploadDataRepository uploadDataRepository,
                            SpService spService) {
        this.excelService = excelService;
        this.uploadDataRepository = uploadDataRepository;
        this.spService = spService;
    }

    @GetMapping("/")
    public String showUploadForm() {
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadExcel(@RequestParam("file") MultipartFile file, Model model) {
        try {
            List<UploadData> list = excelService.parseExcel(file);
            uploadDataRepository.saveAll(list);

            spService.executeTwoProcedures();

            model.addAttribute("message", "업로드 및 처리 완료!");
        } catch (InvalidExcelFormatException e) {
            model.addAttribute("message", "엑셀 포맷 오류: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("message", "알 수 없는 오류 발생: " + e.getMessage());
        }
        return "upload";
    }
}