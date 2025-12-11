package com.example.CapstonProject0.Controller;

import com.example.CapstonProject0.Service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    @GetMapping("/info")
    public String showFinanceInfo() {
        return "EI/Finance-information";
    }

    @GetMapping("/data")
    @ResponseBody
    public Map<String, Object> getFinanceData() {
        // ✅ 환율 데이터 (Frankfurter API)
        return financeService.getLatestData();
    }

    @GetMapping("/news")
    @ResponseBody
    public Map<String, Object> getNews() {
        String apiKey = "25b520b4db864013b6d5086b8352ab47";
        String url = "https://newsapi.org/v2/everything?q=금융&language=ko&apiKey=" + apiKey;

        Map<String, Object> result = new HashMap<>();
        try {
            RestTemplate rest = new RestTemplate();
            ResponseEntity<Map> response = rest.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                result.put("error", "News API response code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            result.put("error", "API call failed");
            result.put("message", e.getMessage());
        }
        return result;
    }

}

