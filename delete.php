<?php
// 连接到 MySQL 数据库
$conn = new mysqli("localhost", "root", "", "hwdb");

// 检查连接是否成功
if ($conn->connect_error) {
    die("连接失败: " . $conn->connect_error);
}

// 从 POST 请求中获取要删除的记录的唯一标识符（假设为 id）
$Name = $_POST['Name'];

// 构建 SQL 删除语句
$sql = "DELETE FROM BMRdetails WHERE Name = '$Name'";

// 执行删除操作

if ($conn->query($sql) === TRUE) {
    echo "刪除成功";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

// 关闭数据库连接
$conn->close();
?>
