<?php

class EncryptPassword {
  
    public static function encrypt_password($pass) {
        $enc_password = password_hash($pass, PASSWORD_DEFAULT);
        return $enc_password;
    }

    public static function check_password($enc_pass, $pass) {
        if (password_verify($pass, $enc_pass)) {
            return true;
        }
        return false;
    }

}
?>

