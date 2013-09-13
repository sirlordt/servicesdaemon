<?php

    //Using CURL php library 

    $ch = curl_init();

    curl_setopt( $ch, CURLOPT_URL, "https://127.0.0.1:8181/DBServices" );
    curl_setopt( $ch, CURLOPT_POST, 1 );
    curl_setopt( $ch, CURLOPT_SSL_VERIFYPEER, false ); //Warning read this for proper fix http://unitstep.net/blog/2009/05/05/using-curl-in-php-to-access-https-ssltls-protected-sites/    
    curl_setopt( $ch, CURLOPT_SSL_VERIFYHOST, 2 );
    curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
    curl_setopt( $ch, CURLOPT_POSTFIELDS, "ServiceName=System.Enum.Services&ResponseFormat=JAVA-XML-WEBROWSET&ResponseFormatVersion=1.0" );

    $result = curl_exec( $ch );

    curl_close( $ch ); 

    echo $result;

?>

