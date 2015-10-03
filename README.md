# Android-Tracker
Layanan Informasi Tracking dengan Android

#Contoh Respon server 
contoh dumy respon data, menggunakan php >= 5.4 
untuk melakukan pengaturan alamat server Anda dapat melihatnya disini 
https://github.com/chabibnr/Info-Pengiriman/blob/master/app/src/main/java/net/chabibnr/tracker/Info.java#L29
~~~
<?php
date_default_timezone_set("Asia/Jakarta");
header('Content-Type: application/json');
$courier = array('JNE', 'TIKI', 'POS Indonesia', 'WAHANA');
$response = [
    'status' => 200,
    'data' => [
        [
            'awb' => 'AWBNO12345678_' . rand(0, 9),
            'courier' => $courier[rand(0, 3)],
            'origin' => 'PEDURUNGAN, SEMARANG',
            'destination' => 'TAMANSARI MAJAPAHIT, SEMARANG',
            'shipper' => 'CHABIB',
            'consignee' => 'NUROZAK',
            'status' => 'MANIFEST',
            'service' => 'REG',
            'last_update' => date("Y-m-d H:i:s"),
            'shipment_status' => []
        ]
    ],

];

echo json_encode($response);
