# PowerShell版本的测试脚本

$API_BASE = "http://localhost:8080/api"

function Test-Api {
    param (
        [string]$description,
        [string]$method,
        [string]$endpoint,
        [string]$data
    )

    Write-Host "测试: $description"
    Write-Host "请求: $method $endpoint"
    Write-Host "数据: $data"

    $headers = @{
        "Content-Type" = "application/json"
    }

    try {
        $response = Invoke-WebRequest `
            -Method $method `
            -Uri "$API_BASE$endpoint" `
            -Headers $headers `
            -Body $data `
            -UseBasicParsing

        Write-Host "状态码: $($response.StatusCode)"
        Write-Host "响应: $($response.Content)"
    }
    catch {
        Write-Host "错误: $($_.Exception.Message)"
    }
    Write-Host "----------------------------------------"
}

# 1. 测试正常创建用户
Test-Api -description "正常创建用户" -method "POST" -endpoint "/users" -data @"
{
    "name": "正常用户",
    "gender": "MALE",
    "password": "Test123456"
}
"@

Start-Sleep -Seconds 1

# 2. 测试创建重复用户名
Test-Api -description "创建重复用户名" -method "POST" -endpoint "/users" -data @"
{
    "name": "正常用户",
    "gender": "MALE",
    "password": "Test123456"
}
"@

Start-Sleep -Seconds 1

# 3. 测试密码格式错误
Test-Api -description "密码格式错误" -method "POST" -endpoint "/users" -data @"
{
    "name": "密码错误用户",
    "gender": "MALE",
    "password": "123"
}
"@

Start-Sleep -Seconds 1

# 4. 测试性别格式错误
Test-Api -description "性别格式错误" -method "POST" -endpoint "/users" -data @"
{
    "name": "性别错误用户",
    "gender": "UNKNOWN",
    "password": "Test123456"
}
"@ 