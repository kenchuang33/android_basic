<?php
    $ip =$_SERVER["REMOTE_ADDR"];
    $link = mysqli_connect("localhost","root","","hwdb") or die("Error with MySQL connection");
    $link ->set_charset("UTF8");

// 1. 從 $_POST 中取出資料
$pname = $_POST["Name"];
$pgender = $_POST["Gender"];
$pweight = $_POST["Weight"];
$pheight = $_POST["Height"];
$page = $_POST["Age"];
$pbmi = $_POST["bmi"];
$pbmr = $_POST["bmr"];


$result = 
    $link -> query("INSERT INTO `BMRdetails`(`Name`,`Gender`,`Weight`,`Height`,`Age`,`bmi`,`bmr`) VALUES ('$pname','$pgender','$pweight','$pheight','$page','$pbmi','$pbmr');");
    
if ($result) {
    echo "Insert successful!";
} else {
    echo "Error: " . $stmt->error;
}

// 關閉預備語句和資料庫連線
$stmt->close();
$link->close();
?>

