package com.example.coindeskapi.service;

import com.example.coindeskapi.dto.ConvertedCurrency;
import com.example.coindeskapi.dto.ConvertedResponse;
import com.example.coindeskapi.model.Currency;
import com.example.coindeskapi.repository.CurrencyRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CoindeskServiceTest {

    @Autowired
    private CurrencyRepository currencyRepository;

    private CoindeskService coindeskService;

    @BeforeEach
    public void setup() {
        // 預先建立資料庫內幣別資料
        currencyRepository.deleteAll();
        currencyRepository.saveAll(Arrays.asList(
                new Currency("USD", "美元"),
                new Currency("GBP", "英鎊"),
                new Currency("EUR", "歐元"),
                new Currency("TWD", "台幣") // 故意加一筆 API 裡沒有的
        ));

        // 建立 service 並覆寫 getRawData() 模擬外部 API 回傳
        coindeskService = new CoindeskService(currencyRepository) {
            @Override
            public String getRawData() {
                return "{\n" +
                        "  \"time\": {\n" +
                        "    \"updatedISO\": \"2025-05-22T15:00:00+00:00\"\n" +
                        "  },\n" +
                        "  \"bpi\": {\n" +
                        "    \"USD\": {\n" +
                        "      \"rate_float\": 67000.0\n" +
                        "    },\n" +
                        "    \"GBP\": {\n" +
                        "      \"rate_float\": 52000.0\n" +
                        "    },\n" +
                        "    \"EUR\": {\n" +
                        "      \"rate_float\": 61000.0\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";
            }
        };
    }
    /*
    *    驗證項目	               說明
    *    更新時間格式正確	        與 mock API 時間一致
    *    結果長度與 DB 一致	        完全以資料庫為基準
    *    每筆 DB 幣別存在於結果中	 code 與 name 完整對應
    *    API 沒有的幣別匯率為 null	模擬整合行為完整性
     */
    @Test
    public void testGetConvertedData() {
        // 呼叫整合轉換方法
        ConvertedResponse response = coindeskService.getConvertedData();

        // 檢查整體物件不為 null，更新時間正確格式
        assertNotNull(response);
        assertEquals("2025/05/22 15:00:00", response.getUpdated());

        List<ConvertedCurrency> resultList = response.getCurrencyList();

        // 驗證整合結果數量應等於資料庫的資料數（預設是 4）
        List<Currency> allDbCurrencies = currencyRepository.findAll();
        assertEquals(allDbCurrencies.size(), resultList.size(), "整合資料應包含所有資料庫幣別");

        // 每一筆資料庫幣別都應在結果中找得到
        for (Currency dbCurrency : allDbCurrencies) {
            ConvertedCurrency matched = resultList.stream()
                    .filter(c -> c.getCode().equals(dbCurrency.getCode()))
                    .findFirst()
                    .orElse(null);

            assertNotNull(matched, "找不到資料庫幣別：" + dbCurrency.getCode());
            assertEquals(dbCurrency.getName(), matched.getName(), "幣別名稱應一致：" + dbCurrency.getCode());

            // 特別確認 TWD 是 null 匯率（API 沒給）
            if (dbCurrency.getCode().equals("TWD")) {
                assertNull(matched.getRate(), "TWD 沒有對應匯率，應為 null");
            }
        }
    }
}