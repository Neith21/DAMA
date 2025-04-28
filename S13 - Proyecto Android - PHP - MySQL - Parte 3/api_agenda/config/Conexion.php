<?php

class Conexion {
    private $host = "localhost";
    private $db_name = "miagenda";
    private $username = "root";
    private $password = "123123";
    public $idConexion;
    
    public function __construct() {
        try {
            $this->idConexion = new PDO(
                "mysql:host=" . $this->host . ";dbname=" . $this->db_name,
                $this->username,
                $this->password,
                [PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION]
            );
            $this->idConexion->exec("SET NAMES utf8");
        } catch(PDOException $e) {
            throw new Exception("Error de conexión: " . $e->getMessage());
        }
    }
}