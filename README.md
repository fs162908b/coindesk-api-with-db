# 💰 Coindesk 整合幣別查詢系統

本專案為 Spring Boot 專案，整合 Coindesk 外部匯率 API 與本地幣別資料庫，提供幣別新增、刪除、查詢、更新與轉換功能。前端使用純 HTML + JavaScript 操作，資料存於 H2 記憶體資料庫中。

## 🧰 專案技術

- Java 8
- Spring Boot 2.7.x
- Spring Data JPA
- H2 Database
- JavaScript + Fetch API
- Maven Wrapper (`mvnw.cmd`)

## 📦 功能介紹

- 呼叫 [Coindesk API](https://kengp3.github.io/blog/coindesk.json) 並轉換為符合資料庫格式
- 幣別資訊 CRUD 功能（新增 / 查詢 / 更新 / 刪除）
- 統一後端 API 入口 (`/currencies/action`)
- 顯示外部 JSON 原始資料
- 顯示整合後資料（以資料庫為基準，對照外部匯率）
- 支援幣別代碼更新（透過刪除＋新增達成）

## 🖥️ 執行方式

請使用內建 Maven Wrapper 啟動：

### Windows

```bash
.\mvnw.cmd spring-boot:run
```

### macOS / Linux

```bash
./mvnw spring-boot:run
```

啟動後請開啟瀏覽器：

```
http://localhost:8080/index.html
```

## 📄 API 說明

所有 CRUD 功能皆透過 `/currencies/action` API 完成：

### POST `/currencies/action`

| 欄位      | 說明                           |
|-----------|--------------------------------|
| `action`  | create / read / update / delete / list |
| `data`    | 對應操作所需資料               |

#### 範例：新增幣別

```json
{
  "action": "create",
  "data": {
    "code": "TWD",
    "name": "台幣"
  }
}
```

## 🔌 外部 API 整合

- `/coindesk/raw`：取得 Coindesk 原始 JSON
- `/coindesk/converted`：轉換後資料（幣別、名稱、匯率、更新時間）

資料顯示以資料庫為主，就算無匯率也會顯示。

## 🧪 測試

```java
@SpringBootTest
public class CoindeskServiceTest {
    @Autowired
    private CoindeskService coindeskService;

    @Test
    public void testGetConvertedData() {
        ConvertedResponse response = coindeskService.getConvertedData();
        assertNotNull(response);
        // 其他驗證
    }
}
```

## 🗃️ 初始化資料（H2）

在 `src/main/resources/data.sql` 、`src/main/resources/schema.sql`撰寫：

```sql
CREATE TABLE IF NOT EXISTS currency (
    code VARCHAR(10) PRIMARY KEY,
    name VARCHAR(50)
);
INSERT INTO currency (code, name) VALUES ('USD', '美元');
INSERT INTO currency (code, name) VALUES ('TWD', '台幣');
INSERT INTO currency (code, name) VALUES ('EUR', '歐元');
```

並於 `application.properties` 加入：

```properties
spring.sql.init.schema-locations=classpath:schema.sql
spring.datasource.initialization-mode=always
spring.datasource.data=classpath:data.sql
```

## 📜 授權 License

MIT License.