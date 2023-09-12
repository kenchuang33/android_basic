<?php
// 连接到 MySQL 数据库
$conn = new mysqli("localhost", "root", "", "hwdb");

// 检查连接是否成功
if ($conn->connect_error) {
    die("连接失败: " . $conn->connect_error);
}

// 从 POST 请求中获取要更新的记录的数据
$Name = $_POST['Name']; // 姓名
$Gender = $_POST['Gender']; // 性别
$Age = $_POST['Age']; // 年龄
$Height = $_POST['Height']; // 身高
$Weight = $_POST['Weight']; // 体重
$BMR = $_POST['BMR']; // BMR
$BMI = $_POST['BMI']; // BMI

// 构建 SQL 更新语句
$sql = "UPDATE BMRdetails SET Gender = '$Gender', Age = $Age, Height = $Height, Weight = $Weight, bmr = $BMR, bmi = $BMI WHERE Name = '$Name'";

// 执行更新操作
if ($conn->query($sql) === TRUE) {
    echo "记录已成功更新";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

// 关闭数据库连接
$conn->close();
?>
