// 初始化：載入幣別清單
document.addEventListener("DOMContentLoaded", function () {
    fetch('/currencies/action', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ action: 'list', data: {} })
    })
    .then(response => response.json())
    .then(data => {
        document.querySelectorAll('.currency-select').forEach(select => {
            select.innerHTML = '';
            data.forEach(currency => {
                const option = document.createElement("option");
                option.value = currency.code;
                option.textContent = currency.name;
                select.appendChild(option);
            });
        });
    });
});

// 查詢幣別
function queryCurrency(event) {
    event.preventDefault();
    const code = document.getElementById("queryCurrencySelect").value;
    const payload = { action: 'read', data: { code } };

    if (!code) {
        alert("請選擇要查詢的幣別");
        return false;
    }

    fetch('/currencies/action', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
    .then(response => {
        if (!response.ok) throw new Error("查詢失敗");
        return response.json();
    })
    .then(data => {
        document.getElementById("currencyDetail").innerHTML = `
            <h2>幣別資訊</h2>
            <p><strong>名稱：</strong>${data.name}</p>
            <p><strong>幣別代碼：</strong>${data.code}</p>`;
    })
    .catch(error => alert(error.message + "\n\n" + JSON.stringify(payload, null, 4)));

    return false;
}

// 新增幣別
function addCurrency(event) {
    event.preventDefault();
    const code = document.getElementById("addCurrencyCode").value.trim();
    const name = document.getElementById("addCurrencyName").value.trim();
    const payload = { action: 'create', data: { code, name } };

    if (!code || !name) {
        alert("請輸入正確的幣別代碼與名稱");
        return false;
    }

    fetch('/currencies/action', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
    .then(response => {
        if (response.ok) {
            alert("新增成功\n\n" + JSON.stringify(payload, null, 4));
            window.location.reload();
        } else if (response.status === 409) {
            alert("幣別代碼已存在\n\n" + JSON.stringify(payload, null, 4));
        } else {
            alert("新增失敗\n\n" + JSON.stringify(payload, null, 2));
        }
    })
    .catch(error => alert("發生錯誤\n\n" + JSON.stringify(payload, null, 4)));

    return false;
}

// 刪除幣別
function deleteCurrency(event) {
    event.preventDefault();
    const code = document.getElementById("deleteCurrencyCode").value;
    const payload = { action: 'delete', data: { code } };

    if (!code.trim()) {
        alert("請選擇要刪除的幣別");
        return false;
    }

    fetch('/currencies/action', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
    .then(response => {
        if (response.status === 204) {
            alert("刪除成功\n\n" + JSON.stringify(payload, null, 4));
            window.location.reload();
        } else {
            alert("刪除失敗或幣別不存在\n\n" + JSON.stringify(payload, null, 4));
        }
    })
    .catch(error => alert("發生錯誤\n\n" + JSON.stringify(payload, null, 4)));

    return false;
}

// 更新幣別
function updateCurrency(event) {
    event.preventDefault();
    const originalCode = document.getElementById("updateCurrencySelect").value;
    const newCode = document.getElementById("updateCurrencyCode").value.trim();
    const newName = document.getElementById("updateCurrencyName").value.trim();

    // 若兩者皆為空，或任一有前後空白 → 不處理
    if ((!newCode && !newName) || /^\s|\s$/.test(newCode) || /^\s|\s$/.test(newName)) {
        alert("請輸入至少一個正確的新代碼或名稱");
        return false;
    }

    const data = {};
    if (newCode) data.code = newCode;
    if (newName) data.name = newName;

    // ✅ 若代碼沒變（或沒輸入新代碼），代表只是改名稱
    if (!newCode || originalCode === newCode) {
        data.code = originalCode; // 仍需指定原始代碼作為識別用
        const payload = { action: 'update', data };

        fetch("/currencies/action", {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        })
        .then(response => {
            if (response.ok) {
                alert("更新成功\n\n" + JSON.stringify(payload, null, 4));
                window.location.reload();
            } else {
                alert("更新失敗\n\n" + JSON.stringify(payload, null, 4));
            }
        })
        .catch(error => alert("發生錯誤\n\n" + JSON.stringify(payload, null, 4)));

        return false;
    }

    const createPayload = {
        action: 'create',
        data: { code: newCode, name: newName || "(未命名)" }
    };

    const deletePayload = {
        action: 'delete',
        data: { code: originalCode }
    };

    // ✅ 若代碼有變，走「新增新代碼 → 刪除舊代碼」流程
    fetch("/currencies/action", {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ action: 'read', data: { code: newCode } })
    })
    .then(response => {
        if (response.ok) throw new Error("新代碼已存在，無法使用");

        return fetch("/currencies/action", {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(createPayload)
        });
    })
    .then(response => {
        if (!response.ok) throw new Error("新增新代碼失敗");

        return fetch("/currencies/action", {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(deletePayload)
        });
    })
    .then(response => {
        if (!response.ok) throw new Error("刪除舊代碼失敗");

        alert("代碼與名稱更新成功\n新增: " + newCode + "\n刪除: " + originalCode + "\n" + JSON.stringify(createPayload, null, 4) + "\n" + JSON.stringify(deletePayload, null, 4));
        window.location.reload();
    })
    .catch(error => alert(error.message));

    return false;
}

function showRawApi() {
    fetch('/coindesk/raw')
        .then(response => {
            if (!response.ok) throw new Error("無法取得外部 API 資料");
            return response.json();
        })
        .then(data => {
            const output = document.getElementById("rawApiOutput");
            output.textContent = JSON.stringify(data, null, 4);
        })
        .catch(error => {
            alert("錯誤：" + error.message);
        });
}

function loadIntegratedData() {
    fetch('/coindesk/converted')
        .then(response => {
            if (!response.ok) throw new Error("取得整合資料失敗");
            return response.json();
        })
        .then(data => {
            const list = data.currencyList || [];

            // 假設 data.updated 是 UTC 時間格式為 "2024/09/02 07:07:20"
            const parts = data.updated.split(/[/ :]/);
            const utcTime = new Date(Date.UTC(parts[0], parts[1] - 1, parts[2], parts[3], parts[4], parts[5]));
            const localTimeStr = utcTime.toLocaleString();

            let html = `
                <h3>UTC 時間（後端提供）：${data.updated}</h3>
                <h3>瀏覽器所在時區的時間（瀏覽器轉換）：${localTimeStr}</h3>
            `;

            html += "<table border='1' cellpadding='5'><tr><th>幣別代碼</th><th>名稱</th><th>匯率</th></tr>";

            list.forEach(item => {
                html += `
                    <tr>
                        <td>${item.code}</td>
                        <td>${item.name}</td>
                        <td>${item.rate == null ? '無法取得匯率' : item.rate}</td>
                    </tr>
                `;
            });

            html += "</table>";
            document.getElementById("integratedData").innerHTML = html;
        })
        .catch(err => {
            alert(err.message);
        });
}