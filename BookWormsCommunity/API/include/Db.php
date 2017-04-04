<?php

include_once 'Config.php';

class DataBaseConnect {
    
    private $connection;
    
    public function __construct() {
        
    }
    
    public function connect() {
        
 
        $this->connection = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME)
            or die("Error connection" . mysqli_connect_error());
            mysqli_set_charset($this->connection, "UTF8");
        return $this->connection;
    }
}

?>