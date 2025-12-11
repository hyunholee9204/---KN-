package com.example.CapstonProject0.Service;

import com.example.CapstonProject0.Entity.FinanceEntity;
import com.example.CapstonProject0.Repository.FinanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final FinanceRepository repo;
    private final RestTemplate restTemplate = new RestTemplate();

    // 🔑 API 키들
    private static final String NINJA_KEY = "cRSq//GOwTn/wNNIDXGCsQ==L5R9INNCDawvKJUS"; // 금리용
    private static final String METALS_API_KEY = "YOUR_METALS_API_KEY"; // 금 시세용

    // ✅ 최신 데이터 조회
    public Map<String, Object> getLatestData() {
        Map<String, Object> result = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        // ===== 환율 =====
        double usd = callExchangeRate("USD", "KRW");
        double eur = callExchangeRate("EUR", "KRW");
        double jpy = callExchangeRate("JPY", "KRW");

        // ===== 주식 지수 =====
        Map<String, Double> stockMap = callStockIndex();
        double kospi = stockMap.get("kospi");
        double kosdaq = stockMap.get("kosdaq");

        // ===== 금리 =====
        double krRate = callInterestRate("korea");
        double usRate = callInterestRate("usa");

        // ===== 금 시세 =====
        double gold = callGoldPrice();

        // ===== DB 저장 =====
        saveFinance("EXCHANGE", "USD/KRW", usd, now);
        saveFinance("EXCHANGE", "EUR/KRW", eur, now);
        saveFinance("EXCHANGE", "JPY/KRW", jpy, now);
        saveFinance("STOCK", "KOSPI", kospi, now);
        saveFinance("STOCK", "KOSDAQ", kosdaq, now);
        saveFinance("GOLD", "GOLD", gold, now);

        // ===== 결과 맵 구성 =====
        result.put("usd", usd);
        result.put("eur", eur);
        result.put("jpy", jpy);
        result.put("kospi", kospi);
        result.put("kosdaq", kosdaq);
        result.put("gold", gold);
        result.put("krRate", krRate);
        result.put("usRate", usRate);

        // ===== 변동폭 계산 =====
        addChangeInfo(result, "EXCHANGE", "USD/KRW", usd, "usd");
        addChangeInfo(result, "EXCHANGE", "EUR/KRW", eur, "eur");
        addChangeInfo(result, "EXCHANGE", "JPY/KRW", jpy, "jpy");
        addChangeInfo(result, "STOCK", "KOSPI", kospi, "kospi");
        addChangeInfo(result, "STOCK", "KOSDAQ", kosdaq, "kosdaq");
        addChangeInfo(result, "GOLD", "GOLD", gold, "gold");

        return result;
    }

    // ✅ 변동폭 계산
    private void addChangeInfo(Map<String, Object> result, String type, String code, double todayValue, String key) {
        List<FinanceEntity> history = repo.findTop2ByTypeAndCodeOrderByTimestampDesc(type, code);
        if (history.size() >= 2) {
            double yesterday = history.get(1).getValue();
            double diff = todayValue - yesterday;
            double diffRate = (yesterday != 0) ? (diff / yesterday * 100) : 0;

            result.put(key + "Change", diff);
            result.put(key + "ChangeRate", diffRate);
        } else {
            result.put(key + "Change", 0.0);
            result.put(key + "ChangeRate", 0.0);
        }
    }

    // ✅ DB 저장
    private void saveFinance(String type, String code, double value, LocalDateTime time) {
        repo.save(FinanceEntity.builder()
                .type(type).code(code).value(value).timestamp(time).build());
    }

    // ✅ 환율 (Frankfurter)
    private double callExchangeRate(String base, String target) {
        String url = String.format("https://api.frankfurter.app/latest?from=%s&to=%s", base, target);
        Map<?, ?> response = restTemplate.getForObject(url, Map.class);
        Map<String, Double> rates = (Map<String, Double>) response.get("rates");
        return rates.get(target);
    }

    // ✅ 주식 지수 (Yahoo Finance)
    private Map<String, Double> callStockIndex() {
        String url = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=^KS11,^KQ11";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        Map<String, Object> quoteResponse = (Map<String, Object>) response.get("quoteResponse");
        List<Map<String, Object>> results = (List<Map<String, Object>>) quoteResponse.get("result");

        double kospi = 0.0, kosdaq = 0.0;
        for (Map<String, Object> item : results) {
            if ("^KS11".equals(item.get("symbol"))) kospi = Double.parseDouble(item.get("regularMarketPrice").toString());
            if ("^KQ11".equals(item.get("symbol"))) kosdaq = Double.parseDouble(item.get("regularMarketPrice").toString());
        }

        Map<String, Double> result = new HashMap<>();
        result.put("kospi", kospi);
        result.put("kosdaq", kosdaq);
        return result;
    }

    // ✅ 금리 (API Ninjas)
    private double callInterestRate(String country) {
        try {
            String url = String.format("https://api.api-ninjas.com/v1/interestrate?country=%s", country);
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Api-Key", NINJA_KEY);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && body.containsKey("central_bank_rates")) {
                List<Map<String, Object>> rates = (List<Map<String, Object>>) body.get("central_bank_rates");
                if (!rates.isEmpty()) {
                    return Double.parseDouble(rates.get(0).get("rate_pct").toString());
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ 금리 API 오류 (" + country + "): " + e.getMessage());
        }
        return 0.0;
    }

    // ✅ 금 시세 (MetalsAPI)
    private double callGoldPrice() {
        try {
            String url = "https://metals-api.com/api/latest?access_key=" + METALS_API_KEY + "&base=USD&symbols=XAU";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            Map<String, Double> rates = (Map<String, Double>) response.get("rates");
            if (rates != null && rates.containsKey("XAU")) {
                return 1 / rates.get("XAU");
            }
        } catch (Exception e) {
            System.err.println("⚠️ 금 시세 API 오류: " + e.getMessage());
        }
        return 0.0;
    }

    // ✅ 과거 데이터 (그래프용)
    public List<FinanceEntity> getHistory(String type, String code, LocalDate start, LocalDate end) {
        return repo.findByTypeAndCodeAndTimestampBetween(
                type, code,
                start.atStartOfDay(),
                end.atTime(23, 59, 59)
        );
    }
}
