<?php
    $ip =$_SERVER["REMOTE_ADDR"];
    $link = mysqli_connect("localhost","root","","hwdb") or die("Error with MySQL connection");
    $link ->set_charset("UTF8");

    $result = $link -> query("SELECT * FROM `BMRdetails`");


    $output=[];
    while($row = $result -> fetch_assoc() )
    {
        $output[]=$row;
    }
    $link -> close();
    echo json_encode($output);
?>